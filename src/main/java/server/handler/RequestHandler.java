package server.handler;

import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import server.NormalServer;
import server.Server;
import server.redirect.RedirectList;
import util.FileTable;
import util.FileUtil;
import util.MIMETypes;
import util.StatusCodeAndPhrase;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static server.ServerMain.*;

// ckw: client包下的Responsehandler处理的是对从服务器端收到的相应报文做处理，RequestHandler处理的是对即将传递给服务器端的请求报文做处理

public class RequestHandler extends Thread implements Handler {
    Socket socket;
    private boolean isDown = false; // 模拟服务器挂掉的情况
    private static RedirectList redirectList = RedirectList.getRedirectList();
    private static MIMETypes MIMEList = MIMETypes.getMIMELists();
    private static StatusCodeAndPhrase statusCodeList = StatusCodeAndPhrase.getStatusCodeList();
    private boolean isTimeout = false;

    private static TimerTask timerTask = null;

    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    private FileTable getFileTable;

    public RequestHandler(Socket socket) {
        this.socket = socket;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToClient = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        getFileTable = new FileTable();
    }

    @Override
    public void run() {
        // readRequest() -> handle() -> sendResponse()
        while (true) {
            if (isTimeout) {
                try {
                    System.out.println("Timeout, Socket closed");
                    socket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            HttpRequest httpRequest = null;
            try {
                httpRequest = readRequest(); //todo 修改  第二次读取时在这里会报错
            } catch (IOException e) {
                System.out.println("readRequest() failed, try again");
                return;
            }

            // handle persistent connection
            if (httpRequest.getHeader().get("Keep-Alive") != null) {
                long timeout = Long.parseLong(httpRequest.getHeader().get("Keep-Alive").substring(8));
                if (timerTask != null) timerTask.cancel();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        isTimeout = true;
                    }
                };
                Timer timer = NormalServer.timer;
                timer.schedule(timerTask, timeout * 1000L);
            }

            HttpResponse httpResponse = handle(httpRequest);
            if (httpResponse != null) sendResponse(httpResponse);

            // non-persistent connection, break out
            if (httpRequest.getHeader().get("Connection") == null || !"Keep-Alive".equals(httpRequest.getHeader().get("Connection"))) {
                System.out.println("Non-persistent connection closed....");
                break;
            }
        }

    }

    private HttpRequest readRequest() throws IOException {
        // TODO: refactor this function

        // #1. reader requestLine and header
        // phrase httpRequest
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = inFromClient.readLine()) != null) {
            sb.append(line).append(System.lineSeparator());
            if (line.isEmpty()) {
                break;
            }
        }
        if (sb.toString().equals("")) return null;
        String request = sb.toString();
        String[] headers = request.split(System.lineSeparator()); // TODO: bad '\n'
        String startLine = headers[0];
        String[] startLineSplit = startLine.split("\\s+");
        String method = startLineSplit[0];
        String uri = startLineSplit[1];
        String version= startLineSplit[2];

        RequestLine requestLine = new RequestLine(method, uri);
        Header header = new Header();
        for (int i = 1; i < headers.length; i++) {
            String singleItem = headers[i];
            String[] temp = singleItem.split(":");
            header.put(temp[0], temp[1].trim());
        }

        byte[] bodyData = new byte[0];
        // #2. read body
        if ("POST".equals(method)) {
            // only POST has body
            int cnt = Integer.parseInt(header.getHeader().get("Content-Length")), cur = 0;
            sb = new StringBuilder();
            while ((line = inFromClient.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
                cur += line.length();
                if (cur >= cnt) break;
            }
            String bodyStr = sb.toString();
            bodyData = bodyStr.getBytes();
        }
        Body body = new Body(bodyData);

        System.out.println("request is :");
        System.out.println(request);

        HttpRequest httpRequest = new HttpRequest(requestLine, header, body);
        return httpRequest;
    }

    private byte[] getBodyDataFromFile(String location) {
        byte[] bodyData = new byte[0];
        try {
            bodyData = FileUtil.readFromFile(location);
        } catch (FileNotFoundException ex) {
            System.out.println(location + "文件未找到");
            return null;
        }
        return bodyData;
    }

    private HttpResponse getHandler(HttpRequest httpRequest) {
        boolean persistent = "Keep-Alive".equals(httpRequest.getHeader().get("Connection"));
        HttpResponse httpResponse = null;
        int statusCode;
        String location;
        byte[] bodyData;

        String uri = httpRequest.requestLine.requestURI;
        String redirectQuery = redirectList.query(uri);
        if (!redirectQuery.equals("")) {
            // 301 / 302
            statusCode = Integer.parseInt(redirectQuery.substring(0, 3));
            location = BIND_DIR + redirectQuery.substring(3);
        } else {
            statusCode = 200;
            location = BIND_DIR + uri;

            // 304
            Long getTime = getFileTable.getModifiedTime(location);
            Long modifyTime = Server.modifiedFileTable.getModifiedTime(location);
            assert (modifyTime != -1);
            if (getTime >= modifyTime) {//todo modifyTime一直为-1
                statusCode = 304;
                location = BIND_DIR + NOT_MODIFIED_RES;
            }
            if (statusCode != 304) getFileTable.modify(location);
        }
        bodyData = getBodyDataFromFile(location);
        if (bodyData == null) {
            statusCode = 404;
            location = BIND_DIR + NOT_FOUND_RES;
            bodyData = getBodyDataFromFile(location);
            assert (bodyData != null);
        }
        Long modifiedTime = getFileTable.getModifiedTime(location);
        httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData),modifiedTime); // TODO
        return httpResponse;
    }

    private HttpResponse postHandler(HttpRequest httpRequest) {
        boolean persistent = "Keep-Alive".equals(httpRequest.getHeader().get("Connection"));
        int statusCode;
        String location;
        byte[] bodyData;
        HttpResponse httpResponse;

        String uri = httpRequest.requestLine.requestURI;
        location = BIND_DIR + uri;
        String contentType = httpRequest.getHeader().get("Content-Type");
        String contentLength = httpRequest.getHeader().get("Content-Length");
        assert (contentType != null && contentLength != null);
        if (contentType.indexOf(';') != -1) {
            // Content-Type: multipart/form-data
            String boundary = contentType.split(";")[1].trim();
            contentType = contentType.split(";")[0].trim();
            assert ("multipart/form-data".equals(contentType));
            assert (boundary.startsWith("boundary="));
            boundary = boundary.substring("boundary=".length());
            // TODO
            String[] bodyLines = new String(httpRequest.messageBody.getBody()).split(System.lineSeparator());
            assert (boundary.equals(bodyLines[0]));
            for (int i = 1; i < bodyLines.length; i++) {

            }

            statusCode = 200;
            location = BIND_DIR + POST_SUCCESS_RES;
            bodyData = getBodyDataFromFile(location);
            assert (bodyData != null);
        } else {
            // bytes[]
            int length = Integer.parseInt(contentLength);
            byte[] fileData = new byte[length];
            for (int i = 0; i < length; i++) {
                fileData[i] = httpRequest.messageBody.getBody()[i];
            }
            try {
                FileUtil.save(fileData, location);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Fail to save file");
            }
            statusCode = 200;
            location = BIND_DIR + POST_SUCCESS_RES; // !warning: reuse variable 'location', bad practice
            bodyData = getBodyDataFromFile(location);
            assert (bodyData != null);
        }
        Long modifiedTime = getFileTable.getModifiedTime(location);
        httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData),modifiedTime);
        return httpResponse;
    }

    private HttpResponse handle(HttpRequest httpRequest) {
        HttpResponse httpResponse = null;
        if (isDown) {
            boolean persistent = "Keep-Alive".equals(httpRequest.getHeader().get("Connection"));
            int statusCode = 500;
            String location = BIND_DIR + SERVER_ERROR_RES;
            byte[] bodyData = getBodyDataFromFile(location);
            assert (bodyData != null);
            httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData));
            return httpResponse;
        }
        String method = httpRequest.getRequestLine().method;
        if ("GET".equals(method)) {
            httpResponse = getHandler(httpRequest);
        } else if ("POST".equals(method)) {
            httpResponse = postHandler(httpRequest);
        } else {
            boolean persistent = "Keep-Alive".equals(httpRequest.getHeader().get("Connection"));
            int statusCode = 405;
            String location = BIND_DIR + METHOD_NOT_ALLOWED_RES;
            byte[] bodyData = getBodyDataFromFile(location);
            assert (bodyData != null);
            httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData));
        }

        return httpResponse;
    }

    private void sendResponse(HttpResponse httpResponse) {
        System.out.println("---->>>>send response<<<<----");
        try {
            outToClient.write(httpResponse.toBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            outToClient.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("---->>>>response sended<<<<----");
    }

}

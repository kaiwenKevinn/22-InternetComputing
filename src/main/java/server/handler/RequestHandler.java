package server.handler;

import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import server.HttpResponseReturnValue;
import server.NormalServer;
import server.Server;
import server.redirect.RedirectList;
import server.usrServices.UserServiceProvider;
import server.usrServices.UserServicesList;
import util.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import static server.ServerMain.*;



public class RequestHandler extends Thread  {
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

    /**
     * readRequest() -> handle() -> sendResponse()
     */
    @Override
    public void run() {
        while (true) {
            if (isTimeout) {
                try {
                    TextDecoration.printRed("Timeout, Socket closed");
                    socket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            HttpRequest httpRequest = null;
            try {
                //读取请求
                httpRequest = readRequest();
            } catch (IOException e) {
                TextDecoration.printRed("readRequest() failed, try again");
                return;
            }

            if (httpRequest != null) {
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
                    TextDecoration.printRed("Non-persistent connection closed....");
                    break;
                }
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
        String[] headers = request.split(System.lineSeparator());
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
                // a char accounts for 2 bytes in Java
                cur += line.getBytes().length + System.lineSeparator().getBytes().length;
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

    /**
     * @param location
     * 以二进制方式从指定位置读取文件
     * @return
     */
    private byte[] getBodyDataFromFile(String location) {
        byte[] bodyData = new byte[0];
        try {
            bodyData = FileUtil.readFromFile(location);
        } catch (FileNotFoundException ex) {
            TextDecoration.printRed(location + "文件未找到");
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
            if (getTime >= modifyTime) {
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
        Long modifiedTime = Server.modifiedFileTable.getModifiedTime(location);
        httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData),modifiedTime);
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

        String[] args = new String[3];
        if (contentType.indexOf(';') != -1) {
            // Content-Type: multipart/form-data
            String boundary = contentType.split(";")[1].trim();
            contentType = contentType.split(";")[0].trim();
            assert ("multipart/form-data".equals(contentType));
            assert (boundary.startsWith("boundary="));
            boundary = boundary.substring("boundary=".length());
            //  only support /uploadFile, 2 args
            String[] bodyLines = new String(httpRequest.messageBody.getBody()).split(System.lineSeparator());
            assert (("--" + boundary).equals(bodyLines[0]));
            args[0] = bodyLines[3];
            args[1] = "";
            for (int i = 7; i < bodyLines.length; i++) {
                if (bodyLines[i].equals("--" + boundary + "--")) break;
                args[1] += bodyLines[i] + System.lineSeparator();
            }
        } else {
            // Content-Type: application/x-www-form-urlencoded
            assert ("application/x-www-form-urlencoded".equals(contentType));
            int length = Integer.parseInt(contentLength);
            byte[] requestBodyData = new byte[length];
            for (int i = 0; i < length; i++) {
                requestBodyData[i] = httpRequest.messageBody.getBody()[i];
            }
            String content = new String(requestBodyData, StandardCharsets.UTF_8);
            String[] contents = content.split("&");

            for (int i = 0; i < Math.min(3, contents.length); i++) {
                args[i] = contents[i].split("=")[1];
            }
        }
        boolean handled = false;
        HttpResponseReturnValue retVal = null;
        for (UserServiceProvider service : Server.services.getServiceProviders()) {
            if (service.bindUri.equals(uri)) {
                handled = true;
                retVal = service.handle(args[0], args[1], args[2]);
                break;
            }
        }
        if (!handled) {
            statusCode = 404;
            location = BIND_DIR + NOT_FOUND_RES;
        } else {
            statusCode = retVal.statusCode;
            location = retVal.location;
        }
        bodyData = getBodyDataFromFile(location);
        httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData));
        return httpResponse;
    }

    /**
     * @param httpRequest
     * GET请求用getHandler处理 POST请求用postHandler处理 其它请求均返回405
     * @return
     */
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
      TextDecoration.printGreen("---->>>>send response<<<<----");
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
        TextDecoration.printGreen("---->>>>response sended<<<<----");
    }

}

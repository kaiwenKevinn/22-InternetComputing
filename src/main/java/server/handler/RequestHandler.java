package server.handler;

import client.NormalClient;
import com.sun.net.httpserver.HttpServer;
import message.Body;
import message.header.Header;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import message.response.ResponseLine;
import server.NormalServer;
import server.redirect.RedirectList;
import util.FileUtil;
import util.MIMETypes;
import util.StatusCodeAndPhrase;

import java.io.*;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import static server.ServerMain.*;
import static util.InputStreamHelper.getResAsStream;


// czh: 我是认为这部分的职责应该交给Server，但是要多线程，所以就先这么试一下，有更好的方法吗？
// ckw: client包下的Responsehandler处理的是对从服务器端收到的相应报文做处理，RequestHandler处理的是对即将传递给服务器端的请求报文做处理

public class RequestHandler extends Thread implements Handler {
    Socket socket;
    private boolean isDown = false; // 模拟服务器挂掉的情况
    private static RedirectList redirectList = RedirectList.getRedirectList();
    private static MIMETypes MIMEList = MIMETypes.getMIMELists();
    private static StatusCodeAndPhrase statusCodeList = StatusCodeAndPhrase.getStatusCodeList();
    private boolean isTimeout = false;
    private TimerTask timerTask = null;

    public RequestHandler(Socket socket) {
        this.socket = socket;
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
                httpRequest = readRequest();
            } catch (IOException e) {
                System.out.println("readRequest() failed, try again");
                continue;
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
                break;
            }
            System.out.println("Non-persistent connection closed....");
        }

    }

    private HttpRequest readRequest() throws IOException {
        // phrase httpRequest
        InputStream is = socket.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int len;
        while ((len = is.read(buffer)) > 0) {
            bos.write(buffer, 0, len);
            if (len < 2048) break;
        }
        String request = new String(bos.toByteArray());
        String method = request.split("\\s+")[0];
        String uri = request.split("\\s+")[1];
        String version = request.split("\\s+")[2];
        String[] HeaderSplit = request.split(System.lineSeparator());

        RequestLine requestLine = new RequestLine(method, uri); //default get
        Header header = new Header();
        Body body = new Body();//construct Request
        for (int i = 1; i < HeaderSplit.length; i++) {
            String singleItem = HeaderSplit[i];
            String[] temp = singleItem.split(":");
            header.put(temp[0], temp[1].trim());
        }

        System.out.println("request is :");
        System.out.println(request);

        HttpRequest httpRequest = new HttpRequest(requestLine, header, body);
        return httpRequest;
    }

    private HttpResponse handle(HttpRequest httpRequest) {
        boolean persistent = "Keep-Alive".equals(httpRequest.getHeader().get("Connection"));
        HttpResponse httpResponse = null;
        String method = httpRequest.getRequestLine().method;
        if ("GET".equals(method)) {
            int statusCode = 0;
            String location = "";
            if (isDown) {
                statusCode = 500;
                location = BIND_DIR + SERVER_ERROR_RES;
            } else {
                String uri = httpRequest.requestLine.requestURI;
                String redirectQuery = redirectList.query(uri);
                if (!redirectQuery.equals("")) {
                    // 301 / 302
                    statusCode = Integer.parseInt(redirectQuery.substring(0, 3));
                    location = BIND_DIR + redirectQuery.substring(3);
                } else {
                    statusCode = 200;
                    location = BIND_DIR + uri;
                }
            }

            byte[] bodyData = new byte[0];
            String trueUri = location.substring(location.lastIndexOf("/"));
            try {
                bodyData = FileUtil.readFromFile(location);
            } catch (FileNotFoundException ex) {
                System.out.println(location + "文件未找到");
                statusCode = 404;
                location = BIND_DIR + NOT_FOUND_RES;
                try {
                    trueUri = location.substring(location.lastIndexOf("/"));
                    bodyData = FileUtil.readFromFile(location);
                } catch (FileNotFoundException ex_) {
                    // impossible
                    assert (false);
                }
            }

            httpResponse = new HttpResponse(statusCode, location, persistent, new Body(bodyData)); // TODO
        } else if ("POST".equals(method)) {
            // TODO
        } else {
            System.out.println("Server does not support this method.");
        }

        return httpResponse;
    }

    private void sendResponse(HttpResponse httpResponse) {
        System.out.println("---->>>>send response<<<<----");
        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        PrintStream ps = new PrintStream(os);
        try {
            ps.write(httpResponse.toBytes());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try {
            os.flush();
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("---->>>>response sended<<<<----");
    }

}

package server.handler;

import com.sun.net.httpserver.HttpServer;
import message.Body;
import message.header.Header;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import message.response.ResponseLine;
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
    private Timer timer = new Timer("timer");
    private boolean isTimeout;
    private TimerTask task;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // readRequest() -> handle() -> sendResponse()
        while(true) {
            if(isTimeout){
                try {
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
                continue;
                //System.out.println("Cannot read Request");
            }

            //handle persistent connection
            if(httpRequest.getHeader().get("Keep-Alive") != null){
                long timeout = Long.parseLong(httpRequest.getHeader().get("Keep-Alive").substring(8));
                if(task != null)task.cancel();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        isTimeout = true;
                    }
                };
                timer.schedule(task, timeout * 1000L);
            }

            handle(httpRequest);
            System.out.println("---->>>>send finished<<<<----");

            //non-persistent connection, break out
            if(httpRequest.getHeader().get("Connection") == null || !"Keep-Alive".equals(httpRequest.getHeader().get("Connection"))){
                break;
            }
        }

        System.out.println("non-persistent connection closed....");
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
        String version=request.split("\\s+")[2];
        String[] HeaderSplit = request.split(System.lineSeparator());

        RequestLine requestLine=new RequestLine(method,uri); //default get
        Header header=new Header();
        Body body=new Body();//construct Request
        for(int i=1;i<HeaderSplit.length;i++){
            String singleItem=HeaderSplit[i];
            String[] temp = singleItem.split(":");
            header.put(temp[0],temp[1].trim());
        }

        System.out.println("request is :");
        System.out.println(request);

        HttpRequest httpRequest=new HttpRequest(requestLine,header,body);
        return httpRequest;

    }

    private void handle(HttpRequest httpRequest) {

        // generate httpResponse and error handling
        // 初始化变量
        String MIMEType;
        System.out.println("---->>>>send response<<<<----");
        ResponseLine responseLine=null;
        ResponseHeader header=null;
        Body body=new Body();
        String uri=httpRequest.requestLine.requestURI;
//        byte[] data = new byte[0];
        InputStream in = null;
        int statusCode=0;
        boolean persistent = "Keep-Alive".equals(httpRequest.getHeader().get("Connection"));

        if (isDown) {
             statusCode = 500;
             responseLine.statusCode=500;
             responseLine.description="服务器已经关闭";
             String location=BIND_DIR + SERVER_ERROR_RES;
            // todo 得到报错的500.html
            sendResponse(socket,statusCode,location, persistent);
            return;
        }

        if(!isDown) {
            String redirectQuery = redirectList.query(uri); //重定向

            if (!redirectQuery.equals("")) { // 有301/302跳转项目，则执行跳转
                statusCode = Integer.parseInt(redirectQuery.substring(0, 3));
                String Location = redirectQuery.substring(3);
                uri = Location;
                sendResponse(socket,statusCode,BIND_DIR + Location, persistent);
            }

            else { //直接访问文件的情形
                statusCode=200;
                String Location =BIND_DIR+uri;
                sendResponse(socket,statusCode,Location, persistent);
            }
            }

    }

    private void sendResponse(Socket socket, int statusCode, String location, boolean persistent) {
        String trueUri=location.substring(location.lastIndexOf("/"));
        byte[] data = new byte[0];
        try {
            data = FileUtil.readFromFile(location);
        }
        catch (FileNotFoundException e) {
            System.out.println(location+"文件未找到");
            statusCode = 404;
            sendResponse(socket,404,BIND_DIR + NOT_FOUND_RES, persistent);
        }
        int dataLen=data.length;
        String Content_Type=MIMEList.getMIMEType(location);

        OutputStream os = null;
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 发送响应头
        String phrase = statusCodeList.getPhrase(statusCode);
        ResponseLine responseLine = new ResponseLine(statusCode, phrase);
        PrintStream print = new PrintStream(os);
        ResponseHeader sendMessageHeader=new ResponseHeader(statusCode,phrase);

        sendMessageHeader.put("Server", "WeDoRay-HttpServer");
        if(statusCode == 301 || statusCode == 302){
            sendMessageHeader.put("Location", trueUri);
        }
        sendMessageHeader.put("Content-Length", String.valueOf(dataLen));
        sendMessageHeader.put("Content-Type", Content_Type);
        if(persistent)sendMessageHeader.put("Connection", "Keep-Alive");
        HttpResponse response=new HttpResponse(responseLine,sendMessageHeader,new Body());
        try {
            print.write(response.toBytesFromServer());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 发送响应数据
        for(int i = 0; i < dataLen; i++){
            print.write(data[i]);
        }
        try {
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

package client;

import client.cache.ClientModifiedCache;
import client.cache.ClientRedirectCache;
import client.handler.ResponseHandler;
import message.Body;
import message.header.Header;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.FileUtil;
import util.MIMETypes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author Kevin
 * @Description
 */
public class NormalClient extends Client {

    private static HashMap<String, String> redirectCache = new ClientRedirectCache().getLocalStorage();
    private static ConnectionPool pool = new ConnectionPool();
    private static ClientModifiedCache localCache = new ClientModifiedCache();
    private static String boundary = "kvOEuWu8KBdBKTF5az4Y";

    private NormalClient() {

    }

    public NormalClient(int port, String host) {
        this.port = port;
        this.host = host;

    }

    public void Get(String uri, boolean persistent) throws IOException {
        Connection conn = null;

        try {
            conn = pool.getConnection(host, port, persistent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //封装将要发送的请求
        HttpRequest request = encapsulateRequest(uri, persistent);

        //发送http请求
        OutputStream socketOut = conn.getSendStream();
        //===>socketOut.write(OutputStreamHelper.toBytesFromLineAndHeader(request.requestLine.method, request.requestLine.requestURI, request.requestLine.version, request.Header.getHeader()));
        //感觉上面这样有点不好复用
        socketOut.write(request.toBytes());
        //处理返回请求
        InputStream inputStream = conn.getRecvStream();
        handleGet(inputStream, uri);
        if(!persistent)NormalClient.pool.removeConnection(host);
    }

    public void Post(String uri, boolean persistent, Body body) throws IOException {
        Connection conn = null;

        try {
            conn = pool.getConnection(host, port, persistent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //encapsulate post request
        HttpRequest request = encapsulatePostRequest(uri, persistent, body);

        OutputStream socketOut = conn.getSendStream();
        //send to server
        socketOut.write(request.toBytes());

        InputStream inputStream = conn.getRecvStream();

        handlePost(inputStream, uri);

        if(!persistent)NormalClient.pool.removeConnection(host);
    }

    private void handlePost(InputStream inputStream, String uri){

    }

    public void uploadFile(String uri, String filepath, boolean persistent) {
        Path path = Paths.get(filepath);
        try {
            byte[] bodyBytes = Files.readAllBytes(path);
            StringBuilder sb = new StringBuilder();
            sb.append("--").append(boundary).append(System.lineSeparator());
            sb.append("Content-Disposition: form-data; name=\"filename\"").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            sb.append(filepath).append(System.lineSeparator());
            sb.append("--").append(boundary).append(System.lineSeparator());
            sb.append("Content-Disposition: application/jpeg; name=\"data\"").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            StringBuilder end = new StringBuilder();
            end.append(System.lineSeparator()).append("--").append(boundary).append("--").append(System.lineSeparator());
            byte[] startBytes = sb.toString().getBytes();
            byte[] endBytes = end.toString().getBytes();
            byte[] bytes = new byte[startBytes.length + bodyBytes.length + endBytes.length];
            System.arraycopy(startBytes, 0, bytes, 0, startBytes.length);
            System.arraycopy(bodyBytes, 0, bytes, startBytes.length, bodyBytes.length);
            System.arraycopy(endBytes, 0, bytes, startBytes.length + bodyBytes.length, endBytes.length);
            Body body = new Body(bytes);
            Post(uri, persistent, body);
        } catch (IOException e) {
            System.out.println("IOExceptions occurs when reading file: " + filepath);
            return;
        }
    }

    private void setCommonHeader(Header requestHeader, boolean persistent){
        requestHeader.put("Accept", "*/*");
        requestHeader.put("Accept-Language", "zh-cn");
        requestHeader.put("User-Agent", "2022-HTTPClient");
        if (port != 80 && port != 443) {
            requestHeader.put("Host", host + ':' + port);
        } else {
            requestHeader.put("Host", host); // 访问默认端口的时候是不需要端口号的
        }
        if(persistent) {
            requestHeader.put("Connection", "Keep-Alive");
            requestHeader.put("Keep-Alive", "timeout=120");
        } else requestHeader.put("Connection", "close");
    }

    private void setHeaderByUri(String uri, Header requestHeader){
        switch(uri){
            case "/registerOrLogin":
                //do something
                requestHeader.put("Content-Type", "application/x-www-form-urlencoded");
                break;
            case "upLoadFile":
                //this is a randomly generated string
                requestHeader.put("Content-Type", "multipart/form-data; boundary=" + boundary);
                break;
        }
    }

    private HttpRequest encapsulatePostRequest(String uri, boolean persistent, Body body){
        RequestLine requestLine = new RequestLine("POST", uri);
        Header requestHeader = new Header();
        setCommonHeader(requestHeader, persistent);

        setHeaderByUri(uri, requestHeader);
        int len = body.getBody().length - System.lineSeparator().length();
        requestHeader.put("Content-Length", String.valueOf(len));
        HttpRequest request = new HttpRequest(requestLine, requestHeader, body);
        return request;
    }

    /**
     * @param uri
     * @return 封装request，方法为GET
     */
    private HttpRequest encapsulateRequest(String uri, boolean persistent) {
        RequestLine requestLine = new RequestLine("GET", uri);
        Header requestHeader = new Header();
        setCommonHeader(requestHeader, persistent);

        HttpRequest request = new HttpRequest(requestLine, requestHeader, null);

        return request;
    }

    /**
     * @param inputStream
     * @param uri
     * @throws IOException 处理从server传过来的流
     */
    private void handleGet(InputStream inputStream, String uri) throws IOException {
        System.out.println("====>>>> RECEIVING MESSAGE <<<<===");
        System.out.println("---->>>> header <<<<----");

        HttpResponse response = new HttpResponse(inputStream, "GET");
        ResponseHeader responseHeader = response.getMessageHeader();
        ResponseLine responseLine = response.getResponseLine();
        Body body = response.getMessageBody();

        //String toBePrint = new String(OutputStreamHelper.toBytesFromLineAndHeader(responseLine.version, String.valueOf(responseLine.statusCode), responseLine.description, responseHeader.getHeader()));
        String toBePrint = response.getResponseLine().toString() + response.getMessageHeader().toString();
        System.out.println(toBePrint);
        String receiveMIMEType = responseHeader.getHeader().get("Content-Type");
        boolean persistent = "Keep-Alive".equals(responseHeader.getHeader().get("Connection"));
        switch (responseLine.statusCode) {
            case 404://未找到
                System.out.println("---->>>> body <<<<----");
                System.out.println(new String(body.getBody()));
                break;
            case 500:// 服务器down掉了
                System.out.println("---->>>> body <<<<----");
                System.out.println(new String(body.getBody()));
                break;
            case 200: //成功
                System.out.println("---->>>> body <<<<----");
                if (receiveMIMEType.substring(0, 4).equals("text")) {
                    String bodyStr = new String(body.getBody());

                    String storage=FileUtil.createFilePath(receiveMIMEType,uri);
                    FileUtil.saveTextFile(bodyStr,storage);
                }
                else{
                    int lena = response.allInBytes.length;
                    byte[] data = Arrays.copyOfRange(response.allInBytes,
                            (int) (lena - responseHeader.getContentLength()), lena);
                    String storage=FileUtil.createFilePath(receiveMIMEType,uri);
                    FileUtil.saveBinaryFile(data,storage);
                }
                break;
            case 301://301 永久重定向
                String trueURI = responseHeader.get("Location");
                redirectCache.put(host + ':' + port + uri, trueURI);
                System.out.println("你将被301重定向至" + trueURI);
                Get(trueURI, persistent); // 跳转
                break;
            case 302: // 302临时重定向
                trueURI = responseHeader.get("Location");
                System.out.println("你将被302重定向至" + trueURI);
                Get(trueURI, persistent); // 跳转
                break;
            //TODO: untested yet
            case 304://not modified
                Body localResource = localCache.getLocalResource(host, uri);
                response.setMessageBody(localResource);
                System.out.println("Not modified, get resource from local storage...");
                break;
        }
        //update local cache if modified
        handleModified(response, uri);
    }

    //TODO: untested yet
    private void handleModified(HttpResponse response, String uri) {
        String lastModifiedTime = response.getMessageHeader().get("Last-Modified");
        if(lastModifiedTime != null && response.getResponseLine().statusCode != 304){
            try {
                localCache.putModified(host, uri, Long.parseLong(lastModifiedTime), response.getMessageBody());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}

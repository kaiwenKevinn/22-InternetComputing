package client.strategy;

import client.Connection;
import client.ConnectionPool;
import client.NormalClient;
import client.cache.ClientModifiedCache;
import message.Body;
import message.header.Header;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.FileUtil;
import util.TextDecoration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Kevin
 * @Description
 * 具体策略类，POST方法发送请求
 */
public class Post implements requestMethod{
    ConnectionPool pool;
    String host;
    int port;
    ClientModifiedCache localCache;
    HashMap<String, String> redirectCache;
    private static String boundary = "kvOEuWu8KBdBKTF5az4Y";
    private Post(){

    }
    public Post(String host, int port, ConnectionPool pool, ClientModifiedCache localCache, HashMap<String, String> redirectCache){
        this.pool=pool;
        this.host=host;
        this.port=port;
        this.localCache=localCache;
        this.redirectCache=redirectCache;
    }
    @Override
    public void handleResponse(InputStream inputStream, String uri) throws IOException {
        TextDecoration.printPurple("====>>>> RECEIVING MESSAGE <<<<===");
        TextDecoration.printPurple("---->>>> header <<<<----");

        HttpResponse response = new HttpResponse(inputStream);
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
                System.out.println("---->>>> 发送请求成功，数据已保存 <<<<----");
                if (receiveMIMEType.substring(0, 4).equals("text")) {
                    String bodyStr = new String(body.getBody());
                    String storage= FileUtil.createFilePath(receiveMIMEType,uri);
                    FileUtil.saveTextFile(bodyStr,storage);
                }
                else{
                    int lena = response.allInBytes.length;
                    byte[] data = Arrays.copyOfRange(response.allInBytes,
                            (int) (lena - responseHeader.getContentLength()), lena);
                    String storage= FileUtil.createFilePath(receiveMIMEType,uri);
                    FileUtil.saveBinaryFile(data,storage);
                }

                break;
            case 301://301 永久重定向
                String trueURI = responseHeader.get("Location");
                redirectCache.put(host + ':' + port + uri, trueURI);
                TextDecoration.printRed("你将被301重定向至" + trueURI);
                sendRequest(trueURI, persistent,new Body()); // 跳转
                break;
            case 302: // 302临时重定向
                trueURI = responseHeader.get("Location");
                TextDecoration.printRed("你将被302重定向至" + trueURI);
                sendRequest(trueURI, persistent,new Body()); // 跳转
                break;

            case 304://not modified
                Body localResource = localCache.getLocalResource(host, uri);
                response.setMessageBody(localResource);
                TextDecoration.printRed("Not modified, get resource from local storage...");
                break;
        }
        //update local cache if modified
        handleModified(response, uri);
    }

    @Override
    public HttpRequest encapsulateRequest(String uri, boolean persistent, Body body) {
        RequestLine requestLine = new RequestLine("POST", uri);
        Header requestHeader = new Header();
        setCommonHeader(requestHeader, persistent);

        setHeaderByUri(uri, requestHeader);
        int len = body.getBody().length - System.lineSeparator().getBytes().length;
        requestHeader.put("Content-Length", String.valueOf(len));
        HttpRequest request = new HttpRequest(requestLine, requestHeader, body);
        return request;
    }

    @Override
    public void sendRequest(String uri, boolean persistent,Body body) throws IOException {
        Connection conn = null;

        try {
            //从连接池中获取连接
            conn = pool.getConnection(host, port, persistent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //encapsulate post request
        HttpRequest request = encapsulateRequest(uri, persistent, body);

        OutputStream socketOut = conn.getSendStream();
        //send to server
        socketOut.write(request.toBytes());

        InputStream inputStream = conn.getRecvStream();

        handleResponse(inputStream, uri);

        if(!persistent) pool.removeConnection(host);
    }



    private void setHeaderByUri(String uri, Header requestHeader){
        switch(uri){
            case "/registerOrLogin":
                //do something
                requestHeader.put("Content-Type", "application/x-www-form-urlencoded");
                break;
            case "/uploadFile":
                //this is a randomly generated string
                requestHeader.put("Content-Type", "multipart/form-data; boundary=" + boundary);
                break;
        }
    }
    private void setCommonHeader(Header requestHeader, boolean persistent) {
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

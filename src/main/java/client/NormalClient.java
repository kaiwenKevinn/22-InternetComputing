package client;

import client.cache.ClientModifiedCache;
import client.cache.ClientRedirectCache;
import client.cache.LocalStorage;
import message.Body;
import message.header.Header;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.FileUtil;
import util.MIMETypes;
import util.OutputStreamHelper;
import util.TimeUtil;

import java.io.*;
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
        socketOut.write(OutputStreamHelper.toBytesFromLineAndHeader(request.requestLine.method, request.requestLine.requestURI, request.requestLine.version, request.Header.getHeader()));

        //处理返回请求
        InputStream inputStream = conn.getRecvStream();
        handleGet(inputStream, uri);
        if(!persistent)NormalClient.pool.removeConnection(host);
    }

    /**
     * @param uri
     * @return 封装request，方法为GET
     */
    private HttpRequest encapsulateRequest(String uri, boolean persistent) {
        RequestLine requestLine = new RequestLine("GET", uri);
        Header requestHeader = new Header();
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

        String toBePrint = new String(OutputStreamHelper.toBytesFromLineAndHeader(responseLine.version, String.valueOf(responseLine.statusCode), responseLine.description, responseHeader.getHeader()));
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
                    System.out.println(bodyStr);
                }
                else{
                    int lena = response.allInBytes.length;
                    String postFix= MIMETypes.getMIMELists().getReverseMIMEType(receiveMIMEType);
                    byte[] data = Arrays.copyOfRange(response.allInBytes,
                            (int) (lena - responseHeader.getContentLength()), lena);
                    String property = System.getProperty("user.dir");
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
                    String storage=new String(property+File.separator+"data"+File.separator+format.format(Calendar.getInstance().getTime())+postFix);
                    FileUtil.save(data,storage);
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

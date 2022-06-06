package client.strategy;

import client.ConnectionPool;
import client.cache.ClientModifiedCache;
import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.response.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;

public interface requestMethod {


//    public requestMethod(String host, int port, ConnectionPool pool, ClientModifiedCache localCache, HashMap<String, String> redirectCache){
////        this.host=host;
////        this.port=port;
////        this.localCache=localCache;
////        this.redirectCache=redirectCache;
//    }
    public abstract void handleResponse(InputStream inputStream, String uri) throws IOException;

    public abstract HttpRequest encapsulateRequest(String uri, boolean persistent, Body body);

    public abstract void sendRequest(String uri, boolean persistent, Body body) throws IOException;

//    public void setCommonHeader(Header requestHeader, boolean persistent) ;
//
//        requestHeader.put("Accept", "*/*");
//        requestHeader.put("Accept-Language", "zh-cn");
//        requestHeader.put("User-Agent", "2022-HTTPClient");
//        if (port != 80 && port != 443) {
//            requestHeader.put("Host", host + ':' + port);
//        } else {
//            requestHeader.put("Host", host); // 访问默认端口的时候是不需要端口号的
//        }
//        if(persistent) {
//            requestHeader.put("Connection", "Keep-Alive");
//            requestHeader.put("Keep-Alive", "timeout=120");
//        } else requestHeader.put("Connection", "close");


//    public void handleModified(HttpResponse response, String uri) ;
//        String lastModifiedTime = response.getMessageHeader().get("Last-Modified");
//        if(lastModifiedTime != null && response.getResponseLine().statusCode != 304){
//            try {
//                localCache.putModified(host, uri, Long.parseLong(lastModifiedTime), response.getMessageBody());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }

}

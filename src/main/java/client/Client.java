package client;

import client.handler.ResponseHandler;

import java.io.IOException;

/**
 * @author Kevin
 * @Description
 */
abstract public class Client {
    public String host;
    public int port;
    ResponseHandler responseHandler =new ResponseHandler();

    public abstract void Get(String uri) throws IOException;

//    public abstract HttpResponse sendHttpRequest(HttpRequest request);
}

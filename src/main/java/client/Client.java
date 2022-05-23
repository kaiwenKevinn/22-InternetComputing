package client;

import client.handler.RequestHandler;
import client.handler.ResponseHandler;
import message.request.HttpRequest;
import message.response.HttpResponse;

import java.io.IOException;

/**
 * @author Kevin
 * @Description
 */
abstract public class Client {
    public String host;
    public int port;
    RequestHandler requestHandler=new RequestHandler();
    ResponseHandler responseHandler =new ResponseHandler();

    public abstract void Get(String uri) throws IOException;

//    public abstract HttpResponse sendHttpRequest(HttpRequest request);
}

package client;

import client.handler.RequestHandler;
import client.handler.ResponseHandler;
import message.request.HttpRequest;
import message.response.HttpResponse;

/**
 * @author Kevin
 * @Description
 */
abstract public class Client {
    public String host;
    public int port;
    RequestHandler requestHandler;
    ResponseHandler responseHandler ;



    public abstract HttpResponse sendHttpRequest(HttpRequest request);
}

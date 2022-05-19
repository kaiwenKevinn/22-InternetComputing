package client.handler;

import message.request.HttpRequest;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.InputStreamHelper;

import java.io.InputStream;


/**
 * @author Kevin
 * @Description
 */
public class ResponseHandler implements Handler{
    public HttpResponse handle(HttpRequest httpRequest, InputStream inputStream){
        ResponseLine responseLine=new ResponseLine(InputStreamHelper.readLine(inputStream));
        //todo 5/19
        return null;
    }
}

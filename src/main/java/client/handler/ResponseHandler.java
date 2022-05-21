package client.handler;

import message.Body;
import message.header.Header;
import message.header.RequestHeader;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.InputStreamHelper;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Kevin
 * @Description
 */
public class ResponseHandler implements Handler{

    /**
     * @param httpRequest
     * @param inputStream
     * @return
     * @throws IOException
     * 目的是处理转发请求，更改缓存clientModifiedCache，history
     */

    public HttpResponse handle(HttpRequest httpRequest, InputStream inputStream) throws IOException {
        ResponseLine responseLine=new ResponseLine(InputStreamHelper.readLine(inputStream));
        ResponseHeader header=new ResponseHeader(inputStream);
        Body body=new Body(inputStream,header);
        HttpResponse response=new HttpResponse(responseLine,header,body);
        switch (responseLine.getStatusCode()){
            case 301:
            case 302:
            case 304:
                //todo handle redirect
        }
        return  response;

    }
}

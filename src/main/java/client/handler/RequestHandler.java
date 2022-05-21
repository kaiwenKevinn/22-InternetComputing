package client.handler;


import message.header.Header;
import message.request.HttpRequest;

/**
 * @author Kevin
 * @Description
 */
public class RequestHandler implements Handler {

    public HttpRequest handle(HttpRequest httpRequest){
        if(httpRequest==null){
            throw new RuntimeException("invaild httpRequest, the request is null");
        }
        Header header = httpRequest.getHeader();
        if(header.contentLength==0){
//            history.addLog("Missing Length was auto added, length="+String.valueOf(httpRequest.getMessageBody().getBody().length), History.LOG_LEVEL_WARNING);
            header.contentLength=httpRequest.messageBody.getBody().length;
        }
        //todo
//        //refering redirect
//        httpRequest = findRedirectCache(httpRequest);
//
//        //refering localStorage
//        httpRequest = findLastModifiedCache(httpRequest);
        return httpRequest;
    }
}

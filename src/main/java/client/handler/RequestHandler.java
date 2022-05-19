package client.handler;

import jdk.internal.org.jline.reader.History;
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
        if(header.get("Content-Length")==null){
            header.put("Content-Length",String.valueOf(httpRequest.getMessageBody().getBody().length));
//            history.addLog("Missing Length was auto added, length="+String.valueOf(httpRequest.getMessageBody().getBody().length), History.LOG_LEVEL_WARNING);
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

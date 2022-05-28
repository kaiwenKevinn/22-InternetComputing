package client.handler;

import message.Body;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.InputStreamHelper;
import util.OutputStreamHelper;

import java.io.IOException;
import java.io.InputStream;


/**
 * @author Kevin
 * @Description  对从服务器端收到的相应报文做处理
 */
public class ResponseHandler {


    public void handle(InputStream inputStream, String method) throws IOException {

    }
}

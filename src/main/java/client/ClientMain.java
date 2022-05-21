package client;

import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;

import java.io.IOException;

/**
 * @author Kevin
 * @Description
 */
public class ClientMain {
    public static void main(String[] args) throws IOException {

        RequestLine requestLine=new RequestLine("GET","/");
        Header header=new Header();
        header.put("Host","www.baidu.com");
        header.put("Accept","*/*");
        header.put("Connection","keep-alive");
        header.put("Accept-Encoding","gzip, deflate, br");

        Body body=new Body();
        HttpRequest request = new HttpRequest(requestLine, header, body);
        Client client=new NormalClient();

        HttpResponse response = client.sendHttpRequest(request);
        response.saveBody("xxx.html");
    }
}

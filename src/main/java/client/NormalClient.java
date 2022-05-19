package client;

import message.header.Header;
import message.header.ResponseHeader;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import message.response.ResponseLine;
import util.OutputStreamHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Kevin
 * @Description
 */
public class NormalClient extends Client{

    public void Get(String uri) throws IOException {
        Socket socket=null;

        try {
            socket=new Socket(this.host,this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //封装将要发送的请求
        RequestLine requestLine=new RequestLine("GET",uri);
        Header requestHeader=new Header();
        requestHeader.put("Accept", "*/*");
        requestHeader.put("Accept-Language", "zh-cn");
        requestHeader.put("User-Agent", "WeDoRay-HTTPClient");

        if(port != 80 && port != 443) {
            requestHeader.put("Host", host + ':' + port);
        }
        else{
            requestHeader.put("Host", host); // 访问默认端口的时候是不需要端口号的
        }
        requestHeader.put("Connection", "Keep-Alive");

        HttpRequest request=new HttpRequest(requestLine,requestHeader,null);

        //发送http请求
        OutputStream socketOut = socket.getOutputStream();
        socketOut.write(OutputStreamHelper.toBytesFromLineAndHeader(requestLine.method,requestLine.requestURI,requestLine.version,requestHeader.getHeader()));

        //处理返回请求
        InputStream inputStream = socket.getInputStream();

//        printGreen("====>>>> RECEIVING MESSAGE <<<<===");
//        printGreen("---->>>> header <<<<----");

        HttpResponse response = new HttpResponse(inputStream,"GET");
        ResponseHeader responseHeader=response.getMessageHeader();
        ResponseLine responseLine= response.getResponseLine();


//        printYellow();
        String toBePrint= new String(OutputStreamHelper.toBytesFromLineAndHeader(responseLine.version, String.valueOf(responseLine.statusCode),responseLine.description,responseHeader.getHeader()));
        System.out.println(toBePrint);
        String receiveMIMEType = responseHeader.getHeader().get("Content-Type");
        switch(responseLine.statusCode){
            case 404://未找到
            case 200: //成功
            case 301://301 永久重定向
            case 302: // 302临时重定向
        }

        if(socket != null) {
            socket.close();
        }
    }
    public HttpResponse sendHttpRequest(HttpRequest request) {
        HttpResponse response=null;
        HttpRequest handledRequest = requestHandler.handle(request);



        return null;
    }
}

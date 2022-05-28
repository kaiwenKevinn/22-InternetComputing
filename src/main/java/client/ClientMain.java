package client;

import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Kevin
 * @Description
 */
public class ClientMain {
    public static void main(String[] args) throws IOException {
        int port=808;
        String host="127.0.0.1";
//        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("请输入服务器host");
//        host=bf.readLine();
//        System.out.println("请输入port");
//        port= Integer.parseInt(bf.readLine());


        Client client=new NormalClient(port,host);

        client.Get("/302origin.html");
//        response.saveBody("xxx.html");
    }
}

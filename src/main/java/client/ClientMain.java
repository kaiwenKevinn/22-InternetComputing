package client;

import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.request.RequestLine;
import message.response.HttpResponse;
import util.TextDecoration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author Kevin
 * @Description
 * 客户端入口类
 */
public class ClientMain {
    public static void main(String[] args) throws IOException {
        int port=8888;
        String host="127.0.0.1";

        NormalClient client=new NormalClient(port,host,"POST");
        TextDecoration.welcome();
        //首次登录系统，强制注册
        String input = TextDecoration.registerAndLogin();

        boolean success = client.RegisterOrLogin(input, true);
        while (!success){
             input = TextDecoration.registerAndLogin();
             success = client.RegisterOrLogin(input, true);
        }
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String cmd="";
        TextDecoration.printBlue(TextDecoration.Head);
        TextDecoration.printGreen("请问您需要建立长连接吗？");
        TextDecoration.printGreen("输入true表示需要，否则表示不需要");
        TextDecoration.printBlue(TextDecoration.Head);
        cmd=bf.readLine();
        //不需要建立长连接，只发送一次请求之后自动关闭连接
        if(!cmd.equals("true")){
            TextDecoration.printBlue(TextDecoration.Head);
            System.out.println("您已经进入短连接，在一次请求之后，客户端程序将终止");
            TextDecoration.printGreen("请输入命令 例如:GET /index.html(从服务器获取/index.html资源) 或者是:POST /uploadFile(上传指定资源到服务器)" +
                    "POST /registerOrLogin(重新登录)");
            System.out.println("输入end结束程序");
            TextDecoration.printBlue(TextDecoration.Head);
            cmd= bf.readLine();
            String[]cmds=cmd.split(" ");
            switch (cmds[0].toUpperCase(Locale.ROOT)){
                case "GET":
                    client.switchMode("GET");
                    client.Get(cmds[1],false);
                    break;
                case "POST":
                    client.switchMode("POST");
                    //因为不是长连接，因此不能多次登录或者重新注册
                if(cmds[1].equals("/uploadFile")){
                    TextDecoration.printBlue(TextDecoration.Head);
                        System.out.println("请输入您想上传文件的名称 例如:temp.txt");
                    TextDecoration.printBlue(TextDecoration.Head);
                        String fileName= bf.readLine();
                        client.uploadFile(fileName,false);
                    }
                case "END":
                    break;
                default:
                    TextDecoration.printBlue(TextDecoration.Head);
                    System.out.println("您输入的命令有误，请重新输入!");
                    TextDecoration.printBlue(TextDecoration.Head);
            }
        }
        //建立长连接，可循环发送多个请求
        while (!cmd.equals("end")){
            TextDecoration.printBlue(TextDecoration.Head);
            System.out.println("您进入了长连接模式");
            TextDecoration.printGreen("请输入命令 例如:GET /index.html(从服务器获取/index.html资源) 或者是:POST /uploadFile(上传指定资源到服务器)" +
                    "POST /registerOrLogin(重新登录)");
            System.out.println("输入end结束程序");
            TextDecoration.printBlue(TextDecoration.Head);
            cmd= bf.readLine();
            String[]cmds=cmd.split(" ");
            switch (cmds[0].toUpperCase(Locale.ROOT)){
                case "GET":
                    client.switchMode("GET");
                    client.Get(cmds[1],true);
                    break;
                case "POST":
                    client.switchMode("POST");
                    if(cmds[1].equals("/uploadFile")){
                        TextDecoration.printBlue(TextDecoration.Head);
                        System.out.println("请输入您想上传文件的名称 例如:temp.txt");
                        TextDecoration.printBlue(TextDecoration.Head);
                        String fileName= bf.readLine();
                        client.uploadFile(fileName,true);
                    }
                    if(cmds[1].equals("/registerOrLogin")){
                        input = TextDecoration.registerAndLogin();
                        success = client.RegisterOrLogin(input, true);
                        while (!success){
                            input = TextDecoration.registerAndLogin();
                            success = client.RegisterOrLogin(input, true);
                        }
                    }
                case "END":
                    break;
                default:
                    TextDecoration.printBlue(TextDecoration.Head);
                    System.out.println("您输入的命令有误，请重新输入!");
                    TextDecoration.printBlue(TextDecoration.Head);
            }
        }

    }



}



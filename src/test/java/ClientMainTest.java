import client.NormalClient;
import org.junit.Test;
import util.TextDecoration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author Kevin
 * @Description
 */
public class ClientMainTest {
    /**
     * @throws IOException
     *
     */
    @Test
    public void April6th() throws IOException {
        int port=8888;
        String host="127.0.0.1";

        NormalClient client=new NormalClient(port,host);
        TextDecoration.welcome();
        String input = TextDecoration.registerAndLogin();

        boolean success = client.RegisterOrLogin(input, true);
        while (!success){
            input = TextDecoration.registerAndLogin();
            success = client.RegisterOrLogin(input, true);
        }
        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        String cmd="";
        System.out.println(TextDecoration.Head);
        System.out.println("请问您需要建立长连接吗？");
        System.out.println("输入true表示需要，否则表示不需要");
        System.out.println(TextDecoration.Head);
        cmd=bf.readLine();
        if(!cmd.equals("true")){
            System.out.println(TextDecoration.Head);
            System.out.println("您已经进入短连接，在一次请求之后，客户端程序将终止");
            System.out.println("请输入命令 例如:GET /index.html(从服务器获取/index.html资源) 或者是:POST /uploadFile(上传指定资源到服务器)" +
                    "POST /registerOrLogin(重新登录)");
            System.out.println("输入end结束程序");
            System.out.println(TextDecoration.Head);
            cmd="POST /uploadFile";
            String[]cmds=cmd.split(" ");
            switch (cmds[0].toUpperCase(Locale.ROOT)){
                case "GET":
                    client.Get(cmds[1],false);
                    break;
                case "POST":
                    if(cmds[1].equals("/uploadFile")){
                        System.out.println(TextDecoration.Head);
                        System.out.println("请输入您想上传文件的名称 例如:temp.txt");
                        System.out.println(TextDecoration.Head);
                        String fileName="post.html";
                        client.uploadFile(fileName,false);
                    }
                case "END":
                    break;
                default:
                    System.out.println(TextDecoration.Head);
                    System.out.println("您输入的命令有误，请重新输入!");
                    System.out.println(TextDecoration.Head);
            }
        }

        while (!cmd.equals("end")){
            System.out.println(TextDecoration.Head);
            System.out.println("您进入了长连接模");
            System.out.println("请输入命令 例如:GET /index.html(从服务器获取/index.html资源) 或者是:POST /uploadFile(上传指定资源到服务器)" +
                    "POST /registerOrLogin(重新登录)");
            System.out.println("输入end结束程序");
            System.out.println(TextDecoration.Head);
            cmd="POST /uploadFile";
            String[]cmds=cmd.split(" ");
            switch (cmds[0].toUpperCase(Locale.ROOT)){
                case "GET":
                    client.Get(cmds[1],true);
                    break;
                case "POST":
                    if(cmds[1].equals("/uploadFile")){
                        System.out.println(TextDecoration.Head);
                        System.out.println("请输入您想上传文件的名称 例如:temp.txt");
                        System.out.println(TextDecoration.Head);
                        String fileName="post.html";
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
                    System.out.println(TextDecoration.Head);
                    System.out.println("您输入的命令有误，请重新输入!");
                    System.out.println(TextDecoration.Head);
            }
        }

    }
    }


package client;

import client.cache.ClientModifiedCache;
import client.cache.ClientRedirectCache;
import client.strategy.Get;
import client.strategy.Post;
import client.strategy.requestMethod;
import lombok.Data;
import message.Body;
import util.TextDecoration;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @author Kevin
 * @Description
 */
@Data
public class NormalClient  {
    private String host;
    private int port;
    private static HashMap<String, String> redirectCache = new ClientRedirectCache().getLocalStorage();
    private static ConnectionPool pool = new ConnectionPool();
    private static ClientModifiedCache localCache = new ClientModifiedCache();
    private static String boundary = "kvOEuWu8KBdBKTF5az4Y";
    private static String RESOURCES_DIR = "src/main/java/client/Resources/";
    requestMethod requestMethod;

    private NormalClient() {

    }

    /**
     * @param port
     * @param host
     *
     * 默认方法为GET
     */
    public NormalClient(int port, String host) {
        this.port = port;
        this.host = host;
        this.requestMethod=new Get(host,port,pool,localCache, redirectCache);
    }

    public NormalClient(int port,String host,String method){
        this.port = port;
        this.host = host;
        if(method.equals("GET")){
            this.requestMethod=new Get(host,port,pool,localCache,redirectCache);
        }
        else if(method.equals("POST")){
            this.requestMethod=new Post(host,port,pool,localCache,redirectCache);
        }


    }
    public void switchMode(String method){
        if(method.equals("GET")){
            this.requestMethod=new Get(host,port,pool,localCache,redirectCache);
        }
        else if(method.equals("POST")) {
            this.requestMethod=new Post(host,port,pool,localCache,redirectCache);
        }
    }


    /**
     * @param input
     * @param persistent
     * @return
     * @throws IOException
     * 登录或者注册方法
     */
    public boolean RegisterOrLogin(String input, boolean persistent) throws IOException {
        byte[] bodyBytes=input.getBytes();
        Body body = new Body(bodyBytes);
        try {
            //调用POST请求发送登录注册的请求
            requestMethod.sendRequest("/registerOrLogin", persistent, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //读取服务端传回的结果
        FileReader fileReader=new FileReader( "src/main/java/client/Resources/registerOrLogin");
        BufferedReader bufferedReader=new BufferedReader(fileReader);
        String s;
       while (!(s = bufferedReader.readLine()).isEmpty()){
           if(s.contains("Successfully")){
               if(s.contains("Register")){
                   TextDecoration.printBlue(TextDecoration.Head);
                   System.out.println("注册成功！");
                   TextDecoration.printBlue(TextDecoration.Head);
               }
               else if(s.contains("login")){//todo
                   TextDecoration.printBlue(TextDecoration.Head);
                   System.out.println("登录成功！");
                   TextDecoration.printBlue(TextDecoration.Head);
               }
               return true;
           }
           if(s.contains("Fail")){
               TextDecoration.printBlue(TextDecoration.Head);
               System.out.println("您输入的密码有误，请重试或者重新注册账号！");
               TextDecoration.printBlue(TextDecoration.Head);
               return false;
           }
       }
        return false;
    }

    /**
     * @param filepath
     * @param persistent
     * 上传文件给服务器端
     */
    public void uploadFile(String filepath, boolean persistent) {
        Path path = Paths.get(RESOURCES_DIR + filepath);
        try {
            byte[] bodyBytes = Files.readAllBytes(path);
            StringBuilder sb = new StringBuilder();
            sb.append("--").append(boundary).append(System.lineSeparator());
            sb.append("Content-Disposition: form-data; name=\"filename\"").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            sb.append(filepath).append(System.lineSeparator());
            sb.append("--").append(boundary).append(System.lineSeparator());
            sb.append("Content-Disposition: application/jpeg; name=\"data\"").append(System.lineSeparator());
            sb.append(System.lineSeparator());
            StringBuilder end = new StringBuilder();
            end.append(System.lineSeparator()).append("--").append(boundary).append("--").append(System.lineSeparator());
            byte[] startBytes = sb.toString().getBytes();
            byte[] endBytes = end.toString().getBytes();
            byte[] bytes = new byte[startBytes.length + bodyBytes.length + endBytes.length];
            System.arraycopy(startBytes, 0, bytes, 0, startBytes.length);
            System.arraycopy(bodyBytes, 0, bytes, startBytes.length, bodyBytes.length);
            System.arraycopy(endBytes, 0, bytes, startBytes.length + bodyBytes.length, endBytes.length);
            Body body = new Body(bytes);
            requestMethod.sendRequest("/uploadFile", persistent, body);
        } catch (IOException e) {
            System.out.println("IOExceptions occurs when reading file: " + filepath);
            return;
        }
    }
    public void Get(String uri,boolean persistent) throws IOException {
        requestMethod.sendRequest(uri,persistent,new Body());
    }



}

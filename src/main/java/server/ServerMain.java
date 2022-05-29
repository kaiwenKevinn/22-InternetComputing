package server;


/**
 * @author czh
 * @Description Main process for a(may only 1) HttpServer
 */

public class ServerMain {
    public static  String BIND_DIR = "src/main/java/server/Resources";// 资源目录
    public static  String SERVER_ERROR_RES = "/500.html"; // 凡是服务器错误都返回这个页面
    public static  String NOT_FOUND_RES = "/404.html"; // 自定义404页面
    public static  int DEFAULT_PORT = 808;
    public static  String HOSTNAME = "127.0.0.1";

    public static void main(String[] args) {
        try {
            String hostname = "127.0.0.1"; // to be modified for test
            int port = 8080; // to be modified for test
            Server server = new NormalServer(hostname, port); // ? Longlinkserver
            server.start(); // note: here duck Exception
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}

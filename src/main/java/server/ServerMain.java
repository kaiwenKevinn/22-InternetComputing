package server;


import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author czh
 * @Description Main process for a(may only 1) HttpServer
 */

public class ServerMain {
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

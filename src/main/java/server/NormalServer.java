package server;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NormalServer extends Server {

    public NormalServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() throws Exception {
        ServerSocket server = new ServerSocket();
        // TODO: check validity for hostname and port
        server.bind(new InetSocketAddress(hostname, port)); // note: here duck exception
        System.out.println("Server init successfully."); // stderr

        while (true) {
            Socket socket = server.accept();

        }
    }
}

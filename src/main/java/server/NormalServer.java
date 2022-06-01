package server;

import server.handler.RequestHandler;
import server.usrServices.UserServicesList;
import util.FileTable;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.HashSet;
import java.util.Timer;

public class NormalServer extends Server {

    public static Timer timer = new Timer("timer");
    public NormalServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        Server.modifiedFileTable = new FileTable();
        Server.modifiedFileTable.initInAFolder(ServerMain.BIND_DIR);
        services = new UserServicesList();
    }

    public void start() throws Exception {
        ServerSocket serverSocket = new ServerSocket();
        // TODO: check validity for hostname and port
        serverSocket.bind(new InetSocketAddress(hostname, port)); // note: here duck exception

        System.out.println("Server init successfully."); // stderr ?
        while (isNormal) {
            Socket socket = serverSocket.accept();
            InetSocketAddress address = (InetSocketAddress) socket.getRemoteSocketAddress();
            System.out.println("Server received request");
            System.out.println(address.getHostName());
            RequestHandler handler = new RequestHandler(socket); // for multi-thread usage
            handler.start();
        }
    }
}

package client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConnectionPool {
    private HashMap<String, Socket> pool = new LinkedHashMap<>();

    public Socket getSocket(String host, int port) throws IOException {
        Socket socket = pool.get(host);
        if(socket != null){
            if(!socket.isClosed())return socket;
            removeConnection(host);
        }

        socket = new Socket(host, port);
        pool.put(host, socket);
        return socket;
    }

    private void removeConnection(String host){
        pool.remove(host);
    }
}

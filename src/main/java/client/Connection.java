package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection {
    private Socket clientSocket;
    private InputStream recvStream;
    private OutputStream sendStream;
    private int port;
    private String host;
    private boolean isKeepAlive;

    public Connection(String host, int port, boolean isKeepAlive){
        this.host = host;
        this.port = port;
        this.isKeepAlive = isKeepAlive;
    }

    public void create(){
        try {
            clientSocket = new Socket(host, port);
            clientSocket.setKeepAlive(isKeepAlive);
            sendStream = clientSocket.getOutputStream();
            recvStream = clientSocket.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isKeepAlive(){return isKeepAlive;}

    public String getHost(){return host;}

    public int getPort(){return port;}

    public InputStream getRecvStream(){return recvStream;}

    public OutputStream getSendStream(){return sendStream;}

    public boolean isClosed(){
        return clientSocket.isClosed();
    }

    public void close() throws IOException {
        clientSocket.close();
    }
}

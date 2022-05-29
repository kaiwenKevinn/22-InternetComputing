package client;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ConnectionPool {
    private HashMap<String, Connection> pool = new LinkedHashMap<>();

    public Connection getConnection(String host, int port, boolean persistent) throws IOException {
        Connection conn = pool.get(host);
        if(conn != null){
            if(conn.isClosed()){
                removeConnection(host);
            }else {
                return conn;
            }
        }
        conn = new Connection(host, port, persistent);
        conn.create();
        pool.put(host, conn);

        return conn;
    }

    public void removeConnection(String host){
        Connection conn = pool.get(host);
        if(conn != null){
            try {
                conn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pool.remove(host);
    }
}

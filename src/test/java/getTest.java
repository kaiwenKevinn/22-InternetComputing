import client.Client;
import client.NormalClient;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;

public class getTest {
    String host;
    int port = 80;
    Client client;

    @Test
    public void simpleTestForGet() throws IOException {
        host = "www.example.com";
        client = new NormalClient(port, host);
        String uri = "/";
        client.Get(uri, false);
    }

    @Test
    public void baiduGetTest() throws IOException {
        try {
            host = "www.baidu.com";
            client = new NormalClient(port, host);
            String uri = "/";
            client.Get(uri, false);
        }catch (SocketException e){
            baiduGetTest();
        }
    }

    @Test
    public void myServerNonPersistentGet() throws IOException {
        host = "127.0.0.1";
        port = 8888;
        client = new NormalClient(port, host);
        String uri = "/index.html";
        client.Get(uri, false);
    }  //todo  不用长连接会导致body打印不完整

    @Test
    public void myServerPersistentGet() throws IOException {
        host = "127.0.0.1";
        port = 8888;
        client = new NormalClient(port, host);
        String uri = "/index.html";
        client.Get(uri, true);
    }  //todo 

    @Test
    public void LoopPersistentGet() throws IOException {
        host = "127.0.0.1";
        port = 8888;
        client = new NormalClient(port, host);
        String uri = "/index.html";
        for (int i = 0; i < 4; i++) {
            client.Get(uri, true);
        }
    }
}
import client.NormalClient;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;

public class getTest {
    String host;
    int port = 80;
    NormalClient client;


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
    }


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

    @Test
    public void pngGet() throws IOException {
        host = "127.0.0.1";
        port = 8888;
        client = new NormalClient(port, host);
        String uri = "/2.png";
        client.Get(uri, false);
    }

    @Test
    public void zipGet() throws IOException {
        host = "127.0.0.1";
        port = 8888;
        client = new NormalClient(port, host);
        String uri = "/3.zip";
        client.Get(uri, false);
    }

    @Test
    public void myServer304Get() throws IOException {
        host = "127.0.0.1";
        port = 8888;
        client = new NormalClient(port, host);
        String uri = "/index.html";
        client.Get(uri, true);
        client.Get(uri, true);
    }
}
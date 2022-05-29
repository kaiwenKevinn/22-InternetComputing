import client.Client;
import client.NormalClient;
import org.junit.Test;

import java.io.IOException;

public class PersistentConnectionTest {
    private String host = "localhost";
    private int port = 8080;
    private Client client = new NormalClient(port, host);

    private void sendRequest(String uri, boolean isPersistent) throws IOException {
        client.Get(uri, isPersistent);
    }

    @Test
    public void persistentConnectionTest() throws IOException {
        String uri = "/index.html";
        sendRequest(uri, true);
        sendRequest(uri, true);
        sendRequest(uri, true);
    }
}

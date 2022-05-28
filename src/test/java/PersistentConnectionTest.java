import client.Client;
import client.NormalClient;
import org.junit.Test;

public class PersistentConnectionTest {
    private String host = "127.0.0.1";
    private int port = 8080;
    private Client client = new NormalClient(port, host);

    private void sendRequest(String uri, boolean isPersistent){

    }

    @Test
    public void persistentConnectionTest(){

    }
}

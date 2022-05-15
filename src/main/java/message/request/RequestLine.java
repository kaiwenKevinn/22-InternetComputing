package message.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author Kevin
 * @Description
 */
@AllArgsConstructor
@Data
@ToString
public class RequestLine {
    public String method;
    public String requestURI;
    public String version="HTTP/1.1";

    public RequestLine(String method, String uri) {
        this.method=method;
        requestURI=uri;
    }
}

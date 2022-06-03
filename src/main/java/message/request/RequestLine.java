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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(' ');
        sb.append(requestURI).append(' ');
        sb.append(version).append(System.lineSeparator());
        return sb.toString();
    }
}

package message.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import message.Body;
import message.header.Header;


/**
 * @author Kevin
 * @Description
 */
@Data
@AllArgsConstructor
@ToString
public class HttpRequest {
    public RequestLine requestLine;
    public Header Header;
    public Body messageBody;
}

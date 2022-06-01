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

    public byte[] toBytes(){
        byte[] lineBytes = requestLine.toString().getBytes();
        byte[] headerBytes = Header.toString().getBytes();
        byte[] bodyBytes = messageBody != null ? messageBody.toBytes() : new byte[0];
        byte[] reqBytes = new byte[lineBytes.length + headerBytes.length + bodyBytes.length];
        System.arraycopy(lineBytes, 0, reqBytes,0, lineBytes.length);
        System.arraycopy(headerBytes, 0, reqBytes, lineBytes.length, headerBytes.length);
        System.arraycopy(bodyBytes, 0, reqBytes, lineBytes.length + headerBytes.length, bodyBytes.length);
        return reqBytes;
    }
}

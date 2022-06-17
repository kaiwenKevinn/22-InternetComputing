package message;

import lombok.Data;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Kevin
 * @Description
 * 请求/响应 体
 */
@Data
@ToString
public class Body {
    byte[] body = new byte[0];

    public Body(byte[] body) { this.body = body; }

    public Body() {

    }
    public Body(InputStream inputStream){

    }

    public Body(BufferedReader reader, int contentLength) throws IOException {
        CharArrayWriter charArray = new CharArrayWriter();
        char[] buffer = new char[2048];
        int totalLen = 0, lenc;
        while ((lenc = reader.read(buffer)) > 0) {
            charArray.write(buffer, 0, lenc);
            totalLen += lenc;
            if (totalLen == contentLength) break;
        }
        body= charArray.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] toBytes(){return body;}
}

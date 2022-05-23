package message.header;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kevin
 * @Description
 */
public class ResponseHeader extends Header {

    int statusCode;
    String phrase;

    /**
     * 服务端构建返回给客户端的报文头
     *
     * @param statusCode 返回状态码
     * @param phrase     状态值
     */
    public ResponseHeader(int statusCode, String phrase) {
        this.statusCode = statusCode;
        this.phrase = phrase;
    }


    public ResponseHeader(InputStream inputStream) throws IOException {
        super(inputStream);
    }
}

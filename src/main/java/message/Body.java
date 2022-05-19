package message;

import lombok.Data;
import lombok.ToString;
import message.header.Header;
import util.ByteReader;
import util.ChunkReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Kevin
 * @Description
 */
@Data
@ToString
public class Body {
    byte[] body = new byte[0];


    public Body(InputStream inputStream, Header messageHeader) throws IOException {
        byte[] b = new byte[0];
        String transferEncoding = messageHeader.get("Transfer-Encoding");
        long contentLength = messageHeader.getContentLength();
        if(transferEncoding!=null&&transferEncoding.equals("chunked")){

            b = ChunkReader.readChunk(inputStream);
            // Content-Encoding: gzip 解压缩
            String contentEncoding = messageHeader.get("Content-Encoding");
//            if(contentEncoding!=null&&contentEncoding.equals("gzip")){
//                GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(b));
//                b = InputStreamHelper.readInputStream(gzipInputStream);
//            }
        }else if(contentLength!=0){
            b = ByteReader.readByte(inputStream,contentLength);
        }
        this.body = b;
    }
    public Body(){

    }
}

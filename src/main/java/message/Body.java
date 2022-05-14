package message;

import lombok.Data;
import lombok.ToString;

/**
 * @author Kevin
 * @Description
 */
@Data
@ToString
public class Body {
    byte[] body = new byte[0];


    public Body(InputStream inputStream,Header messageHeader) throws IOException {
        byte[] b = new byte[0];
        String transferEncoding = messageHeader.get("Transfer-Encoding");
        long contentLength = messageHeader.get("Content-Length");
        if(transferEncoding!=null&&transferEncoding.equals("chunked")){
            b = ChunkReader.readChunk(inputStream);
            // Content-Encoding: gzip 解压缩
            String contentEncoding = messageHeader.get(Header.Content_Encoding);
//            if(contentEncoding!=null&&contentEncoding.equals("gzip")){
//                GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(b));
//                b = InputStreamReaderHelper.readInputStream(gzipInputStream);
//            }
        }else if(contentLength!=null){
            int length = Integer.parseInt(contentLength);
            b = ByteReader.readByte(inputStream,length);
        }
        this.body = b;
    }
}

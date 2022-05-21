package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Kevin
 * @Description
 */
public class ByteReader {

    public static byte[] readByte(InputStream inputStream, long contentLength) throws IOException {
        byte []ans=new byte[1024];
        StringBuffer buffer = new StringBuffer();
//        int count=0;
//        while (count!=contentLength){
//            count+=inputStream.read(ans,count, (int) (contentLength-count));
//        }
        while (inputStream.read(ans)!=-1){
            buffer.append(ans);
        }
        return buffer.toString().getBytes(StandardCharsets.UTF_8);
    }
}

package message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import message.Body;
import message.header.ResponseHeader;
import sun.net.www.MessageHeader;

import java.io.*;
import java.util.Arrays;

/**
 * @author Kevin
 * @Description
 */
@Data
@AllArgsConstructor
@ToString
public class HttpResponse {
    ResponseLine responseLine;
    ResponseHeader messageHeader;
    Body messageBody;


    public HttpResponse(InputStream inputStream, String method) throws IOException {
        if(method.equals("POST")) return;//todo POST请求

        else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[ ]buffer=new byte[2048];
            int lenc;
            while ((lenc = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, lenc);
            }
            buffer = baos.toByteArray();
            BufferedReader reader=new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));

            //读取响应行
            String statusLine = reader.readLine();
            String[] elements = statusLine.split("\\s+");
            responseLine.version = elements[0];
            responseLine.statusCode = Integer.parseInt(elements[1]);

            int len =  elements.length;
            String[] phrases = Arrays.copyOfRange(elements, 2, len);
            responseLine.description = String.join(" ", phrases);

            //读取响应头
            String header = reader.readLine();
            while (!"".equals(header)) {
                String[] array = header.split(":");
                String key = array[0].trim(); // 去掉头尾空白符
                String value = array[1].trim();
                messageHeader.put(key, value);
                if (key.equalsIgnoreCase("Content-Length")) {
                    messageHeader.setContentLength(Long.parseLong(value));
                }
                header = reader.readLine();
            }

        }
    }
}

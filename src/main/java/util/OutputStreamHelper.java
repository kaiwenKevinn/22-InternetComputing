package util;

import java.util.HashMap;

/**
 * @author Kevin
 * @Description
 */
public class OutputStreamHelper {

    /**
     * 将客户端发送报文转换为 byte
     *
     * @return 发送报文byte数组
     */
    public static byte[] toBytesFromLineAndHeader(String param1, String param2, String param3, HashMap<String, String> headerFields) {
        StringBuilder resStringBuilder = new StringBuilder();

        resStringBuilder.append(param1);
        resStringBuilder.append(' ');
        resStringBuilder.append(param2);
        resStringBuilder.append(' ');
        resStringBuilder.append(param3);
        resStringBuilder.append(System.lineSeparator());
        for (String key : headerFields.keySet()
        ) {
            resStringBuilder.append(key);
            resStringBuilder.append(": ");
            resStringBuilder.append(headerFields.get(key));
            resStringBuilder.append(System.lineSeparator());
        }
        resStringBuilder.append(System.lineSeparator());
        return resStringBuilder.toString().getBytes();
    }
}

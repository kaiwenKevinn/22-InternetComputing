package util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author Kevin
 * @Description
 */
public class InputStreamHelper {

    public static String readLine(InputStream inputStream) {
        byte[] temp = new byte[1024];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        int eachChar;
        try {
            int times = 0;
//            循环读取，直到\n
            while (true) {
                eachChar = bufferedInputStream.read();
                if ((char) eachChar == '\n') {
                    return new String(new String(temp, 0, times));
                } else {
                    temp[times] = (byte) eachChar;
                    times++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] readAllFromInputStream(InputStream inputStream) {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        byte[] ans = new byte[1024];
        try {
            bis.read(ans);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ans;
    }

    /**
     * 从资源输入流构建返回byte数组
     * @param in 输入流
     * @return byte数组流
     */
    public static  byte[] getResAsStream(InputStream in){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int len = 0; // 单次读取长度
        int totalLen = 0; // 所有内容长度
        byte[] bytes = new byte[2048];
        while(true)
        {
            try {
                if ((len = in.read(bytes)) == -1) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            totalLen += len;
            try {
                os.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int dataLen = totalLen;
        byte [] ans=new byte[dataLen];
        ans = Arrays.copyOf(os.toByteArray(), dataLen);

        return ans;
    }
}

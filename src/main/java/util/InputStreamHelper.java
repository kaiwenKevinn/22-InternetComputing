package util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

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
}

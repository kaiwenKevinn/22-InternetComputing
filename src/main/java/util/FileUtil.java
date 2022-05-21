package util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Kevin
 * @Description
 */
public class FileUtil {
    public static void save(byte[]data,String path) throws IOException {
        FileOutputStream fis=new FileOutputStream(path);
        fis.write(data);
        fis.close();
    }
}

package util;

import java.io.*;

import static server.ServerMain.BIND_DIR;
import static server.ServerMain.NOT_FOUND_RES;
import static util.InputStreamHelper.getResAsStream;

/**
 * @author Kevin
 * @Description
 */
public class FileUtil {

    public static void save(byte[] data, String path) throws IOException {
        FileOutputStream fis = new FileOutputStream(path);
        BufferedOutputStream bos=null;
        bos=new BufferedOutputStream(fis);
        bos.write(data);
        bos.flush();
        bos.close();
    }

    /**
     * @param FileLocation
     * @return
     * @throws FileNotFoundException
     * 将FileLocation以字节形式读出
     */
    public static final  byte[] readFromFile(String FileLocation) throws FileNotFoundException {
            InputStream in=null;
            in=new FileInputStream(FileLocation);
            byte[] data = getResAsStream(in);
            return data;
    }
    }

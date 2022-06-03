package util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static server.ServerMain.BIND_DIR;
import static server.ServerMain.NOT_FOUND_RES;
import static util.InputStreamHelper.getResAsStream;

/**
 * @author Kevin
 * @Description
 */
public class FileUtil {

    /**
     * @param data
     * @param path
     * @throws IOException
     * 存储二进制文件
     */
    public static void saveBinaryFile(byte[] data, String path) throws IOException {
        FileOutputStream fis = new FileOutputStream(path, false);
        BufferedOutputStream bos = null;
        bos = new BufferedOutputStream(fis);
        bos.write(data);
        bos.flush();
        bos.close();
    }

    public static void saveTextFile(String data,String path) throws IOException {
        if(path.endsWith("/"))path = path + "temp.txt";
        FileWriter writer=new FileWriter(path,false);
        writer.write(data);
        writer.flush();
        writer.close();
    }
    /**
     * @param FileLocation
     * @return
     * @throws FileNotFoundException 将FileLocation以字节形式读出
     */
    public static final byte[] readFromFile(String FileLocation) throws FileNotFoundException {
        InputStream in = null;
        in = new FileInputStream(FileLocation);
        byte[] data = getResAsStream(in);
        return data;
    }
    public static String createFilePath(String MimeType,String uri){
        String postFix= MIMETypes.getMIMELists().getReverseMIMEType(MimeType);
        String property = System.getProperty("user.dir");
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
        String storage=new String(property+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"client"+File.separator+"Resources"+File.separator+uri);
        return storage;
    }
}

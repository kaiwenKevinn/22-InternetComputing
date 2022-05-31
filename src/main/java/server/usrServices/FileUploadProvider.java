package server.usrServices;

import server.HttpResponseReturnValue;
import server.Server;
import server.ServerMain;
import util.FileTable;
import util.FileUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;

public class FileUploadProvider extends UserServiceProvider {
    private static String UPLOAD_SUCCESS_DIR = "/uploadSuccess.html";
    FileTable fileTable;

    public FileUploadProvider() {
        bindUri = "/uploadFile";
        lock = new ReentrantLock();
        fileTable = Server.modifiedFileTable;
        assert (fileTable != null);
    }

    /**
     * 上传文件
     * @param arg1 fileName
     * @param arg2 bytes[] in file
     * @param arg3 null, 不用
     * @return ()
     */
    public HttpResponseReturnValue handle(String arg1, String arg2, String arg3) {
        assert (arg3 == null);
        lock.lock();
        String fileName = arg1;
        byte[] bytes = arg2.getBytes(StandardCharsets.UTF_8);
        fileTable.modify(fileName); // !warning: deadlock
        try {
            FileUtil.save(bytes, ServerMain.BIND_DIR + "/" + fileName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpResponseReturnValue ret = new HttpResponseReturnValue(200, ServerMain.BIND_DIR + UPLOAD_SUCCESS_DIR);
        lock.unlock();
        return ret;
    }
}

package util;

import java.util.HashMap;

/**
 * @author Kevin
 * @Description
 */

public class StatusCodeAndPhrase {
    public static HashMap<Integer, String> codeList = new HashMap<Integer, String>();
    private static StatusCodeAndPhrase statusCodeAndPhrase = null;

    private StatusCodeAndPhrase(){
        // 视频部分
        codeList.put(200, "OK");
        codeList.put(301, "Moved Permanently");
        codeList.put(302, "Found");
        codeList.put(304, "Not Modified");
        codeList.put(404, "Not Found");
        codeList.put(405, "Method Not Allowed");
        codeList.put(500, "Internal Server Error");
    }

    public static StatusCodeAndPhrase getStatusCodeList(){
        if(StatusCodeAndPhrase.statusCodeAndPhrase == null){
            StatusCodeAndPhrase.statusCodeAndPhrase = new StatusCodeAndPhrase();
        }
        return StatusCodeAndPhrase.statusCodeAndPhrase;
    }

    public String getPhrase(int status){
        return codeList.getOrDefault(status, "UNKNOWN OPERATION");
    }
}


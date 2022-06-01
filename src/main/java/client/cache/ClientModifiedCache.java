package client.cache;

import message.Body;
import util.TimeUtil;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;

//TODO: untested yet
/**
 * @author sunlifan
 * @date 2022/5/31
 */
public class ClientModifiedCache {
    private HashMap<String, LocalStorage> localCache = new LinkedHashMap<>();

    public void putModified(String hostname, String uri, Long time, Body body) throws ParseException {
        long timeStamp = TimeUtil.getTimestamp(TimeUtil.toTimeString(time));

        LocalStorage localStorage = new LocalStorage(timeStamp, body);

        localCache.put(hostname + uri, localStorage);

    }

    public Long getModifiedTime(String hostname, String uri){
        LocalStorage localStorage = localCache.get(hostname + uri);
        if(localStorage == null)return null;

        return localStorage.getTimeStamp();
    }

    public Body getLocalResource(String hostname, String uri){
        return localCache.get(hostname + uri).getBody();
    }
}

package client.cache;

import message.Body;

//TODO: untested yet
/**
 * @author sunlifan
 * @date 2022/5/31
 * local storage, support for 304 status code
 */
public class LocalStorage {
    private long timeStamp;
    private Body body;

    public LocalStorage(long timeStamp, Body body){
        this.timeStamp = timeStamp;
        this.body = body;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}

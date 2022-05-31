package server;

/**
 *  (statusCode, location) 二元组，用于产生返回的消息
 */
public class HttpResponseReturnValue {
    int statusCode;
    String location;

    public HttpResponseReturnValue() {
        statusCode = 0;
        location = null;
    }

    public HttpResponseReturnValue(int statusCode, String location) {
        this.statusCode = statusCode;
        this.location = location;
    }
}

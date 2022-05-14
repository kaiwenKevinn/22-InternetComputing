package message;

/**
 * @author Kevin
 * @Description
 */
public class RequestHeader extends Header{

    public String operation;
    public String uri;

    /**
     * 客户端构建给服务器的报文头
     * @param op 客户端请求方法
     * @param uri 请求URI
     */
    public RequestHeader(String op, String uri){
        this.operation = op;
        this.uri = uri;
    }

    public String getRequestLine(){
        return operation + ' ' + uri + version;
    }

}

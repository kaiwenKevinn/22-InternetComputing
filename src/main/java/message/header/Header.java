package message.header;

import lombok.Data;
import lombok.ToString;
import util.InputStreamHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Kevin
 * @Description
 */
@Data
@ToString
public class Header {
//    public String version = "HTTP/1.1";

    private HashMap<String, String> header = new LinkedHashMap<>();
//    头部的长度。
    public long contentLength = 0;

    public Header(){

    }
    /**
     * @param inputStream
     * @throws IOException
     * 基于输入流构建报文头
     */
    public Header(InputStream inputStream) throws IOException {
        List<String> headers = new ArrayList<>();
        String temp;
        while (!(temp= InputStreamHelper.readLine(inputStream)).equals("")){
            headers.add(temp);
        }
        constructHelper(headers);
    }


    public Header(List<String> headers){
        constructHelper(headers);
    }



    private void constructHelper(List<String> headers){
        for(String header:headers){
            String formattedHeader = header.trim();
            if(formattedHeader.equals("")){
                continue;
            }
            if(!formattedHeader.contains(":")){
                //TODO
                continue;
            }
            int index = formattedHeader.indexOf(":");
            String fieldName = formattedHeader.substring(0,index).trim();

            if(fieldName.equalsIgnoreCase("Content-Length")){

//                this.contentLength = Long.parseLong(value);
            }

            String fieldValue = formattedHeader.substring(index+1).trim();
            this.header.put(fieldName,fieldValue);
        }
    }




    public void put(String fieldName, String fieldValue){
        header.put(fieldName, fieldValue);
    }

    public void remove(String fieldName){
        header.remove(fieldName);
    }

    public String get(String fieldName){
        return header.get(fieldName);
    }

    public Set<String> getAllFieldName(){
        return header.keySet();
    }

}

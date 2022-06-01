package message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author Kevin
 * @Description
 */
@AllArgsConstructor
@Data
@ToString
public class ResponseLine {
    public String version = "HTTP/1.1";
    public Integer statusCode;
    public String description;

    public ResponseLine(String responseLineString){
        if(!responseLineString.contains(" ")){
            //TODO
            //throw new Exception("some error occured when reading ResponseLine");
        }else{
            int index = responseLineString.indexOf(" ");
            this.version = responseLineString.substring(0,index);
            responseLineString = responseLineString.substring(index+1);
        }
        if(!responseLineString.contains(" ")){
            //TODO
            //throw new Exception("some error occured when reading ResponseLine");
        }else{
            int index = responseLineString.indexOf(" ");
            String statusCode = responseLineString.substring(0,index);
            try{
                this.statusCode = Integer.parseInt(statusCode);
            }catch (Exception e){
                //TODO
            }
            this.description = responseLineString.substring(index+1);
        }
    }

    public ResponseLine(int statusCode, String phrase) {
        this.statusCode=statusCode;
        this.description=phrase;
    }

    public ResponseLine() {

    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(version).append(' ').append(statusCode).append(' ').append(description);
        sb.append(System.lineSeparator());

        return sb.toString();
    }
}

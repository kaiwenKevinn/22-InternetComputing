package message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import message.Body;
import message.header.ResponseHeader;

import util.FileUtil;
import util.MIMETypes;
import util.StatusCodeAndPhrase;

import java.io.*;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Kevin
 * @Description
 */
@Data
@ToString
public class HttpResponse {
    ResponseLine responseLine;
    ResponseHeader messageHeader;
    Body messageBody;
    public byte[] allInBytes; // 所有从server写过来的数据

    /**
     * generate HttpResponse
     * called from RequestHandler
     *
     * @param statusCode return statusCode
     * @param location   resources location
     * @param persistent is connection persistent
     */
    public HttpResponse(int statusCode, String location, boolean persistent, Body messageBody,long modifiedTime) {
        MIMETypes MIMEList = MIMETypes.getMIMELists();
        StatusCodeAndPhrase statusCodeAndPhrase = StatusCodeAndPhrase.getStatusCodeList();
        String phrase = statusCodeAndPhrase.getPhrase(statusCode);
        ResponseLine responseLine = new ResponseLine(statusCode, phrase);

        ResponseHeader messageHeader = new ResponseHeader(statusCode, phrase);
        messageHeader.put("Server", "2022-HttpServer");
        if (statusCode == 301 || statusCode == 302) {
            String trueUri = location.substring(location.lastIndexOf("/"));
            messageHeader.put("Location", trueUri);
        }
        int dataLen = messageBody.getBody().length;
        messageHeader.put("Content-Length", String.valueOf(dataLen));
        String Content_Type = MIMEList.getMIMEType(location);
        messageHeader.put("Content-Type", Content_Type);
//        String lastModified=
//        messageHeader.put("Last-Modified",);
        //todo get modifiedTime
        messageHeader.put("Last-Modified", String.valueOf(modifiedTime));
        if (persistent) {
            messageHeader.put("Connection", "Keep-Alive");
        }

        this.responseLine = responseLine;
        this.messageHeader = messageHeader;
        this.messageBody = messageBody;
    }
    public HttpResponse(int statusCode, String location, boolean persistent, Body messageBody) {
        MIMETypes MIMEList = MIMETypes.getMIMELists();
        StatusCodeAndPhrase statusCodeAndPhrase = StatusCodeAndPhrase.getStatusCodeList();
        String phrase = statusCodeAndPhrase.getPhrase(statusCode);
        ResponseLine responseLine = new ResponseLine(statusCode, phrase);

        ResponseHeader messageHeader = new ResponseHeader(statusCode, phrase);
        messageHeader.put("Server", "2022-HttpServer");
        if (statusCode == 301 || statusCode == 302) {
            String trueUri = location.substring(location.lastIndexOf("/"));
            messageHeader.put("Location", trueUri);
        }
        int dataLen = messageBody.getBody().length;
        messageHeader.put("Content-Length", String.valueOf(dataLen));
        String Content_Type = MIMEList.getMIMEType(location);
        messageHeader.put("Content-Type", Content_Type);

        if (persistent) {
            messageHeader.put("Connection", "Keep-Alive");
        }

        this.responseLine = responseLine;
        this.messageHeader = messageHeader;
        this.messageBody = messageBody;
    }
    public HttpResponse(ResponseLine responseLine, ResponseHeader messageHeader, Body body, byte[] allInBytes) {
        this.responseLine = responseLine;
        this.messageHeader = messageHeader;
        this.messageBody = body;
    }

    /**
     * @param inputStream
     * @param method
     * @param inputStream 服务端输入流
     * @throws IOException 客户端基于服务器给的输入流构建response
     */
    public HttpResponse(InputStream inputStream, String method) throws IOException {
        responseLine = new ResponseLine();
        messageHeader = new ResponseHeader();
        messageBody = new Body();
        if (method.equals("POST")) return;//todo POST请求

        else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int lenc = 0;
            while ((lenc = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, lenc);
                if(lenc < 2048)break;
            }
//            BufferedInputStream
            allInBytes = baos.toByteArray();
            buffer = baos.toByteArray();


            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer)));

            //读取响应行;
            String statusLine = reader.readLine();
            //System.out.println("debug : " + statusLine);
            String[] elements = statusLine.split("\\s+");
            String version = elements[0];
            int statusCode = Integer.parseInt(elements[1]);
            int len = elements.length;
            String[] phrases = Arrays.copyOfRange(elements, 2, len);
            String description = String.join(" ", phrases);


            responseLine = new ResponseLine(statusCode, description);
            //读取响应头
            String header = reader.readLine();

            //header 和body之间有""
            while (!"".equals(header)) {
                String[] array = header.split(":");
                String key = array[0].trim(); // 去掉头尾空白符
                String value = array[1].trim();
                messageHeader.put(key, value);
                if (key.equalsIgnoreCase("Content-Length")) {
                    messageHeader.setContentLength(Long.parseLong(value));
                }
                header = reader.readLine();
            }

            messageBody = new Body(reader, (int) messageHeader.getContentLength());

        }
    }

    public void saveBody(String path) throws IOException {
        FileUtil.save(messageBody.getBody(), path);
    }

    /**
     * construct byte[] of a whole HttpResponse, for being sended in socket
     * @return byte[]
     */
    public byte[] toBytes() {
        // construct header
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(responseLine.getVersion()).append(' ');
        headerBuilder.append(responseLine.getStatusCode()).append(' ');
        headerBuilder.append(responseLine.getDescription()).append(' ');
        headerBuilder.append(System.lineSeparator());
        for (String key : messageHeader.getHeader().keySet()) {
            headerBuilder.append(key).append(": ").append(messageHeader.getHeader().get(key));
            headerBuilder.append(System.lineSeparator());
        }
        headerBuilder.append(System.lineSeparator());
        byte[] header = headerBuilder.toString().getBytes();

        // construct body
        byte[] body = messageBody.getBody();

        // concat header and body
        ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
        try {
            tmpStream.write(header);
            tmpStream.write(body);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return tmpStream.toByteArray();
    }

}

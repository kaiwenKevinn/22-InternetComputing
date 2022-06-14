package client.strategy;

import client.ConnectionPool;
import client.cache.ClientModifiedCache;
import message.Body;
import message.header.Header;
import message.request.HttpRequest;
import message.response.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.HashMap;

public interface requestMethod {



    public abstract void handleResponse(InputStream inputStream, String uri) throws IOException;

    public abstract HttpRequest encapsulateRequest(String uri, boolean persistent, Body body);

    public abstract void sendRequest(String uri, boolean persistent, Body body) throws IOException;


}

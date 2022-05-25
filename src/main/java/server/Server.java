package server;

import java.net.ServerSocket;

/**
 * @author czh
 * @Description Abstract class for HttpServer，extended by NormalServer and LongLinkServer
 */

abstract public class Server{
    // TODO
    protected boolean isNormal = true; // is working normally
    protected String hostname;
    protected int port;
    abstract public void start() throws Exception;
}

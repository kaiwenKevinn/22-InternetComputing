package server;

import server.usrServices.UserServicesList;
import util.FileTable;

import java.io.File;
import java.net.ServerSocket;

/**
 * @author czh
 * @Description Abstract class for HttpServer，extended by NormalServer and LongLinkServer
 */

abstract public class Server{
    protected boolean isNormal = true; // is working normally
    protected String hostname;
    protected int port;
    public static UserServicesList services;
    public static FileTable modifiedFileTable;
    abstract public void start() throws Exception;
}

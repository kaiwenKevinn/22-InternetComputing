package server.usrServices;

import server.HttpResponseReturnValue;

import java.util.concurrent.locks.Lock;

public abstract class UserServiceProvider {
    protected Lock lock;
    protected String bindUri;

    abstract HttpResponseReturnValue handle(String arg1, String arg2, String arg3);
}

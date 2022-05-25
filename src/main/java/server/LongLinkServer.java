package server;

import java.util.Timer;
import java.util.TimerTask;

public class LongLinkServer extends Server {
    private boolean timeout;
    private Timer timer;
    public LongLinkServer(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
        timeout = false;
        timer = new Timer("timer");
    }

    @Override
    public void start() throws Exception {
        //TODO
        //1. setup connection

        //TODO
        //2. start loop
        //2.1 check if timeout, if timeout, close connection
        //2.2 handle request
        //2.3 reset timer
    }


    private void startTimer(long delay){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeout = true;
            }
        };
        timer.schedule(task, delay);
    }

    private void cancelTimer(){
        timer.cancel();
    }
}

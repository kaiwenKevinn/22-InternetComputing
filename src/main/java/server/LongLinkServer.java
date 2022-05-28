package server;

import java.util.Timer;
import java.util.TimerTask;

public class LongLinkServer extends Server {
    private boolean timeout;
    private Timer timer;
    public LongLinkServer(String hostname, int port){
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void start() throws Exception {
        //TODO
        //1. setup connection

        //TODO
        //2. start loop

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

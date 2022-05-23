package server.handler;

import message.request.HttpRequest;
import message.response.HttpResponse;

import java.net.Socket;

// czh: 我是认为这部分的职责应该交给Server，但是要多线程，所以就先这么试一下，有更好的方法吗？

public class RequestHandler extends Thread implements Handler {
    Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // TODO
        // readRequest() -> handle() -> sendRequest()
    }

    private HttpRequest readRequest() {
        // TODO
        // phrase httpRequest
        return null;
    }

    private HttpResponse handle(HttpRequest httpRequest) {
        // TODO
        // generate httpResponse and error handling
        return null;
    }

    private void sendResponse() {
        // TODO
        // send httpResponse
    }
}

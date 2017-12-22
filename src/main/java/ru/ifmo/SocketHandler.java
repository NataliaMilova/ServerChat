package ru.ifmo;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class SocketHandler extends WebSocketHandler {
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(MySocket.class);
    }
}

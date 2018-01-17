package ru.ifmo.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.utils.ChatServerUtils;

import java.io.IOException;
import java.sql.SQLException;

@WebSocket
public class MessageSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSocket.class);

    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        this.session = session;
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(message);
            if (parse.get("type") != null) {
                System.out.println(parse.get("type"));
                switch (MessageType.getMessageType((String) parse.get("type"))) {
                    case AUTHORIZATION:
                        ChatServerUtils.onWebSocketConnectUser(parse, this.session);
                        break;
                    case MESSAGE:
                        ChatServerUtils.onWebSocketMessage(parse);
                        break;
                    case EXIT:
                        ChatServerUtils.onWebSocketDisconnectUser(parse, this.session);
                        break;
                }
            }
        } catch (ParseException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

}

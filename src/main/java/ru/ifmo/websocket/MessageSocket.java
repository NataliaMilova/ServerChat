package ru.ifmo.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.ifmo.ChatServer;
import ru.ifmo.entity.Chat;
import ru.ifmo.utils.DataBaseUtils;

import java.io.IOException;
import java.sql.SQLException;

@WebSocket
public class MessageSocket {

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
                        DataBaseUtils.onWebSocketConnectUser(parse, this.session);
                        break;
                        //отдать сообщения по фильтру времени последнего визита
                    case MESSAGE:
                        DataBaseUtils.onWebSocketMessage(parse);
                        break;
                    case EXIT:
                        DataBaseUtils.onWebSocketDisconnectUser(parse, this.session);
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

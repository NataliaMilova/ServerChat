package ru.ifmo.old.websocket;

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
import ru.ifmo.old.utils.ChatServerUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebSocket
public class MessageSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSocket.class);

    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Connect: " + session.getRemoteAddress());
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        JSONObject jsonObject =  new JSONObject();
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(message);
            if (parse.get("type") != null) {
                switch (MessageType.getMessageType((String) parse.get("type"))) {
                    case AUTHORIZATION:
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("Get authorization message by socket from " + this.session.getRemoteAddress() + " with userId = " + parse.get("userId"));
                        ChatServerUtils.onWebSocketConnectUser(parse, this.session);
                        break;
                    case MESSAGE:
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("Get message by  socket from " + this.session.getRemoteAddress() + " with userId = " + parse.get("userId") + ", text = " + parse.get("text"));
                        ChatServerUtils.onWebSocketMessage(parse);
                        break;
                    case EXIT:
                        if (LOGGER.isDebugEnabled())
                            LOGGER.debug("Get exit message by socket from " + this.session.getRemoteAddress() + " with userId = " + parse.get("userId"));
                        ChatServerUtils.onWebSocketDisconnectUser(parse, this.session);
                        break;
                }
            }
        } catch (ParseException | SQLException | IOException e) {
            jsonObject.put("code", "500");
            jsonObject.put("type", "server error");
            List<Session> sessions = new ArrayList<>();
            sessions.add(this.session);
            try {
                ChatServerUtils.sendMessage(sessions, jsonObject);
            } catch (IOException e1) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Send exception message error", e);
            }
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Web socket error", e);
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Close: " + session.getRemoteAddress() + " with statusCode = " + statusCode + ", reason = " + reason);
    }

}

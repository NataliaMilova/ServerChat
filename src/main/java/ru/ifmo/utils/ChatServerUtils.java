package ru.ifmo.utils;

import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.server.ChatServer;
import ru.ifmo.entity.Chat;
import ru.ifmo.entity.Message;
import ru.ifmo.entity.User;
import ru.ifmo.services.ChatsService;
import ru.ifmo.services.ChatsUsersService;
import ru.ifmo.services.MessagesService;
import ru.ifmo.services.UsersService;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ChatServerUtils {

    private static ChatsUsersService chatsUsersService;
    private static UsersService usersService;
    private static ChatsService chatsService;
    private static MessagesService messagesService;

    static {
        try {
            chatsUsersService = new ChatsUsersService(ChatServer.getConnection());
            usersService = new UsersService(ChatServer.getConnection());
            chatsService = new ChatsService(ChatServer.getConnection());
            messagesService = new MessagesService(ChatServer.getConnection(), 25);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static Logger LOGGER = LoggerFactory.getLogger(ChatServerUtils.class);


    private static void createTables() throws SQLException {
        StringBuilder chatsSb = new StringBuilder();
        chatsSb.append("CREATE TABLE IF NOT EXISTS chats ( \n")
                .append(" chatId integer PRIMARY KEY AUTOINCREMENT,\n")
                .append(" chatName varchar(255) NOT NULL\n")
                .append(");");
        String chats = chatsSb.toString();

        StringBuilder usersSb = new StringBuilder();
        usersSb.append("CREATE TABLE IF NOT EXISTS users (\n")
                .append(" userId varchar(20) NOT NULL UNIQUE,\n")
                .append(" nickname varchar(100) NOT NULL,\n")
                .append(" lastVisit bigint NOT NULL,\n")
                .append(" password text NOT NULL,\n")
                .append(" PRIMARY KEY (userId)\n")
                .append(");");
        String users = usersSb.toString();

        StringBuilder messagesSb = new StringBuilder();
        messagesSb.append("CREATE TABLE IF NOT EXISTS messages (\n")
                .append("	messageId integer PRIMARY KEY AUTOINCREMENT,\n")
                .append("	timestamp bigint NOT NULL,\n")
                .append("	text text NOT NULL,\n")
                .append("	userId varchar NOT NULL,\n")
                .append("	chatId integer NOT NULL,\n")
                .append("	FOREIGN KEY (userId) REFERENCES users (userId)\n")
                .append("	FOREIGN KEY (chatId) REFERENCES chats (chatId) ON DELETE CASCADE\n")
                .append(");");
        String messages = messagesSb.toString();

        StringBuilder chatsUsersSb = new StringBuilder();
        chatsUsersSb.append("CREATE TABLE IF NOT EXISTS chats_users (\n")
                .append(" userId varchar NOT NULL,\n")
                .append(" chatId integer NOT NULL,\n")
                .append(" FOREIGN KEY (userId) REFERENCES users (userId)\n")
                .append(" FOREIGN KEY (chatId) REFERENCES chats (chatId) ON DELETE CASCADE\n")
                .append(");");
        String chats_users = chatsUsersSb.toString();

        try (Connection connection = ChatServer.getConnection().getConnection()) {
            //log debug try create table chats
            createTable(connection, chats);
            //log info table chats success create or exists
            //log debug log debug try create table users
            //etc
            createTable(connection, users);
            createTable(connection, messages);
            createTable(connection, chats_users);
        }
        //log create
        LOGGER.debug("create");
    }

    private static void createTable(Connection connection, String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public static boolean createDataBase(){
        try {
            createTables();
            //log info database is ready
        } catch (SQLException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Error of database", e);
            return false;
        }
        return true;
    }


    public static String authorization(JSONObject parse) throws SQLException{
        JSONObject result = new JSONObject();
        String userId = (String)parse.get("userId");
        String password = (String)parse.get("password");
        User user = usersService.getUserById(userId);
        if (user.getUserId() == null){
            result.put("code", "404");
            result.put("message","User is not found");
        }
        else {
            if (password.equals(user.getPassword())) {
                result.put("code", "200");
                result.put("userId", user.getUserId());
                result.put("nickname", user.getNickname());
                result.put("message", "Success authorization");
            } else {
                result.put("code", "400");
                result.put("message", "Invalid password"); }
        }
        return result.toJSONString();
    }

    public static String getChatsWithNewMessagesByUserId(JSONObject parse) throws SQLException {
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        String userId = (String) parse.get("userId");
        User user = usersService.getUserById(userId);
        if (user.getUserId() == null) {
            result.put("code", "404");
            result.put("message", "User is not found");
        } else {
            if (user.getLastVisit() != 0) {
                for (Integer chat : messagesService.getChatsWithNewMessagesByUserId(user, chatsUsersService.getChatsByUserId(userId)))
                    array.add(chat);
            }
            result.put("code", "200");
            result.put("chats", array);
        }
        return result.toJSONString();
    }

    public static String registration(JSONObject parse) throws SQLException {
        JSONObject result = new JSONObject();
        String userId = (String)parse.get("userId");
        String nickname = (String)parse.get("nickname");
        String password = (String)parse.get("password");
        if (!usersService.checkOfUsersExistence(userId)){
            User addUser = new User(userId, nickname, password);
            usersService.insertUser(addUser);
            result.put("code", "200");
            result.put("userId", userId);
            result.put("nickname", nickname);
            result.put("message", "Success registration");
        }
        else{
            result.put("code", "400");
            result.put("userId", userId);
            result.put("message", "User is already exist");
        }
        return result.toJSONString();
    }

    public static void onWebSocketConnectUser(JSONObject json, Session session) throws SQLException {
        LOGGER.debug(json.toJSONString());
        if (json.get("userId") != null){
            if (usersService.checkOfUsersExistence((String) json.get("userId"))) {
                ChatServer.addUser(session, (String) json.get("userId"));
            }
        }
    }

    public static void onWebSocketDisconnectUser(JSONObject json, Session session) throws SQLException {
        if (json.get("userId") != null){
            System.out.println("HAaaay");
            User user = usersService.getUserById((String) json.get("userId"));
            if (user.getUserId() != null) {
                ChatServer.deleteUser((String) json.get("userId"), session);
                if (ChatServer.getUserSessions(user.getUserId()).size() == 0){
                    user.setLastVisit(System.currentTimeMillis());
                    usersService.updateUserLastVisit(user);
                }
            }
        }
    }

    public static void onWebSocketMessage(JSONObject json) throws SQLException, IOException {
        Message message = parseToMessage(json);
        //System.out.println(json);//log input message
        if (message.getText() != null){
            int messageId = messagesService.insertMessage(message);
            message.setMessageId(messageId);
            Set<String> users = chatsUsersService.getUsersByChatId(message.getChatId());
            for (String user: users)
                if (ChatServer.getUserSessions(user) != null) {
                    JSONObject message1 = createJsonMessage(message);
                    message1.put("type", "message");
                    sendMessage(ChatServer.getUserSessions(user), message1);
                }
        }
    }

    public static String getChatsByUserId(JSONObject parse) throws SQLException {
        LOGGER.debug(parse.toJSONString());
        JSONObject jsonObject =  new JSONObject();
        String userId = (String) parse.get("userId");
        JSONArray array = new JSONArray();
        for(Chat chat: chatsUsersService.getChatsByUserId(userId))
            array.add(createJsonChat(chat));
        jsonObject.put("chats", array);
        System.out.println(jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }

    public static String getMessagesByChatId(JSONObject parse) throws SQLException{
        JSONObject jsonObject =  new JSONObject();
        JSONArray array = new JSONArray();
        int chatId = Integer.parseInt((String) parse.get("chatId"));
        int pageNum = Integer.parseInt((String) parse.get("pageNum"));
        int messageId = Integer.parseInt((String) parse.get("messageId"));
        int offset = Integer.parseInt((String) parse.get("offset"));
        Chat chat = chatsService.getChatById(chatId);
        if (chat.getChatId() != 0){
            if (messageId != 0) {
                for (Message message : messagesService.getMessagesByChatId(chatId, pageNum, messageId, offset))
                    array.add(createJsonMessage(message));
            }
            else {
                for (Message message : messagesService.getMessagesByChatId(chatId, pageNum))
                    array.add(createJsonMessage(message));
            }
        }
        jsonObject.put("messages", array);
        return jsonObject.toJSONString();
    }

    public static String getUserById(JSONObject parse) throws SQLException {
        JSONObject jsonObject =  new JSONObject();
        User user = usersService.getUserById((String) parse.get("userId"));
        if (user.getUserId() != null){
            jsonObject.put("code", "200");
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("nickname", user.getNickname());
            jsonObject.put("lastVisit", user.getLastVisit());
        }
        else
            jsonObject.put("code", "404");
        return jsonObject.toJSONString();
    }

    public static String createChat(JSONObject parse) throws SQLException, IOException {
        JSONObject result = new JSONObject();
        JSONObject message = new JSONObject();
        String userId = (String) parse.get("userId");
        String chatName = (String) parse.get("chatName");
        JSONArray users = (JSONArray) parse.get("users");
        if (!usersService.checkOfUsersExistence(userId)) {
            result.put("code", "404");
            result.put("message", "User is not found");
        } else {
            if (!chatName.equals("")) {
                int chatId = chatsService.insertChat(chatName);
                if (addUsersToChat(chatId, users, chatName)) {
                    if (chatsUsersService.insertChatsUsers(userId, chatId)) {
                        message.put("chatId", Integer.toString(chatId));
                        message.put("userId", userId);
                        message.put("text", "I create the chat");
                        Message message1 = parseToMessage(message);
                        JSONObject message2 = createJsonMessage(message1);
                        message2.put("type", "message");
                        int messageId = messagesService.insertMessage(message1);
                        result.put("code", "200");
                        result.put("message", "Success creating");
                        result.put("messageId", messageId);
                        result.put("chatId", chatId);
                        result.put("chatName", chatName);
                    }
                }
            } else {
                result.put("code", "300");
                result.put("message", "Invalid input data");
            }
        }
        return result.toJSONString();
    }

    public static String outUserFromChat(JSONObject parse) throws SQLException, IOException {
        JSONObject result = new JSONObject();
        JSONObject message = new JSONObject();
        String userId = (String) parse.get("userId");
        int chatId = Integer.parseInt((String) parse.get("chatId"));
        if (chatsService.checkOfChatExistence(chatId) &&
                usersService.checkOfUsersExistence(userId)) {
            message.put("chatId", Integer.toString(chatId));
            message.put("userId", userId);
            message.put("text", "I leave the chat");
            Message message1 = parseToMessage(message);
            JSONObject message2 = createJsonMessage(message1);
            message2.put("type", "message");
            int messageId = messagesService.insertMessage(message1);
                Set<String> users = chatsUsersService.getUsersByChatId(chatId);
                for (String user : users)
                    if (ChatServer.getUserSessions(user) != null)
                        sendMessage(ChatServer.getUserSessions(user), message2);
                chatsUsersService.outUserFromChat(chatId, userId);
                result.put("code", "200");
                result.put("message", "Success delete");
                result.put("messageId", messageId);
                result.put("chatId", chatId);
                result.put("userId", userId);
                ChatServer.addChatForCheck(chatId);
        }
        else{
            result.put("code", "300");
            result.put("message", "Invalid input data");
        }
        return result.toJSONString();
    }

    public static void deleteChat(int chatId){
        try {
            if (chatsService.checkOfChatExistence(chatId)){
                Iterator<String> users = chatsUsersService.getUsersByChatId(chatId).iterator();
                if (!users.hasNext())
                    chatsService.deleteChat(chatId);
                //log debug delete chat with chatid from database
            }
        } catch (SQLException e) {
            //log error delete chat from table
            e.printStackTrace();
        }
    }

    private static void sendMessage(Set<Session> users, JSONObject message) throws IOException {
        for (Session session: users){
            if (session.isOpen())
                session.getRemote().sendString(message.toJSONString());
        }
    }

    private static boolean addUsersToChat(int chatId, JSONArray users, String chatName) throws IOException {
        JSONObject message = new JSONObject();
        if (chatsUsersService.insertChatsUsers(users.iterator(), chatId)) {
            Iterator<JSONObject> usersIds = users.iterator();
            while (usersIds.hasNext()) {
                String user = (String) usersIds.next().get("userId");
                System.out.println("i am here iterator " + user);
                if (ChatServer.getUserSessions(user) != null) {
                    message.put("type", "newChat");
                    message.put("chatId", chatId);
                    message.put("chatName", chatName);
                    sendMessage(ChatServer.getUserSessions(user), message);
                }
            }
            return true;
        } else
            return false;
    }

    private static JSONObject createJsonMessage(Message message) throws SQLException {
        JSONObject jsonObject =  new JSONObject();
        User user = usersService.getUserById(message.getUserId());
        if (user.getUserId() != null) {
            jsonObject.put("messageId", message.getMessageId());
            jsonObject.put("userId", message.getUserId());
            jsonObject.put("nickname", user.getNickname());
            jsonObject.put("text", message.getText());
            jsonObject.put("timestamp", message.getTimestamp());
            jsonObject.put("chatId", message.getChatId());
        }
        return jsonObject;
    }

    private static Message parseToMessage(JSONObject parse){
        Message message  = new Message();
        if (parse.get("userId") != null && parse.get("chatId") != null && parse.get("text") != null){
            message.setChatId(Integer.parseInt((String) parse.get("chatId")));
            message.setUserId((String) parse.get("userId"));
            message.setText((String) parse.get("text"));
            message.setTimestamp(System.currentTimeMillis());
        }
        return message;
    }

    private static JSONObject createJsonChat(Chat chat){
        JSONObject jsonObject =  new JSONObject();
        jsonObject.put("chatId", chat.getChatId());
        jsonObject.put("chatName", chat.getChatName());
        return jsonObject;
    }
}

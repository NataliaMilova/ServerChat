package ru.ifmo.utils;

import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import ru.ifmo.ChatServer;
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

public class DataBaseUtils {

    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private static ChatsUsersService chatsUsersService = new ChatsUsersService();
    private static UsersService usersService = new UsersService();
    private static ChatsService chatsService = new ChatsService();
    private static MessagesService messagesService = new MessagesService();
    private static Logger LOGGER = LoggerFactory.getLogger(DataBaseUtils.class);

    static {
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
    }

    private static void createTables() throws SQLException {
        String chats = "CREATE TABLE IF NOT EXISTS chats (\n"
                + "	chatId integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	chatName varchar(255) NOT NULL\n"
                + ");";
        String users = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	userId varchar(12) NOT NULL UNIQUE,\n"
                + "	nickname varchar(100) NOT NULL,\n"
                + "	lastVisit bigint NOT NULL,\n"
                + "	password text NOT NULL,\n"
                + "	PRIMARY KEY (userId)\n"
                + ");";
        String messages = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	messageId integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	timestamp bigint NOT NULL,\n"
                + "	text text NOT NULL,\n"
                + "	userId varchar NOT NULL,\n"
                + "	chatId integer NOT NULL,\n"
                + "	FOREIGN KEY (userId) REFERENCES users (userId)\n"
                + "	FOREIGN KEY (chatId) REFERENCES chats (chatId) ON DELETE CASCADE\n"
                + ");";
        String chats_users = "CREATE TABLE IF NOT EXISTS chats_users (\n"
                + "	userId varchar NOT NULL,\n"
                + "	chatId integer NOT NULL,\n"
                + "	FOREIGN KEY (userId) REFERENCES users (userId)\n"
                + "	FOREIGN KEY (chatId) REFERENCES chats (chatId) ON DELETE CASCADE\n"
                + ");";
        try (Connection connection = dataSource.getPooledConnection().getConnection()) {
            createTable(connection, chats);
            createTable(connection, users);
            createTable(connection, messages);
            createTable(connection, chats_users);
        }
        LOGGER.info("create table");
    }

    private static void createTable(Connection connection, String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
        stmt.close();
    }

    public static boolean createDataBase(){
        try {
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String authorization(JSONObject parse) throws SQLException{
        JSONObject result = new JSONObject();
        String userId = (String)parse.get("userId");
        String password = (String)parse.get("password");
        User user = usersService.getUserById(userId, dataSource.getPooledConnection().getConnection());
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

    public static String registration(JSONObject parse) throws SQLException {
        JSONObject result = new JSONObject();
        String userId = (String)parse.get("userId");
        String nickname = (String)parse.get("nickname");
        String password = (String)parse.get("password");
        if (!usersService.checkOfUsersExistence(userId, dataSource.getPooledConnection().getConnection())){
            User addUser = new User(userId, nickname, password);
            usersService.insertUser(addUser, dataSource.getPooledConnection().getConnection());
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

    public static void onWebSocketConnectUser(JSONObject json, Session session) throws SQLException, IOException {
        System.out.println(json.toJSONString());
        if (json.get("userId") != null){
            if (usersService.checkOfUsersExistence((String) json.get("userId"), dataSource.getPooledConnection().getConnection())) {
                ChatServer.addUser(session, (String) json.get("userId"));
            }
        }
    }

    public static void onWebSocketDisconnectUser(JSONObject json, Session session) throws SQLException, IOException {
        System.out.println(json.toJSONString());
        if (json.get("userId") != null){
            User user = usersService.getUserById((String) json.get("userId"), dataSource.getPooledConnection().getConnection());
            if (user.getUserId() != null) {
                ChatServer.deleteUser((String) json.get("userId"), session);
            }
            usersService.updateUserLastVisit(user, dataSource.getPooledConnection().getConnection());
        }
    }

    public static void onWebSocketMessage(JSONObject json) throws SQLException, IOException {
        Message message = parseToMessage(json);
        //System.out.println(json);//log input message
        if (message.getText() != null){
            messagesService.insertMessage(message, dataSource.getPooledConnection().getConnection());
            Set<String> users = chatsUsersService.getUsersByChatId(message.getChatId(), dataSource.getPooledConnection().getConnection());
            for (String user: users)
                if (ChatServer.getUserSessions(user) != null){
                    for (Session session: ChatServer.getUserSessions(user)){
                        if (session.isOpen()){
                            JSONObject message1 = createJsonMessage(message);
                            message1.put("type", "message");
                            session.getRemote().sendString(message1.toJSONString());
                        }
                    }
                }
        }
    }

    public static String getChatsByUserId(JSONObject parse) throws SQLException {
        JSONObject jsonObject =  new JSONObject();
        String userId = (String) parse.get("userId");
        JSONArray array = new JSONArray();
        for(Chat chat: chatsUsersService.getChatsByUserId(userId, dataSource.getPooledConnection().getConnection()))
            array.add(createJsonChat(chat));
        jsonObject.put("chats", array);
        return jsonObject.toJSONString();
    }

    public static String getMessagesByChatId(JSONObject parse) throws SQLException{
        JSONObject jsonObject =  new JSONObject();
        JSONArray array = new JSONArray();
        int chatId = Integer.parseInt((String) parse.get("chatId"));
        Chat chat = chatsService.getChatById(chatId, dataSource.getPooledConnection().getConnection());
        if (chat.getChatId() != 0){
            for(Message message: messagesService.getMessagesByChatId(chatId, dataSource.getPooledConnection().getConnection()))
                array.add(createJsonMessage(message));
        }
        jsonObject.put("messages", array);
        return jsonObject.toJSONString();
    }

    public static String getUserById(JSONObject parse) throws SQLException {
        JSONObject jsonObject =  new JSONObject();
        User user = usersService.getUserById((String) parse.get("userId"), dataSource.getPooledConnection().getConnection());
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
        if (!usersService.checkOfUsersExistence(userId, dataSource.getPooledConnection().getConnection())) {
            result.put("code", "404");
            result.put("message", "User is not found");
        } else {
            if (!chatName.equals("")) {
                int chatId = chatsService.insertChat(chatName, dataSource.getPooledConnection().getConnection());
                if (chatId != -1) {
                    Iterator<JSONObject> iterator = users.iterator();
                    if (chatsUsersService.insertChatsUsers(iterator, chatId, dataSource.getPooledConnection().getConnection())) {
                        chatsUsersService.insertChatsUsers(userId, chatId, dataSource.getPooledConnection().getConnection());
                        result.put("code", "200");
                        result.put("message", "Success creating");
                        result.put("chatId", chatId);
                        result.put("chatName", chatName);
                        Iterator<JSONObject> usersIds = users.iterator();
                        while (usersIds.hasNext()){
                            String user = (String) usersIds.next().get("userId");
                            System.out.println("i am here iterator " + user);
                            if (ChatServer.getUserSessions(user) != null){
                                System.out.println();
                                for (Session session: ChatServer.getUserSessions(user)){
                                    if (session.isOpen()){
                                        System.out.println(user);
                                        message.put("type", "newChat");
                                        message.put("chatId", chatId);
                                        message.put("chatName", chatName);
                                        session.getRemote().sendString(message.toJSONString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else{
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
        if (chatsService.checkOfChatExistence(chatId, dataSource.getPooledConnection().getConnection()) &&
                usersService.checkOfUsersExistence(userId, dataSource.getPooledConnection().getConnection())){
            chatsUsersService.outUserFromChat(chatId, userId, dataSource.getPooledConnection().getConnection());
            message.put("chatId", chatId);
            message.put("userId", userId);
            message.put("text", "I leave the chat");
            onWebSocketMessage(message);
            result.put("code", "200");
            result.put("message", "Success delete");
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
            if (chatsService.checkOfChatExistence(chatId, dataSource.getPooledConnection().getConnection())){
                Iterator<String> users = chatsUsersService.getUsersByChatId(chatId,dataSource.getPooledConnection().getConnection()).iterator();
                if (!users.hasNext())
                    chatsService.deleteChat(chatId, dataSource.getPooledConnection().getConnection());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static JSONObject createJsonMessage(Message message) throws SQLException {
        JSONObject jsonObject =  new JSONObject();
        User user = usersService.getUserById(message.getUserId(), dataSource.getPooledConnection().getConnection());
        if (user.getUserId() != null) {
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

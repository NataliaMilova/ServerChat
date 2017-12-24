package ru.ifmo.utils;

import org.eclipse.jetty.websocket.api.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import ru.ifmo.ChatServer;
import ru.ifmo.entity.Message;
import ru.ifmo.entity.User;
import ru.ifmo.services.ChatsService;
import ru.ifmo.services.ChatsUsersService;
import ru.ifmo.services.MessagesService;
import ru.ifmo.services.UsersService;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class DataBaseUtils {

    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private static ChatsUsersService chatsUsersService = new ChatsUsersService();
    private static UsersService usersService = new UsersService();
    private static ChatsService chatsService = new ChatsService();
    private static MessagesService messagesService = new MessagesService();

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
                + "	messageID integer PRIMARY KEY AUTOINCREMENT,\n"
                + "	timestamp bigint NOT NULL,\n"
                + "	text text NOT NULL,\n"
                + "	userId varchar NOT NULL,\n"
                + "	chatId integer NOT NULL,\n"
                + "	FOREIGN KEY (userId) REFERENCES users (userId)\n"
                + "	FOREIGN KEY (chatId) REFERENCES chats (chatId)\n"
                + ");";
        String chats_users = "CREATE TABLE IF NOT EXISTS chats_users (\n"
                + "	userId varchar NOT NULL,\n"
                + "	chatId integer NOT NULL,\n"
                + "	FOREIGN KEY (userId) REFERENCES users (userId)\n"
                + "	FOREIGN KEY (chatId) REFERENCES chats (chatId)\n"
                + ");";
        Connection connection = dataSource.getPooledConnection().getConnection();
        createTable(connection, chats);
        createTable(connection, users);
        createTable(connection, messages);
        createTable(connection, chats_users);
        connection.close();
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

    public static ConcurrentHashMap<Integer, Set<Session>> getAllChats(){
        ConcurrentHashMap<Integer, Set<Session>> result =  new ConcurrentHashMap<>();
        try {
            Set<Integer> chats = chatsService.getAllChatsId(dataSource.getPooledConnection().getConnection());
            for (int chat: chats){
                result.put(chat, new HashSet<Session>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Set<Integer> getChatsByUserId(String userId){
        Set<Integer> chats = new HashSet<>();
        try {
            chats = chatsUsersService.getChatsIdByUserId(userId, dataSource.getPooledConnection().getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chats;
    }

    public static String authorization(String json) throws SQLException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject result = new JSONObject();
        JSONObject parse = (JSONObject) parser.parse(json);
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

    public static String registration(String json) throws SQLException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject result = new JSONObject();
        JSONObject parse = (JSONObject) parser.parse(json);
        String userId = (String)parse.get("userId");
        String nickname = (String)parse.get("nickname");
        String password = (String)parse.get("password");
        User user = usersService.getUserById(userId, dataSource.getPooledConnection().getConnection());
        if (user.getUserId() == null){
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
        JSONObject result = new JSONObject();
        System.out.println(json.toJSONString());
        if (json.get("userId") != null){
            User user = usersService.getUserById((String) json.get("userId"), dataSource.getPooledConnection().getConnection());
            if (user.getUserId() != null){
                ChatServer.addUser(session, (String)json.get("userId"));
                result.put("type","тру чувак");
                session.getRemote().sendString(result.toJSONString());
            }
            else{
                result.put("type","пошел нафиг");
                session.getRemote().sendString(result.toJSONString());
            }
            System.out.println(ChatServer.allSessions());
        }
    }

    public static void onWebSocketMessage(String json) throws ParseException, SQLException, IOException {
        Message message = parseToMessage(json);
        System.out.println(json);//log input message
        if (message.getText() != null){
            Set<String> users = chatsUsersService.getUsersByChatId(message.getChatId(), dataSource.getPooledConnection().getConnection());
            for (String user: users)
                if (ChatServer.getUserSession(user) != null && ChatServer.getUserSession(user).isOpen())
                    ChatServer.getUserSession(user).getRemote().sendString(createJsonMessage(message).toJSONString());
            messagesService.insertMessage(message, dataSource.getPooledConnection().getConnection());
        }
    }

    private static JSONObject createJsonMessage(Message message) throws SQLException {
        JSONObject jsonObject =  new JSONObject();
        User user = usersService.getUserById(message.getUserId(), dataSource.getPooledConnection().getConnection());
        if (user.getUserId() != null && ChatServer.getUserSession(user.getUserId()) != null)
            jsonObject.put("type", "message");
            jsonObject.put("chatId", message.getChatId());
            jsonObject.put("userId", message.getUserId());
            jsonObject.put("nickname", user.getNickname());
            jsonObject.put("text", message.getText());
            jsonObject.put("timestamp", new Date(message.getTimestamp()));
        return jsonObject;
    }

    private static Message parseToMessage(String json) throws ParseException {
        Message message  = new Message();
        JSONParser parser = new JSONParser();
        JSONObject parse = (JSONObject) parser.parse(json);
        if (parse.get("userId") != null && parse.get("chatId") != null && parse.get("text") != null){
            message.setChatId(Integer.parseInt((String) parse.get("chatId")));
            message.setUserId((String) parse.get("userId"));
            message.setText((String) parse.get("text"));
            message.setTimestamp(System.currentTimeMillis());
        }
        return message;
    }
}

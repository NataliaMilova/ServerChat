package ru.ifmo;

import org.eclipse.jetty.websocket.api.Session;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.sql.*;
import java.util.*;

public class DataBaseUtils {

    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();

    private static final String CREATE_CHATS_TABLE;
    private static final String CREATE_USERS_TABLE;
    private static final String CREATE_MESSAGES_TABLE;
    private static final String CREATE_CHATS_USERS_TABLE;

    static {
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        CREATE_CHATS_TABLE = "CREATE TABLE IF NOT EXISTS chats (\n"
                + "	chatId integer AUTO_INCREMENT NOT NULL UNIQUE,\n"
                + "	chatName varchar(255) NOT NULL,\n"
                + "	PRIMARY KEY (chatId)\n"
                + ");";
        CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	userId varchar(12) NOT NULL UNIQUE,\n"
                + "	nickname varchar(100) NOT NULL,\n"
                + "	lastVisit bigint NOT NULL,\n"
                + "	password integer NOT NULL,\n"
                + "	PRIMARY KEY (userId)\n"
                + ");";
        CREATE_MESSAGES_TABLE = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	messageID bigint AUTO_INCREMENT NOT NULL UNIQUE,\n"
                + "	timestamp bigint NOT NULL,\n"
                + "	text text NOT NULL,\n"
                + "	status int NOT NULL,\n"
                + "	userId varchar NOT NULL,\n"
                + "	chatId integer NOT NULL,\n"
                + "	PRIMARY KEY (messageID)\n"
                + "	FOREIGN KEY (userId) REFERENCES users (userId)\n"
                + "	FOREIGN KEY (chatId) REFERENCES chats (chatId)\n"
                + ");";
        CREATE_CHATS_USERS_TABLE = "CREATE TABLE IF NOT EXISTS chats_users (\n"
                + "	userId varchar NOT NULL,\n"
                + "	chatId integer NOT NULL,\n"
                + "	FOREIGN KEY (userId) REFERENCES users (userId)\n"
                + "	FOREIGN KEY (chatId) REFERENCES chats (chatId)\n"
                + ");";
    }


    public static void createDB() throws SQLException {
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
        //String url = "jdbc:sqlite:./ChatServer.db";
        //поверка на наличие файла и вывод в лог
        //SQLiteDataSource dataSource = new SQLiteDataSource();
        //dataSource.setUrl(url);
        Connection connection = dataSource.getPooledConnection().getConnection();
        if (connection != null) {
            System.out.println("connection complete");
            createTable(connection, CREATE_CHATS_TABLE);
            System.out.println("chats table create or exists");//log
            createTable(connection, CREATE_MESSAGES_TABLE);
            System.out.println("messages table create or exists");//log
            createTable(connection, CREATE_USERS_TABLE);
            System.out.println("users table create or exists");//log
            createTable(connection, CREATE_CHATS_USERS_TABLE);
            System.out.println("chats_users table create or exists");//log
            connection.close();
        }
    }

    private static void createTable(Connection connection, String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
    }

    public static void getChats(){
        //String url = "jdbc:sqlite:./ChatServer.db";
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
        //поверка на наличие файла и вывод в лог
        try {
            Connection connection = dataSource.getPooledConnection().getConnection();
            String sql = "SELECT chatId FROM chats;";
            Statement st = connection.createStatement();
            ResultSet result = st.executeQuery(sql);
            while (result.next()){
                int chatId = result.getInt("chatId");
                if (JettyStarter.users.get(chatId) == null)
                    JettyStarter.users.put(chatId, new ArrayList<Session>());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Set<Integer> getChatsByUserId(String id){
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
        Set<Integer> resultList = new HashSet<>();
        //String url = "jdbc:sqlite:./ChatServer.db";
        //поверка на наличие файла и вывод в лог
        try {
            Connection connection = dataSource.getPooledConnection().getConnection();
            String sql = "SELECT chatId FROM chats_users WHERE userId = " + "'" + id + "'"+";";
            Statement st = connection.createStatement();
            ResultSet result = st.executeQuery(sql);
            while (result.next()){
                resultList.add(result.getInt("chatId"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }
    //prepared statement!!!!
    public static void main(String[] args) {
        //String url = "jdbc:sqlite:./ChatServer.db";
        /*dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
        dataSource.setEnforceForeignKeys(true);*/
        //поверка на наличие файла и вывод в лог
        try {
            Connection connection = dataSource.getPooledConnection().getConnection();
            String sql = "INSERT INTO chats_users(chatId, userId) VALUES(?,?)";
            //String sql = "INSERT INTO chats(chatName ,userId) VALUES(?,?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, 1);
            pstmt.setString(2, "zoo");
            //pstmt.setString(3, "+79141385421");
            pstmt.executeUpdate();
            System.out.println("success");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }
}

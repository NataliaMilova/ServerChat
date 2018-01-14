package ru.ifmo;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import ru.ifmo.utils.ChatServerUtils;
import ru.ifmo.websocket.SocketServlet;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.LogManager;


public class ChatServer {
    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();
    private static Map<String, Set<Session>> users = new ConcurrentHashMap<>();
    private static BlockingDeque<Integer> chatsForCheck = new LinkedBlockingDeque<>();
    private static Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);
    private static File dbDir = new File(System.getProperty("user.home") + "/chat");

    static{
        if (!dbDir.exists())
            dbDir.mkdirs();
        dataSource.setUrl("jdbc:sqlite:" + dbDir.getAbsolutePath().toString() + "/ChatServer.db");
        dataSource.setEnforceForeignKeys(true);
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    public static void main(String[] args) {
        if (ChatServerUtils.createDataBase()) {
            Thread deleteWorker = new Worker();
            deleteWorker.setName("DeleteThread");
            deleteWorker.start();
            LOGGER.info("Started " + deleteWorker);
            //log info deleteworker started

            ResourceConfig config = new ResourceConfig();
            config.packages("ru.ifmo");
            ServletHolder servlet = new ServletHolder(new ServletContainer(config));

            Server server = new Server(8080);
            ServletContextHandler context = new ServletContextHandler(server, "/*");
            context.addServlet(servlet, "/*");

            context.addServlet(SocketServlet.class, "/ws/*");

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{context});

            server.setHandler(handlers);

            try {
                server.start();
                LOGGER.info("Started" );
                server.join();
            } catch (Exception e) {
                LOGGER.error("", e);
                deleteWorker.interrupt();
            }
            finally {
                server.destroy();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getPooledConnection().getConnection();
    }

    public static void addUser(Session session, String userId){
        if (users.get(userId) == null)
            users.put(userId, new HashSet<Session>());
        users.get(userId).add(session);
        //log debug add user and session
    }

    public static Set<Session> getUserSessions(String userId){
        return users.get(userId);
    }

    public static void deleteUser(String userId, Session session){
        users.get(userId).remove(session);
        //log debug delete user id and session
    }

    public static void addChatForCheck(int chatId){
        chatsForCheck.addLast(chatId);
        //log debug chat + chatid add for checking by deleteworker
    }

    public static class Worker extends Thread{
        @Override
        public void run() {
            while (!isInterrupted()){
                try {
                    int chatId = chatsForCheck.takeFirst();
                    //log debug task chatid
                    System.out.println("i wake up");
                    ChatServerUtils.deleteChat(chatId);
                } catch (InterruptedException e) {
                    interrupt();
                    e.printStackTrace();
                }
            }
        }
    }
}

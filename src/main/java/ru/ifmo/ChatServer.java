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
import ru.ifmo.utils.DataBaseUtils;
import ru.ifmo.websocket.SocketServlet;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.LogManager;


public class ChatServer {
    private static Map<String, Set<Session>> users = new ConcurrentHashMap<>();
    private static BlockingDeque<Integer> chatsForCheck = new LinkedBlockingDeque<>();
    private static Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);

    static{
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    public static void main(String[] args) {
        if (DataBaseUtils.createDataBase()) {
            LOGGER.info("test message");
            Thread deleteWorker = new Worker();
            deleteWorker.start();
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
                server.join();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                deleteWorker.interrupt();
            }
        }
        else
            System.out.println("database error!");
    }

    public static void addUser(Session session, String userId){
        if (users.get(userId) == null)
            users.put(userId, new HashSet<Session>());
        users.get(userId).add(session);
    }

    public static Set<Session> getUserSessions(String userId){
        return users.get(userId);
    }

    public static void deleteUser(String userId, Session session){
        users.get(userId).remove(session);
    }

    public static void addChatForCheck(int chatId){
        chatsForCheck.addLast(chatId);
    }

    public static class Worker extends Thread{
        @Override
        public void run() {
            while (!isInterrupted()){
                try {
                    int chatId = chatsForCheck.takeFirst();
                    System.out.println("i am wakeup");
                    DataBaseUtils.deleteChat(chatId);
                } catch (InterruptedException e) {
                    interrupt();
                    e.printStackTrace();
                }
            }
        }
    }
}


package ru.ifmo.server;

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
import ru.ifmo.utils.ChatServerUtils;
import ru.ifmo.websocket.SocketServlet;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.LogManager;


public class ChatServer {
    private static Map<String, ArrayList<Session>> users = new ConcurrentHashMap<>();
    private static BlockingDeque<Long> chatsForCheck = new LinkedBlockingDeque<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServer.class);

    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    public static void main(String[] args) {
        if (ChatServerUtils.createDataBase()) {
            Thread cleaner = new Cleaner();

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
                LOGGER.info("Started");
                cleaner.setName("Cleaner");
                cleaner.start();
                LOGGER.info("Started " + cleaner);
                server.join();
            } catch (Exception e) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("Server error", e);
                cleaner.interrupt();
            } finally {
                server.destroy();
            }
        }
    }

    public static void addUser(Session session, String userId) {
        if (users.get(userId) == null)
            users.put(userId, new ArrayList<>());
        users.get(userId).add(session);
        LOGGER.debug("Add session " + session.getRemoteAddress() + " to userId " + userId);
    }

    public static ArrayList<Session> getUserSessions(String userId) {
        return users.get(userId);
    }

    public static void deleteUser(String userId, Session session) {
        users.get(userId).remove(session);
        LOGGER.info("Delete session " + session.getRemoteAddress() + " from userId " + userId);
    }

    public static void addChatForCheck(long chatId) {
        chatsForCheck.addLast(chatId);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Add chatId " + chatId + " for checking by cleaner");
    }

    public static class Cleaner extends Thread {
        private static final Logger LOGGER = LoggerFactory.getLogger(Cleaner.class);

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    long chatId = chatsForCheck.takeFirst();
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Get chatId " + chatId + " for checking");
                    ChatServerUtils.deleteChat(chatId);
                } catch (InterruptedException e) {
                    interrupt();
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug("Cleaner was interrupt", e);
                }
            }
        }
    }
}

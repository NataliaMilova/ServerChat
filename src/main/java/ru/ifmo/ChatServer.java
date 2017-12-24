package ru.ifmo;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import ru.ifmo.utils.DataBaseUtils;
//import ru.ifmo.websocket.SocketHandler;
import ru.ifmo.websocket.SocketServlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class ChatServer {
    private static Map<String, Session> users = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        if (DataBaseUtils.createDataBase()) {

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
            }
        }
        else
            System.out.println("database error!");
    }

    public static void addUser(Session session, String userId){
        users.put(userId, session);
    }

    public static Session getUserSession(String userId){
        return users.get(userId);
    }

    public static void deleteUser(String userId){
        users.remove(userId);
    }

    public static Map<String, Session> allSessions(){
        return users;
    }
}

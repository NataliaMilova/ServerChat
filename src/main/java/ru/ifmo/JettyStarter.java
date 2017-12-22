package ru.ifmo;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.api.Session;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JettyStarter {
    static Map<Session, Integer> userUsernameMap = new ConcurrentHashMap<>();
    static int memberId = 0;

    static Map<Integer, List<Session>> users = new ConcurrentHashMap<>();

    static {
        DataBaseUtils.getChats();
    }

    public static void main(String[] args) {
        System.out.println(users);
        try {
            DataBaseUtils.createDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ResourceConfig config = new ResourceConfig();
        config.packages("ru.ifmo");
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, "/socket");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {new SocketHandler(), context});

        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}

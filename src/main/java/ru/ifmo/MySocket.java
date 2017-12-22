package ru.ifmo;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebSocket
public class MySocket {

     /*логика отправки сообщения в нужный чат:
     * когда поднимется сервак, он должен сохранить все чаты, которые есть в мапу,
     * ключ которой айди чата, а значение все сессии юзверей находящихся в данном чате
     * при подключении юзверя берутся все чаты из бд и он добавляется в список сессий
     * при отправке сообщения от клиента приходит айди чата, в который написал юзверь
     * поэтому отсылаем сообщение мы только живым сессиям в чате и сохраняем сообщение в бд*/


    private int chatId = 1;
    private String user1 = "+79141385421";
    private String user2 = "+79141385422";
    private String user3 = "+79141385423";
    /*@OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {
            JettyStarter.userUsernameMap.put(session, JettyStarter.memberId++);
            for (Map.Entry<Session, Integer> member: JettyStarter.userUsernameMap.entrySet())
                System.out.println(member.getKey().getRemoteAddress());
            session.getRemote().sendString("Got your connect message ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @OnWebSocketConnect
    public void onConnect(Session session) {
        String user = null;
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        try {
            session.getRemote().sendString("Got your connect message ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JettyStarter.userUsernameMap.put(session, JettyStarter.memberId++);
        System.out.println(JettyStarter.userUsernameMap.size());
        if (JettyStarter.userUsernameMap.size() == 1)
            user = user1;
        else if (JettyStarter.userUsernameMap.size() == 2)
            user = user2;
        else
            user = user3;
        System.out.println(user);
        Set<Integer> chatsForUser = DataBaseUtils.getChatsByUserId(user);
        System.out.println(chatsForUser);
        for (Integer chat: chatsForUser){
            JettyStarter.users.get(chat).add(session);
        }
        System.out.println(JettyStarter.users);
    }

   /* @OnWebSocketMessage
    public void onNessage(String message) {
        System.out.println("text: " + message);
        try {
            for (Map.Entry<Session, Integer> member: JettyStarter.userUsernameMap.entrySet()){
                System.out.println(member.getKey().getRemoteAddress());
                if (member.getKey().isOpen())
                    member.getKey().getRemote().sendString(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @OnWebSocketMessage
    public void onNessage(String message) {
        System.out.println("text: " + message);
        System.out.println(JettyStarter.users.get(chatId));
        try {
            for (Session session: JettyStarter.users.get(chatId)){
                session.getRemote().sendString(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }
}

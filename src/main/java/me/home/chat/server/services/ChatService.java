package me.home.chat.server.services;


import me.home.chat.server.models.chats.Chat;
import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.List;

public interface ChatService {
    void deleteChat(long id);
    boolean checkOfChatExistence(long id);
    Chat getChatById(long id);
    long insertChat(String chatName);
    List<Chat> getChatsByUserId(String id);
    void outUserFromChat(long chatId, String userId);
    boolean insertChatsUsers(String userId, long chatId);
    boolean insertChatsUsers(Iterator<JSONObject> userId, long chatId);
}

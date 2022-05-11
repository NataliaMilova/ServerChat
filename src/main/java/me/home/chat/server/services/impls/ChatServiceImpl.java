package me.home.chat.server.services.impls;

import me.home.chat.server.models.chats.Chat;
import me.home.chat.server.services.ChatService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Override
    public void deleteChat(long id) {

    }

    @Override
    public boolean checkOfChatExistence(long id) {
        return false;
    }

    @Override
    public Chat getChatById(long id) {
        return null;
    }

    @Override
    public long insertChat(String chatName) {
        return 0;
    }

    @Override
    public List<Chat> getChatsByUserId(String id) {
        return null;
    }

    @Override
    public void outUserFromChat(long chatId, String userId) {

    }

    @Override
    public boolean insertChatsUsers(String userId, long chatId) {
        return false;
    }

    @Override
    public boolean insertChatsUsers(Iterator<JSONObject> userId, long chatId) {
        return false;
    }
}

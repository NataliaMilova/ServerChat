package me.home.chat.server.services;


import me.home.chat.server.models.chats.Message;
import ru.ifmo.old.entity.Chat;
import ru.ifmo.old.entity.User;

import java.util.List;

public interface MessageService {
    Message getMessageById(long messageId);
    List<Message> getMessagesByChatId(long chatId, int pageNum, int messageId, int off);
    int getCountOfNextMessagesInChat(int messageId, long chatId);
    List<Message> getMessagesByChatId(long chatId, int pageNum);
    List<Message> getMessagesByChatId(long chatId);
    long insertMessage(Message message);
    List<Long> getChatsWithNewMessagesByUserId(User user, List<Chat> chats);
    void deleteMessageById(long messageId);
}

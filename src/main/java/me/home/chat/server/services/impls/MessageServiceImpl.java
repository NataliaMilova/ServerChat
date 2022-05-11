package me.home.chat.server.services.impls;

import me.home.chat.server.models.chats.Message;
import me.home.chat.server.services.MessageService;
import org.springframework.stereotype.Service;
import ru.ifmo.old.entity.Chat;
import ru.ifmo.old.entity.User;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Override
    public Message getMessageById(long messageId) {
        return null;
    }

    @Override
    public List<Message> getMessagesByChatId(long chatId, int pageNum, int messageId, int off) {
        return null;
    }

    @Override
    public int getCountOfNextMessagesInChat(int messageId, long chatId) {
        return 0;
    }

    @Override
    public List<Message> getMessagesByChatId(long chatId, int pageNum) {
        return null;
    }

    @Override
    public List<Message> getMessagesByChatId(long chatId) {
        return null;
    }

    @Override
    public long insertMessage(Message message) {
        return 0;
    }

    @Override
    public List<Long> getChatsWithNewMessagesByUserId(User user, List<Chat> chats) {
        return null;
    }

    @Override
    public void deleteMessageById(long messageId) {

    }
}

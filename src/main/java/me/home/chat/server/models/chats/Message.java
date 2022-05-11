package me.home.chat.server.models.chats;

import me.home.chat.server.models.users.User;

import java.time.LocalDateTime;

public class Message {
    private long id;
    private Chat chat;
    private LocalDateTime publishDate;
    private User user;
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        return 31 * (1 + Long.hashCode(id) + Long.hashCode(chat.getId()) + publishDate.hashCode() + user.getUsername().hashCode() + text.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (hashCode() != obj.hashCode()) return false;
        if (!(obj instanceof Message)) return false;
        Message other = (Message) obj;
        if (this == other) return true;
        if (id != other.id) return false;
        if (!chat.equals(other.chat)) return false;
        if (!publishDate.equals(other.publishDate)) return false;
        if (!user.equals(other.user)) return false;
        return text.equals(other.text);
    }
}

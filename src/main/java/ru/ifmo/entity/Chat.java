package ru.ifmo.entity;

public class Chat {
    private int chatId;
    private String chatName;

    public Chat(int chatId, String chatName) {
        this.chatId = chatId;
        this.chatName = chatName;
    }

    public Chat() {
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}

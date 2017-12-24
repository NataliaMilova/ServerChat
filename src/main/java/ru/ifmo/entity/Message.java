package ru.ifmo.entity;

public class Message {
    private long messageId;
    private long timestamp;
    private String text;
    private String userId;
    private int chatId;

    public Message(long timestamp, String text, String userId, int chatId) {
        this.timestamp = timestamp;
        this.text = text;
        this.userId = userId;
        this.chatId = chatId;
    }

    public Message() {
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }
}

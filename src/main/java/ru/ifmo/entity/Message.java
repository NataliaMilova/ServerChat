package ru.ifmo.entity;

import java.util.Objects;

public class Message {
    private long messageId;
    private long timestamp;
    private String text;
    private String userId;
    private long chatId;

    public Message(String text, String userId, long chatId) {
        this.text = text;
        this.userId = userId;
        this.chatId = chatId;
    }

    public Message(long messageId, String text, String userId, long chatId) {
        this.messageId = messageId;
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

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return messageId == message.messageId &&
                timestamp == message.timestamp &&
                chatId == message.chatId &&
                Objects.equals(text, message.text) &&
                Objects.equals(userId, message.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(messageId, timestamp, text, userId, chatId);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", timestamp=" + timestamp +
                ", text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", chatId=" + chatId +
                '}';
    }
}

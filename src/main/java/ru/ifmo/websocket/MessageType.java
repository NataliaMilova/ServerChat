package ru.ifmo.websocket;

public enum MessageType {
    AUTHORIZATION,
    MESSAGE;

    public static MessageType getMessageType(String type){
        if (type.equals("authorization"))
            return AUTHORIZATION;
        return MESSAGE;
    }
}

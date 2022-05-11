package ru.ifmo.old.websocket;

public enum MessageType {
    AUTHORIZATION,
    EXIT,
    MESSAGE;

    public static MessageType getMessageType(String type){
        if (type.equals("authorization"))
            return AUTHORIZATION;
        if (type.equals("exit"))
            return EXIT;
        return MESSAGE;
    }
}

package com.example.chatroom;

public class ChatMessage {
    private String name;
    private String content;
    private long timestamp;

    public ChatMessage(String name, String content, long t) {
        this.name = name;
        this.content = content;
        this.timestamp = t;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

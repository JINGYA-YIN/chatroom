package com.example.chatroom.model;

public class ChatMessage {
    public enum Type { PUBLIC, PRIVATE, SYSTEM }

    private String sender;
    private String receiver;
    private String content;
    private long timestamp;
    private Type type;

    public ChatMessage() {
        this.timestamp = System.currentTimeMillis();
    }

    // 构造方法 - 公开/系统消息
    public ChatMessage(String sender, String content, Type type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    // 构造方法 - 私聊消息
    public ChatMessage(String sender, String receiver, String content, Type type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public String getContent() { return content; }
    public long getTimestamp() { return timestamp; }
    public Type getType() { return type; }

    public void setSender(String sender) { this.sender = sender; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public void setContent(String content) { this.content = content; }
    public void setType(Type type) { this.type = type; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
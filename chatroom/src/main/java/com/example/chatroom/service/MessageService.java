package com.example.chatroom.service;

import com.example.chatroom.model.ChatMessage;
import jakarta.servlet.ServletContext;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class MessageService {

    private static final String MESSAGES_KEY = "chat.messages";

    // 修正方法：使用 synchronized 确保线程安全地初始化列表
    @SuppressWarnings("unchecked")
    private List<ChatMessage> getSharedMessages(ServletContext context) {

        Object messages = context.getAttribute(MESSAGES_KEY);

        if (messages == null) {
            // 使用同步锁确保在高并发环境下，列表只会被初始化一次
            synchronized (context) {
                messages = context.getAttribute(MESSAGES_KEY);
                if (messages == null) {
                    messages = new CopyOnWriteArrayList<ChatMessage>();
                    context.setAttribute(MESSAGES_KEY, messages);
                }
            }
        }

        return (List<ChatMessage>) messages;
    }

    public void addMessage(ChatMessage message, ServletContext context) {
        if (message.getContent() == null || message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        getSharedMessages(context).add(message);
    }

    public void addSystemMessage(String content, ServletContext context) {
        ChatMessage systemMsg = new ChatMessage("系统通知", null, content, ChatMessage.Type.SYSTEM);
        getSharedMessages(context).add(systemMsg);
    }

    public List<ChatMessage> getAllMessages(ServletContext context) {
        return getSharedMessages(context);
    }

    public List<ChatMessage> getMessagesForUser(String username, long lastTimestamp, ServletContext context) {
        List<ChatMessage> allMessages = getSharedMessages(context);

        return allMessages.stream()
                .filter(msg -> msg.getTimestamp() > lastTimestamp)
                .filter(msg -> {
                    if (msg.getType() == ChatMessage.Type.PUBLIC || msg.getType() == ChatMessage.Type.SYSTEM) {
                        return true;
                    }
                    return username.equals(msg.getReceiver()) || username.equals(msg.getSender());
                })
                .collect(Collectors.toList());
    }
}
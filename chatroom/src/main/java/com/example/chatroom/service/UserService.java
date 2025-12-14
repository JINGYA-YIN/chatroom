package com.example.chatroom.service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private final MessageService messageService = new MessageService();
    private static final String ONLINE_USERS_KEY = "chat.onlineUsers";

    @SuppressWarnings("unchecked")
    private Set<String> getOnlineUsersSet(ServletContext context) {

        Object onlineUsers = context.getAttribute(ONLINE_USERS_KEY);

        if (onlineUsers == null) {
            // 使用同步锁确保只初始化一次
            synchronized (context) {
                onlineUsers = context.getAttribute(ONLINE_USERS_KEY);
                if (onlineUsers == null) {
                    onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
                    context.setAttribute(ONLINE_USERS_KEY, onlineUsers);
                }
            }
        }

        return (Set<String>) onlineUsers;
    }

    public void login(String username, HttpSession session, ServletContext context) {
        if (username.equalsIgnoreCase("系统通知")) {
            throw new IllegalArgumentException("用户名不能为 '系统通知'");
        }

        Set<String> onlineUsers = getOnlineUsersSet(context);
        if (onlineUsers.contains(username)) {
            throw new IllegalStateException("用户 " + username + " 已在线");
        }

        session.setAttribute("username", username);
        onlineUsers.add(username);
        messageService.addSystemMessage(username + " 进入了聊天室", context);
    }

    public void processLogout(String username, ServletContext context) {
        Set<String> onlineUsers = getOnlineUsersSet(context);
        if (onlineUsers.remove(username)) {
            messageService.addSystemMessage(username + " 退出了聊天室", context);
        }
    }

    public Set<String> getOnlineUsers(ServletContext context) {
        return getOnlineUsersSet(context);
    }
}
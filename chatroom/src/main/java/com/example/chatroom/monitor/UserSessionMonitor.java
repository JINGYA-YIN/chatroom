package com.example.chatroom.monitor;

import com.example.chatroom.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebListener
public class UserSessionMonitor implements HttpSessionListener {

    private final UserService userService = new UserService();

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // 1. 获取 HttpSession
        HttpSession session = se.getSession();

        // 2. 通过 HttpSession 获取 ServletContext
        ServletContext context = session.getServletContext();

        String username = (String) session.getAttribute("username");

        if (username != null) {
            // 用户退出，委托给 UserService 处理移除和系统消息发送
            userService.processLogout(username, context);
        }
    }
}
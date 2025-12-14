package com.example.chatroom.controller;

import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet("/chat")
public class ChatPageController extends HttpServlet {
    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            response.sendRedirect(request.getContextPath() + "/views/index.jsp");
            return;
        }

        List<ChatMessage> messages = messageService.getAllMessages(getServletContext());
        Set<String> onlineUsers = userService.getOnlineUsers(getServletContext());

        request.setAttribute("messages", messages);
        request.setAttribute("onlineUsers", onlineUsers);
        request.setAttribute("currentUsername", username);

        // 转发到 views 目录下的 JSP
        request.getRequestDispatcher("/views/chat.jsp").forward(request, response);
    }
}
package com.example.chatroom.controller;

import com.example.chatroom.model.ChatMessage;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.UserService;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet("/api/v1/message")
public class MessageApiController extends HttpServlet {

    private final Gson gson = new Gson();
    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();

    // GET: 获取增量消息和在线用户
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String username = (String) request.getSession().getAttribute("username");

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> errorMap = Map.of("error", "未登录或会话已过期，请重新登录。");
            response.getWriter().write(gson.toJson(errorMap));
            return;
        }

        long lastTimestamp = 0;
        try {
            String ts = request.getParameter("timestamp");
            if (ts != null) {
                lastTimestamp = Long.parseLong(ts);
            }
        } catch (NumberFormatException ignored) {}

        List<ChatMessage> newMessages = messageService.getMessagesForUser(username, lastTimestamp, getServletContext());
        Set<String> onlineUsers = userService.getOnlineUsers(getServletContext());

        Map<String, Object> responseData = Map.of(
                "messages", newMessages,
                "onlineUsers", onlineUsers
        );

        response.getWriter().write(gson.toJson(responseData));
    }

    // POST: 发送消息
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String sender = (String) request.getSession().getAttribute("username");

        if (sender == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, String> errorMap = Map.of("error", "未登录或会话已过期，请重新登录。");
            response.getWriter().write(gson.toJson(errorMap));
            return;
        }

        try (BufferedReader reader = request.getReader()) {
            Map<?, ?> data = gson.fromJson(reader, Map.class);
            String content = data.get("content") != null ? data.get("content").toString().trim() : "";
            String receiver = data.get("receiver") != null ? data.get("receiver").toString().trim() : null;

            if (content.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                Map<String, String> errorMap = Map.of("error", "消息内容不能为空");
                response.getWriter().write(gson.toJson(errorMap));
                return;
            }

            ChatMessage msg;
            if (receiver == null || receiver.isEmpty()) {
                msg = new ChatMessage(sender, content, ChatMessage.Type.PUBLIC);
            } else {
                msg = new ChatMessage(sender, receiver, content, ChatMessage.Type.PRIVATE);
            }

            messageService.addMessage(msg, getServletContext());

            response.setStatus(HttpServletResponse.SC_CREATED);
            Map<String, Object> successMap = Map.of("success", true);
            response.getWriter().write(gson.toJson(successMap));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> errorMap = Map.of("error", "发送消息失败: " + e.getMessage());
            response.getWriter().write(gson.toJson(errorMap));
        }
    }
}

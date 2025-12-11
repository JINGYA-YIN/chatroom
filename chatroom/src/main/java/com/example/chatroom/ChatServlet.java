package com.example.chatroom;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.*;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        if (context.getAttribute("messages") == null) {
            context.setAttribute("messages", new ArrayList<Map<String, String>>());
        }
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException {
        HttpSession session = request.getSession(true);

        if (session.isNew() || session.getAttribute("username") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        BufferedReader reader = request.getReader();
        Map<String, String> body = gson.fromJson(reader, Map.class);
        String text = body.get("message");

        if (text == null || text.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String username = (String) session.getAttribute("username");

        long lastAccess = session.getLastAccessedTime();

        String timestr = new Date(lastAccess).toString();

        ServletContext context = getServletContext();
        List<Map<String, String>> messages = (List<Map<String, String>>) context.getAttribute("messages");

        Map<String, String> msg = new HashMap<>();
        msg.put("user", username);
        msg.put("text", text);
        msg.put("timestamp", timestr);

        messages.add(msg);

        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(gson.toJson(msg));
        }

    @Override
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException {
        ServletContext context = getServletContext();
        List<Map<String, String>> messages = (List<Map<String, String>>) context.getAttribute("messages");

        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(gson.toJson(messages));
    }

}
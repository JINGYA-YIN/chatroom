package com.example.chatroom;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String,String> result = new HashMap<>();
        result.put("message", "登出成功");

        response.setContentType("application/json;charset=utf-8");

        try {
            response.getWriter().write(gson.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

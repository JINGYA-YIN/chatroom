package com.example.chatroom;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private Gson gson = new Gson();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        Map<String,String>body = gson.fromJson(reader,Map.class);
        String username = body.get("username");

        HttpSession session = request.getSession();
        session.setAttribute("username",username);

        Map<String,Object>result = new HashMap<>();
        result.put("username",username);
        result.put("createTime",new Date(session.getCreationTime()).toString());

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(result));
    }
}


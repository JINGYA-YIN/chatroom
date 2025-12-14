package com.example.chatroom.controller;

import com.example.chatroom.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet({"/login", "/logout"})
public class AuthApiController extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(request, response);
        } else if ("/logout".equals(path)) {
            handleLogout(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取表单参数
        String username = request.getParameter("username");
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "用户名不能为空");
            request.getRequestDispatcher("/views/index.jsp").forward(request, response);
            return;
        }

        username = username.trim();

        // 创建 session 并登录
        HttpSession session = request.getSession(true);
        session.setAttribute("username", username);
        userService.login(username, session, getServletContext());

        // 登录成功 → 跳转到聊天页面
        response.sendRedirect(request.getContextPath() + "/chat");
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 重定向回登录页
        response.sendRedirect(request.getContextPath() + "/views/index.jsp");
    }
}

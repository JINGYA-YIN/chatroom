package com.example.chatroom.filter;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebFilter(urlPatterns = {"/chat", "/chat.jsp", "/api/v1/*", "/logout"})
public class UserCheck implements Filter {

    private final Gson gson = new Gson();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();

        // 放行登录 / 登出
        if ("/login".equals(path) || "/logout".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("username") != null;

        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            if (path.endsWith(".jsp")) {
                resp.sendRedirect(req.getContextPath() + "/views/index.jsp");
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json;charset=UTF-8");
                resp.getWriter().write(gson.toJson(
                        Map.of("error", "未登录或会话已过期，请重新登录。")
                ));
            }
        }
    }

}

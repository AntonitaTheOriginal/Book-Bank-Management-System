package com.bbms.servlet;

import com.bbms.model.User;
import com.bbms.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // If already logged in, redirect to dashboard
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        req.getRequestDispatcher("/views/common/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId   = req.getParameter("userId");
        String password = req.getParameter("password");

        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            req.setAttribute("error", "Please enter your University ID and password.");
            req.getRequestDispatcher("/views/common/login.jsp").forward(req, resp);
            return;
        }

        try {
            User user = authService.authenticate(userId.trim(), password);
            if (user == null) {
                req.setAttribute("error", "Invalid credentials. Please try again.");
                req.getRequestDispatcher("/views/common/login.jsp").forward(req, resp);
                return;
            }

            // Create session
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userType", user.getUserType().name());
            session.setMaxInactiveInterval(1800); // 30 minutes

            resp.sendRedirect(req.getContextPath() + "/dashboard");

        } catch (SQLException e) {
            req.setAttribute("error", "System error. Please contact administrator.");
            req.getRequestDispatcher("/views/common/login.jsp").forward(req, resp);
        }
    }
}

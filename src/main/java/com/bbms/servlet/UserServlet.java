package com.bbms.servlet;

import com.bbms.dao.UserDAO;
import com.bbms.model.User;
import com.bbms.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private final UserDAO     userDAO     = new UserDAO();
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }
        User admin = (User) session.getAttribute("user");
        requireAdmin(admin, resp); if (resp.isCommitted()) return;

        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "add":
                    req.getRequestDispatcher("/views/admin/addUser.jsp").forward(req, resp);
                    break;

                case "edit":
                    String uid = req.getParameter("id");
                    req.setAttribute("editUser", userDAO.getUserById(uid));
                    req.getRequestDispatcher("/views/admin/editUser.jsp").forward(req, resp);
                    break;

                case "delete":
                    userDAO.deleteUser(req.getParameter("id"));
                    session.setAttribute("flash", "User removed successfully.");
                    resp.sendRedirect(req.getContextPath() + "/users");
                    break;

                default:
                    req.setAttribute("users", userDAO.getAllUsers());
                    req.getRequestDispatcher("/views/admin/userList.jsp").forward(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("User operation failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }
        User admin = (User) session.getAttribute("user");
        requireAdmin(admin, resp); if (resp.isCommitted()) return;

        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                User newUser = buildUser(req);

                // Check for duplicate ID
                if (userDAO.getUserById(newUser.getUserId()) != null) {
                    req.setAttribute("error", "A user with this ID already exists.");
                    req.setAttribute("formUser", newUser);
                    req.getRequestDispatcher("/views/admin/addUser.jsp").forward(req, resp);
                    return;
                }

                String rawPassword = req.getParameter("password");
                newUser.setPasswordHash(authService.hashPassword(rawPassword));
                userDAO.addUser(newUser);
                session.setAttribute("flash", "User registered successfully.");
                resp.sendRedirect(req.getContextPath() + "/users");

            } else if ("edit".equals(action)) {
                User u = buildUser(req);
                userDAO.updateUser(u);
                session.setAttribute("flash", "User updated successfully.");
                resp.sendRedirect(req.getContextPath() + "/users");
            }

        } catch (SQLException e) {
            throw new ServletException("User save failed", e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private User buildUser(HttpServletRequest req) {
        User u = new User();
        u.setUserId(req.getParameter("userId"));
        u.setFirstName(req.getParameter("firstName"));
        u.setLastName(req.getParameter("lastName"));
        u.setEmail(req.getParameter("email"));
        u.setPhone(req.getParameter("phone"));
        u.setDepartment(req.getParameter("department"));
        
        String typeStr = req.getParameter("userType");
        String statStr = req.getParameter("status");
        
        try {
            if (typeStr != null) u.setUserType(User.UserType.valueOf(typeStr.toUpperCase()));
            if (statStr != null) u.setStatus(User.UserStatus.valueOf(statStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Log error or set defaults
            System.err.println("Invalid enum value: " + typeStr + " / " + statStr);
        }
        
        u.setRegistrationDate(LocalDate.now());
        return u;
    }

    private void requireAdmin(User u, HttpServletResponse resp) throws IOException {
        if (u == null || u.getUserType() != User.UserType.ADMIN)
            resp.sendRedirect("dashboard");
    }
}

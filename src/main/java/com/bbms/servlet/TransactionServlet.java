package com.bbms.servlet;

import com.bbms.dao.TransactionDAO;
import com.bbms.model.User;
import com.bbms.service.BookService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {

    private final BookService    bookService    = new BookService();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }

        User   user   = (User) session.getAttribute("user");
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "issue":
                    requireAdmin(user, resp); if (resp.isCommitted()) return;
                    req.setAttribute("books", bookService.getAllBooks());
                    req.getRequestDispatcher("/views/admin/issueBook.jsp").forward(req, resp);
                    break;

                case "return":
                    requireAdmin(user, resp); if (resp.isCommitted()) return;
                    req.setAttribute("activeTransactions", transactionDAO.getAllActiveTransactions());
                    req.getRequestDispatcher("/views/admin/returnBook.jsp").forward(req, resp);
                    break;

                case "overdue":
                    requireAdmin(user, resp); if (resp.isCommitted()) return;
                    req.setAttribute("overdueList", transactionDAO.getOverdueTransactions());
                    req.getRequestDispatcher("/views/admin/overdueList.jsp").forward(req, resp);
                    break;

                default:
                    if (user.getUserType() == User.UserType.ADMIN) {
                        req.setAttribute("transactions", transactionDAO.getAllTransactions());
                    } else {
                        req.setAttribute("transactions", transactionDAO.getTransactionsByUser(user.getUserId()));
                    }
                    req.getRequestDispatcher("/views/common/transactions.jsp").forward(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Transaction error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }

        User   user   = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        requireAdmin(user, resp); if (resp.isCommitted()) return;

        try {
            if ("issue".equals(action)) {
                String userId = req.getParameter("userId");
                String bIdStr = req.getParameter("bookId");
                
                if (userId == null || bIdStr == null || bIdStr.isBlank()) {
                    req.setAttribute("error", "User ID and Book ID are required.");
                    req.setAttribute("books", bookService.getAllBooks());
                    req.getRequestDispatcher("/views/admin/issueBook.jsp").forward(req, resp);
                    return;
                }

                try {
                    int bookId = Integer.parseInt(bIdStr);
                    String result = bookService.issueBook(userId, bookId);
                    
                    if (result.startsWith("SUCCESS:")) {
                        session.setAttribute("flash", result.split(":", 2)[1].trim());
                        resp.sendRedirect(req.getContextPath() + "/transactions");
                    } else {
                        req.setAttribute("error", result.split(":", 2)[1].trim());
                        req.setAttribute("books", bookService.getAllBooks());
                        req.getRequestDispatcher("/views/admin/issueBook.jsp").forward(req, resp);
                    }
                } catch (NumberFormatException e) {
                    req.setAttribute("error", "Invalid Book ID format.");
                    req.setAttribute("books", bookService.getAllBooks());
                    req.getRequestDispatcher("/views/admin/issueBook.jsp").forward(req, resp);
                }

            } else if ("return".equals(action)) {
                String userId = req.getParameter("userId");
                String bIdStr = req.getParameter("bookId");
                
                if (userId == null || bIdStr == null || bIdStr.isBlank()) {
                    session.setAttribute("flash", "Error: User ID and Book ID are required.");
                    resp.sendRedirect(req.getContextPath() + "/transactions?action=return");
                    return;
                }

                try {
                    int bookId = Integer.parseInt(bIdStr);
                    String result = bookService.returnBook(userId, bookId);
                    if (result.startsWith("SUCCESS:")) {
                        session.setAttribute("flash", result.split(":", 2)[1].trim());
                    } else {
                        session.setAttribute("flash", "Error: " + result.split(":", 2)[1].trim());
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("flash", "Error: Invalid Book ID format.");
                }
                resp.sendRedirect(req.getContextPath() + "/transactions?action=return");
            }

        } catch (SQLException e) {
            throw new ServletException("Transaction processing failed", e);
        }
    }

    private void requireAdmin(User u, HttpServletResponse resp) throws IOException {
        if (u == null || u.getUserType() != User.UserType.ADMIN)
            resp.sendRedirect("dashboard");
    }
}

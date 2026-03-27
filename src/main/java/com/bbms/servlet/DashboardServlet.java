package com.bbms.servlet;

import com.bbms.dao.*;
import com.bbms.model.User;
import com.bbms.service.BookService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final BookService    bookService    = new BookService();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final FineDAO        fineDAO        = new FineDAO();
    private final UserDAO        userDAO        = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            if (user.getUserType() == User.UserType.ADMIN) {
                req.setAttribute("totalBooks",        bookService.countTotalBooks());
                req.setAttribute("totalUsers",        userDAO.countActiveUsers());
                req.setAttribute("totalTransactions", transactionDAO.countTotalTransactions());
                req.setAttribute("overdueList",       transactionDAO.getOverdueTransactions());
                req.setAttribute("pendingFines",      fineDAO.getAllPendingFines());
                req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);

            } else {
                // Student / Faculty
                req.setAttribute("myTransactions", transactionDAO.getTransactionsByUser(user.getUserId()));
                req.setAttribute("myFines",        fineDAO.getFinesByUser(user.getUserId()));
                req.getRequestDispatcher("/views/student/dashboard.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Dashboard error", e);
        }
    }
}

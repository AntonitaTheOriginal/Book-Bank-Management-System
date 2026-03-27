package com.bbms.servlet;

import com.bbms.dao.*;
import com.bbms.model.User;
import com.bbms.service.BookService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final BookService    bookService    = new BookService();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final FineDAO        fineDAO        = new FineDAO();
    private final UserDAO        userDAO        = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }

        User user = (User) session.getAttribute("user");
        if (user.getUserType() != User.UserType.ADMIN) {
            resp.sendRedirect(req.getContextPath() + "/dashboard"); return;
        }

        String type = req.getParameter("type");
        if (type == null) type = "summary";

        try {
            switch (type) {
                case "transactions":
                    req.setAttribute("transactions", transactionDAO.getAllTransactions());
                    req.setAttribute("reportTitle", "All Transactions Report");
                    break;

                case "overdue":
                    req.setAttribute("transactions", transactionDAO.getOverdueTransactions());
                    req.setAttribute("reportTitle", "Overdue Books Report");
                    break;

                case "fines":
                    req.setAttribute("fines",       fineDAO.getAllFines());
                    req.setAttribute("reportTitle", "Fines Report");
                    break;

                case "inventory":
                    req.setAttribute("books",       bookService.getAllBooks());
                    req.setAttribute("reportTitle", "Inventory Status Report");
                    break;

                default: // summary
                    req.setAttribute("totalBooks",        bookService.countTotalBooks());
                    req.setAttribute("totalUsers",        userDAO.countActiveUsers());
                    req.setAttribute("totalTransactions", transactionDAO.countTotalTransactions());
                    req.setAttribute("overdueCount",      transactionDAO.getOverdueTransactions().size());
                    req.setAttribute("pendingFineCount",  fineDAO.getAllPendingFines().size());
                    req.setAttribute("reportTitle",       "Summary Report");
                    break;
            }

            req.setAttribute("reportType", type);
            req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException("Report generation failed", e);
        }
    }
}

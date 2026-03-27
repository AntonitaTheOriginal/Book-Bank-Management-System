package com.bbms.servlet;

import com.bbms.dao.FineDAO;
import com.bbms.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/fines")
public class FineServlet extends HttpServlet {

    private final FineDAO fineDAO = new FineDAO();

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
            if (user.getUserType() == User.UserType.ADMIN) {
                try {
                    switch (action) {
                        case "pay":
                            fineDAO.markAsPaid(Integer.parseInt(req.getParameter("id")));
                            session.setAttribute("flash", "Fine marked as paid.");
                            resp.sendRedirect(req.getContextPath() + "/fines");
                            return;
                        case "waive":
                            fineDAO.markAsWaived(Integer.parseInt(req.getParameter("id")));
                            session.setAttribute("flash", "Fine waived.");
                            resp.sendRedirect(req.getContextPath() + "/fines");
                            return;
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("flash", "Error: Invalid Fine ID.");
                    resp.sendRedirect(req.getContextPath() + "/fines");
                    return;
                }
                
                // If not pay/waive, or if switch fell through
                req.setAttribute("fines", fineDAO.getAllFines());
                req.getRequestDispatcher("/views/admin/fineList.jsp").forward(req, resp);
                return;
            } else {
                // Student / Faculty see only their own fines
                req.setAttribute("fines",        fineDAO.getFinesByUser(user.getUserId()));
                req.setAttribute("totalPending",  fineDAO.getTotalPendingFineByUser(user.getUserId()));
                req.getRequestDispatcher("/views/student/myFines.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            throw new ServletException("Fine operation failed", e);
        }
    }
}

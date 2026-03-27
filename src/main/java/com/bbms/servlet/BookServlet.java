package com.bbms.servlet;

import com.bbms.model.Book;
import com.bbms.model.User;
import com.bbms.service.BookService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private final BookService bookService = new BookService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "add":
                    requireAdmin(session, resp); if (resp.isCommitted()) return;
                    req.setAttribute("departments", bookService.getAllDepartments());
                    req.getRequestDispatcher("/views/admin/addBook.jsp").forward(req, resp);
                    break;

                case "edit":
                    requireAdmin(session, resp); if (resp.isCommitted()) return;
                    int editId = Integer.parseInt(req.getParameter("id"));
                    req.setAttribute("book",        bookService.getBookById(editId));
                    req.setAttribute("departments", bookService.getAllDepartments());
                    req.getRequestDispatcher("/views/admin/editBook.jsp").forward(req, resp);
                    break;

                case "delete":
                    requireAdmin(session, resp); if (resp.isCommitted()) return;
                    int delId = Integer.parseInt(req.getParameter("id"));
                    boolean deleted = bookService.deleteBook(delId);
                    req.getSession().setAttribute("flash",
                            deleted ? "Book deleted successfully."
                                    : "Cannot delete: book has active transactions.");
                    resp.sendRedirect(req.getContextPath() + "/books");
                    break;

                case "search":
                default:
                    String  keyword  = req.getParameter("keyword");
                    String  dept     = req.getParameter("department");
                    String  yearStr  = req.getParameter("year");
                    String  availStr = req.getParameter("available");
                    Integer year     = (yearStr  != null && !yearStr.isBlank())  ? Integer.parseInt(yearStr)  : null;
                    Boolean avail    = (availStr != null && !availStr.isBlank()) ? Boolean.parseBoolean(availStr) : null;

                    List<Book> books = (keyword != null || dept != null || year != null)
                            ? bookService.searchBooks(keyword, dept, year, avail)
                            : bookService.getAllBooks();

                    req.setAttribute("books",       books);
                    req.setAttribute("departments", bookService.getAllDepartments());
                    req.getRequestDispatcher("/views/common/books.jsp").forward(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Book operation failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); return;
        }
        requireAdmin(session, resp); if (resp.isCommitted()) return;

        String action = req.getParameter("action");

        try {
            Book book = extractBook(req);

            if ("add".equals(action)) {
                if (!book.validateISBN()) {
                    req.setAttribute("error", "Invalid ISBN-13 format.");
                    req.setAttribute("book",  book);
                    req.setAttribute("departments", bookService.getAllDepartments());
                    req.getRequestDispatcher("/views/admin/addBook.jsp").forward(req, resp);
                    return;
                }
                if (bookService.getBookByIsbn(book.getIsbn()) != null) {
                    req.setAttribute("error", "A book with this ISBN already exists.");
                    req.setAttribute("book",  book);
                    req.setAttribute("departments", bookService.getAllDepartments());
                    req.getRequestDispatcher("/views/admin/addBook.jsp").forward(req, resp);
                    return;
                }
                bookService.addBook(book);
                session.setAttribute("flash", "Book added successfully.");

            } else if ("edit".equals(action)) {
                book.setBookId(Integer.parseInt(req.getParameter("bookId")));
                bookService.updateBook(book);
                session.setAttribute("flash", "Book updated successfully.");
            }

            resp.sendRedirect(req.getContextPath() + "/books");

        } catch (SQLException e) {
            throw new ServletException("Book save failed", e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Book extractBook(HttpServletRequest req) {
        Book b = new Book();
        b.setIsbn(req.getParameter("isbn"));
        b.setTitle(req.getParameter("title"));
        b.setAuthor(req.getParameter("author"));
        b.setPublisher(req.getParameter("publisher"));
        b.setEdition(req.getParameter("edition"));
        b.setDepartment(req.getParameter("department"));
        String yr = req.getParameter("publicationYear");
        try {
            if (yr != null && !yr.isBlank()) b.setPublicationYear(Integer.parseInt(yr));
        } catch (NumberFormatException e) {
            System.err.println("Invalid publication year: " + yr);
        }

        String tc = req.getParameter("totalCopies");
        try {
            if (tc != null && !tc.isBlank()) {
                int copies = Integer.parseInt(tc);
                b.setTotalCopies(copies);
                b.setAvailableCopies(copies);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid total copies: " + tc);
        }
        return b;
    }

    private void requireAdmin(HttpSession session, HttpServletResponse resp) throws IOException {
        User u = (User) session.getAttribute("user");
        if (u == null || u.getUserType() != User.UserType.ADMIN)
            resp.sendRedirect("dashboard");
    }
}

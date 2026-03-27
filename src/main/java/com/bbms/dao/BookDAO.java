package com.bbms.dao;

import com.bbms.model.Book;
import com.bbms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book entities.
 */
public class BookDAO {

    /** Retrieves a book by its primary key. */
    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Retrieves a book by ISBN. */
    public Book getBookByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns all books, ordered by title. */
    public List<Book> getAllBooks() throws SQLException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /**
     * Multi-criteria search across title, author, ISBN, and optionally department.
     */
    public List<Book> searchBooks(String keyword, String department,
                                  Integer year, Boolean availableOnly) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM books WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (title LIKE ? OR author LIKE ? OR isbn LIKE ?)");
            String kw = "%" + keyword + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        if (department != null && !department.isBlank()) {
            sql.append(" AND department = ?");
            params.add(department);
        }
        if (year != null) {
            sql.append(" AND publication_year = ?");
            params.add(year);
        }
        if (Boolean.TRUE.equals(availableOnly)) {
            sql.append(" AND available_copies > 0");
        }
        sql.append(" ORDER BY title");

        List<Book> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Inserts a new book record. */
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, publisher, publication_year, " +
                     "edition, total_copies, available_copies, department) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            ps.setInt(5,    book.getPublicationYear());
            ps.setString(6, book.getEdition());
            ps.setInt(7,    book.getTotalCopies());
            ps.setInt(8,    book.getAvailableCopies());
            ps.setString(9, book.getDepartment());
            return ps.executeUpdate() > 0;
        }
    }

    /** Updates an existing book record. */
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn=?, title=?, author=?, publisher=?, publication_year=?, " +
                     "edition=?, total_copies=?, available_copies=?, department=? WHERE book_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getPublisher());
            ps.setInt(5,    book.getPublicationYear());
            ps.setString(6, book.getEdition());
            ps.setInt(7,    book.getTotalCopies());
            ps.setInt(8,    book.getAvailableCopies());
            ps.setString(9, book.getDepartment());
            ps.setInt(10,   book.getBookId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Decrements available copies by 1. */
    public boolean decrementAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies - 1 " +
                     "WHERE book_id = ? AND available_copies > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Increments available copies by 1. */
    public boolean incrementAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + 1 " +
                     "WHERE book_id = ? AND available_copies < total_copies";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Deletes a book only if it has no active transactions. */
    public boolean deleteBook(int bookId) throws SQLException {
        // Check for active transactions first
        String check = "SELECT COUNT(*) FROM transactions WHERE book_id=? AND status='ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return false; // blocked
            }
        }
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Returns total number of book titles. */
    public int countTotalBooks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Returns distinct department values. */
    public List<String> getAllDepartments() throws SQLException {
        List<String> depts = new ArrayList<>();
        String sql = "SELECT DISTINCT department FROM books ORDER BY department";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) depts.add(rs.getString(1));
        }
        return depts;
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private Book mapRow(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setPublisher(rs.getString("publisher"));
        b.setPublicationYear(rs.getInt("publication_year"));
        b.setEdition(rs.getString("edition"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setDepartment(rs.getString("department"));
        return b;
    }
}

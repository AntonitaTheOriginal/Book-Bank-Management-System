package com.bbms.dao;

import com.bbms.model.Transaction;
import com.bbms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Transaction entities.
 */
public class TransactionDAO {

    /** Inserts a new transaction and returns the generated ID. */
    public int addTransaction(Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, book_id, issue_date, due_date, status) " +
                     "VALUES (?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getUserId());
            ps.setInt(2,    t.getBookId());
            ps.setDate(3,   Date.valueOf(t.getIssueDate()));
            ps.setDate(4,   Date.valueOf(t.getDueDate()));
            ps.setString(5, t.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Retrieves a transaction by its primary key. */
    public Transaction getTransactionById(int id) throws SQLException {
        String sql = "SELECT t.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, b.isbn AS book_isbn " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.transaction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns the active (ISSUED) transaction for a user/book pair. */
    public Transaction getActiveTransaction(String userId, int bookId) throws SQLException {
        String sql = "SELECT t.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, b.isbn AS book_isbn " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.user_id=? AND t.book_id=? AND t.status='ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns all transactions for a specific user. */
    public List<Transaction> getTransactionsByUser(String userId) throws SQLException {
        String sql = "SELECT t.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, b.isbn AS book_isbn " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.user_id = ? ORDER BY t.issue_date DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Returns all currently issued (active) transactions. */
    public List<Transaction> getAllActiveTransactions() throws SQLException {
        String sql = "SELECT t.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, b.isbn AS book_isbn " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.status = 'ISSUED' ORDER BY t.due_date";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Returns all overdue transactions. */
    public List<Transaction> getOverdueTransactions() throws SQLException {
        String sql = "SELECT t.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, b.isbn AS book_isbn " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.status = 'ISSUED' AND t.due_date < CURDATE() " +
                     "ORDER BY t.due_date";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Returns all transactions (for reports). */
    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = "SELECT t.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, b.isbn AS book_isbn " +
                     "FROM transactions t " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "ORDER BY t.issue_date DESC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Marks a transaction as RETURNED and sets the return date. */
    public boolean returnBook(int transactionId, java.time.LocalDate returnDate) throws SQLException {
        String sql = "UPDATE transactions SET status='RETURNED', return_date=? WHERE transaction_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, transactionId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Counts how many books a user currently has issued. */
    public int countActiveIssuesByUser(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id=? AND status='ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Returns total transactions processed. */
    public int countTotalTransactions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setUserId(rs.getString("user_id"));
        t.setBookId(rs.getInt("book_id"));
        Date id = rs.getDate("issue_date");
        if (id != null) t.setIssueDate(id.toLocalDate());
        Date dd = rs.getDate("due_date");
        if (dd != null) t.setDueDate(dd.toLocalDate());
        Date rd = rs.getDate("return_date");
        if (rd != null) t.setReturnDate(rd.toLocalDate());
        t.setStatus(Transaction.TransactionStatus.valueOf(rs.getString("status")));
        t.setUserName(rs.getString("user_name"));
        t.setBookTitle(rs.getString("book_title"));
        t.setBookIsbn(rs.getString("book_isbn"));
        return t;
    }
}

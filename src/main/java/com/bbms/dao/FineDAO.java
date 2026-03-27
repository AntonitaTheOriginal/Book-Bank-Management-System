package com.bbms.dao;

import com.bbms.model.Fine;
import com.bbms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Fine entities.
 */
public class FineDAO {

    /** Inserts a new fine and returns the generated ID. */
    public int addFine(Fine fine) throws SQLException {
        String sql = "INSERT INTO fines (transaction_id, amount, issued_date, status) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,    fine.getTransactionId());
            ps.setDouble(2, fine.getAmount());
            ps.setDate(3,   Date.valueOf(fine.getIssuedDate()));
            ps.setString(4, fine.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    /** Retrieves a fine by its primary key. */
    public Fine getFineById(int fineId) throws SQLException {
        String sql = "SELECT f.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, t.user_id " +
                     "FROM fines f " +
                     "JOIN transactions t ON f.transaction_id = t.transaction_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE f.fine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    /** Returns all fines for a specific user. */
    public List<Fine> getFinesByUser(String userId) throws SQLException {
        String sql = "SELECT f.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, t.user_id " +
                     "FROM fines f " +
                     "JOIN transactions t ON f.transaction_id = t.transaction_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE t.user_id = ? ORDER BY f.issued_date DESC";
        List<Fine> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Returns all pending fines. */
    public List<Fine> getAllPendingFines() throws SQLException {
        String sql = "SELECT f.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, t.user_id " +
                     "FROM fines f " +
                     "JOIN transactions t ON f.transaction_id = t.transaction_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE f.status = 'PENDING' ORDER BY f.issued_date";
        List<Fine> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Returns all fines (for reports). */
    public List<Fine> getAllFines() throws SQLException {
        String sql = "SELECT f.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, t.user_id " +
                     "FROM fines f " +
                     "JOIN transactions t ON f.transaction_id = t.transaction_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "ORDER BY f.issued_date DESC";
        List<Fine> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Returns total pending fine amount for a user. */
    public double getTotalPendingFineByUser(String userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(f.amount),0) FROM fines f " +
                     "JOIN transactions t ON f.transaction_id = t.transaction_id " +
                     "WHERE t.user_id=? AND f.status='PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    /** Marks a fine as PAID. */
    public boolean markAsPaid(int fineId) throws SQLException {
        String sql = "UPDATE fines SET status='PAID', paid_date=CURDATE() WHERE fine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Marks a fine as WAIVED. */
    public boolean markAsWaived(int fineId) throws SQLException {
        String sql = "UPDATE fines SET status='WAIVED' WHERE fine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Returns fine linked to a specific transaction. */
    public Fine getFineByTransaction(int transactionId) throws SQLException {
        String sql = "SELECT f.*, CONCAT(u.first_name,' ',u.last_name) AS user_name, " +
                     "b.title AS book_title, t.user_id " +
                     "FROM fines f " +
                     "JOIN transactions t ON f.transaction_id = t.transaction_id " +
                     "JOIN users u ON t.user_id = u.user_id " +
                     "JOIN books b ON t.book_id = b.book_id " +
                     "WHERE f.transaction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, transactionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ── Private helpers ─────────────────────────────────────────────────────

    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine f = new Fine();
        f.setFineId(rs.getInt("fine_id"));
        f.setTransactionId(rs.getInt("transaction_id"));
        f.setAmount(rs.getDouble("amount"));
        Date id = rs.getDate("issued_date");
        if (id != null) f.setIssuedDate(id.toLocalDate());
        Date pd = rs.getDate("paid_date");
        if (pd != null) f.setPaidDate(pd.toLocalDate());
        f.setStatus(Fine.FineStatus.valueOf(rs.getString("status")));
        f.setUserName(rs.getString("user_name"));
        f.setBookTitle(rs.getString("book_title"));
        f.setUserId(rs.getString("user_id"));
        return f;
    }
}

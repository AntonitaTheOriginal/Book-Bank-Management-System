package com.bbms.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Records a complete book borrowing cycle.
 */
public class Transaction {

    public enum TransactionStatus { ISSUED, RETURNED, OVERDUE }

    private int               transactionId;
    private String            userId;
    private int               bookId;
    private LocalDate         issueDate;
    private LocalDate         dueDate;
    private LocalDate         returnDate;
    private TransactionStatus status;

    // Joined fields for display
    private String userName;
    private String bookTitle;
    private String bookIsbn;

    public Transaction() {}

    public Transaction(String userId, int bookId, LocalDate issueDate,
                       LocalDate dueDate, TransactionStatus status) {
        this.userId    = userId;
        this.bookId    = bookId;
        this.issueDate = issueDate;
        this.dueDate   = dueDate;
        this.status    = status;
    }

    /**
     * Calculates the due date based on issue date and loan days.
     */
    public LocalDate calculateDueDate(int loanDays) {
        return issueDate.plusDays(loanDays);
    }

    /**
     * Returns true if the transaction is currently overdue.
     */
    public boolean isOverdue() {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /**
     * Returns the number of overdue days (0 if not overdue).
     */
    public long getOverdueDays() {
        LocalDate checkDate = (returnDate != null) ? returnDate : LocalDate.now();
        long days = ChronoUnit.DAYS.between(dueDate, checkDate);
        return Math.max(0, days);
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int               getTransactionId()             { return transactionId; }
    public void              setTransactionId(int v)        { this.transactionId = v; }

    public String            getUserId()                    { return userId; }
    public void              setUserId(String v)            { this.userId = v; }

    public int               getBookId()                    { return bookId; }
    public void              setBookId(int v)               { this.bookId = v; }

    public LocalDate         getIssueDate()                 { return issueDate; }
    public void              setIssueDate(LocalDate v)      { this.issueDate = v; }

    public LocalDate         getDueDate()                   { return dueDate; }
    public void              setDueDate(LocalDate v)        { this.dueDate = v; }

    public LocalDate         getReturnDate()                { return returnDate; }
    public void              setReturnDate(LocalDate v)     { this.returnDate = v; }

    public TransactionStatus getStatus()                    { return status; }
    public void              setStatus(TransactionStatus v) { this.status = v; }

    public String            getUserName()                  { return userName; }
    public void              setUserName(String v)          { this.userName = v; }

    public String            getBookTitle()                 { return bookTitle; }
    public void              setBookTitle(String v)         { this.bookTitle = v; }

    public String            getBookIsbn()                  { return bookIsbn; }
    public void              setBookIsbn(String v)          { this.bookIsbn = v; }
}

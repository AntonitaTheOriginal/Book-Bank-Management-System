package com.bbms.model;

import java.time.LocalDate;
import com.bbms.util.AppConfig;

/**
 * Tracks overdue fine amounts and payment status for a transaction.
 */
public class Fine {

    public enum FineStatus { PENDING, PAID, WAIVED }

    private int       fineId;
    private int       transactionId;
    private double    amount;
    private LocalDate issuedDate;
    private LocalDate paidDate;
    private FineStatus status;

    // Joined fields for display
    private String userName;
    private String bookTitle;
    private String userId;

    public Fine() {}

    public Fine(int transactionId, double amount, LocalDate issuedDate, FineStatus status) {
        this.transactionId = transactionId;
        this.amount        = amount;
        this.issuedDate    = issuedDate;
        this.status        = status;
    }

    /**
     * Calculates fine amount based on overdue days.
     * Rule: ₹1/day, max ₹50, with 2-day grace period.
     */
    public static double calculateFineAmount(long overdueDays) {
        int graceDays = AppConfig.getGraceDays();
        if (overdueDays <= graceDays) return 0.0;
        long chargeableDays = overdueDays - graceDays;
        double fine = chargeableDays * AppConfig.getFineRatePerDay();
        return Math.min(fine, AppConfig.getMaxFine());
    }

    public void markAsPaid() {
        this.status   = FineStatus.PAID;
        this.paidDate = LocalDate.now();
    }

    public void markAsWaived() {
        this.status = FineStatus.WAIVED;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int        getFineId()                  { return fineId; }
    public void       setFineId(int v)             { this.fineId = v; }

    public int        getTransactionId()           { return transactionId; }
    public void       setTransactionId(int v)      { this.transactionId = v; }

    public double     getAmount()                  { return amount; }
    public void       setAmount(double v)          { this.amount = v; }

    public LocalDate  getIssuedDate()              { return issuedDate; }
    public void       setIssuedDate(LocalDate v)   { this.issuedDate = v; }

    public LocalDate  getPaidDate()                { return paidDate; }
    public void       setPaidDate(LocalDate v)     { this.paidDate = v; }

    public FineStatus getStatus()                  { return status; }
    public void       setStatus(FineStatus v)      { this.status = v; }

    public String     getUserName()                { return userName; }
    public void       setUserName(String v)        { this.userName = v; }

    public String     getBookTitle()               { return bookTitle; }
    public void       setBookTitle(String v)       { this.bookTitle = v; }

    public String     getUserId()                  { return userId; }
    public void       setUserId(String v)          { this.userId = v; }
}

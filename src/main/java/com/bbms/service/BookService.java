package com.bbms.service;

import com.bbms.dao.BookDAO;
import com.bbms.dao.FineDAO;
import com.bbms.dao.TransactionDAO;
import com.bbms.dao.UserDAO;
import com.bbms.model.*;
import com.bbms.util.AppConfig;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for book issuance, returns, and inventory management.
 */
public class BookService {

    private final BookDAO        bookDAO        = new BookDAO();
    private final UserDAO        userDAO        = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final FineDAO        fineDAO        = new FineDAO();
    private final EmailService   emailService   = new EmailService();

    // ── Issue Book ───────────────────────────────────────────────────────────

    /**
     * Issues a book to an eligible user.
     * @return result message; starts with "SUCCESS:" or "ERROR:"
     */
    public String issueBook(String userId, int bookId) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user == null) return "ERROR: User not found. Please register the user first.";
        if (user.getStatus() != User.UserStatus.ACTIVE)
            return "ERROR: User account is " + user.getStatus() + ".";

        // Fine eligibility check
        double pendingFine = fineDAO.getTotalPendingFineByUser(userId);
        if (pendingFine > AppConfig.getMaxFineThreshold())
            return String.format("ERROR: User has unpaid fines of ₹%.2f exceeding the ₹%.2f limit.",
                    pendingFine, AppConfig.getMaxFineThreshold());

        // Borrowing limit check
        int activeIssues = transactionDAO.countActiveIssuesByUser(userId);
        if (activeIssues >= user.getBorrowingLimit())
            return "ERROR: User has reached the maximum borrowing limit of "
                    + user.getBorrowingLimit() + " books.";

        // Duplicate-borrow check: same user cannot borrow the same book twice
        Transaction existing = transactionDAO.getActiveTransaction(userId, bookId);
        if (existing != null)
            return "ERROR: This user already has an active loan for this book (Transaction ID: "
                   + existing.getTransactionId() + ").";

        Book book = bookDAO.getBookById(bookId);
        if (book == null) return "ERROR: Book not found.";
        if (!book.isAvailable()) return "ERROR: No copies available for this book.";

        // Create transaction
        LocalDate today   = LocalDate.now();
        LocalDate dueDate = today.plusDays(user.getLoanDays());
        Transaction txn   = new Transaction(userId, bookId, today, dueDate,
                                             Transaction.TransactionStatus.ISSUED);
        int txnId = transactionDAO.addTransaction(txn);
        if (txnId < 0) return "ERROR: Failed to create transaction. Please try again.";

        bookDAO.decrementAvailableCopies(bookId);

        // Send confirmation email (non-blocking; failure doesn't roll back)
        try {
            emailService.sendIssueConfirmation(user.getEmail(), user.getFullName(),
                    book.getTitle(), today, dueDate);
        } catch (Exception ignored) {}

        return "SUCCESS: Book issued successfully. Transaction ID: " + txnId +
               ". Due date: " + dueDate;
    }

    // ── Return Book ──────────────────────────────────────────────────────────

    /**
     * Processes a book return and calculates any fine.
     * @return result message; starts with "SUCCESS:" or "ERROR:"
     */
    public String returnBook(String userId, int bookId) throws SQLException {
        Transaction txn = transactionDAO.getActiveTransaction(userId, bookId);
        if (txn == null) return "ERROR: No active transaction found for this user and book.";

        LocalDate today       = LocalDate.now();
        long      overdueDays = txn.getOverdueDays();
        double    fineAmount  = Fine.calculateFineAmount(overdueDays);

        transactionDAO.returnBook(txn.getTransactionId(), today);
        bookDAO.incrementAvailableCopies(bookId);

        String msg = "SUCCESS: Book returned successfully.";

        if (fineAmount > 0) {
            Fine fine = new Fine(txn.getTransactionId(), fineAmount, today, Fine.FineStatus.PENDING);
            int fineId = fineDAO.addFine(fine);
            msg += String.format(" Fine assessed: ₹%.2f (Fine ID: %d).", fineAmount, fineId);

            // Send fine notification email
            User user = userDAO.getUserById(userId);
            Book book = bookDAO.getBookById(bookId);
            try {
                if (user != null && book != null)
                    emailService.sendFineNotice(user.getEmail(), user.getFullName(),
                            book.getTitle(), fineAmount);
            } catch (Exception ignored) {}
        }

        return msg;
    }

    // ── Inventory helpers ────────────────────────────────────────────────────

    public List<Book>   getAllBooks()                                       throws SQLException { return bookDAO.getAllBooks(); }
    public Book         getBookById(int id)                                 throws SQLException { return bookDAO.getBookById(id); }
    public Book         getBookByIsbn(String isbn)                          throws SQLException { return bookDAO.getBookByIsbn(isbn); }
    public List<Book>   searchBooks(String kw, String dept, Integer yr, Boolean avail) throws SQLException { return bookDAO.searchBooks(kw, dept, yr, avail); }
    public boolean      addBook(Book b)                                     throws SQLException { return bookDAO.addBook(b); }
    public boolean      updateBook(Book b)                                  throws SQLException { return bookDAO.updateBook(b); }
    public boolean      deleteBook(int id)                                  throws SQLException { return bookDAO.deleteBook(id); }
    public List<String> getAllDepartments()                                  throws SQLException { return bookDAO.getAllDepartments(); }
    public int          countTotalBooks()                                    throws SQLException { return bookDAO.countTotalBooks(); }
}

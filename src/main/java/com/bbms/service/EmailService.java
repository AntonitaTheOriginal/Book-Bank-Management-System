package com.bbms.service;

import com.bbms.util.AppConfig;

import javax.mail.*;
import javax.mail.internet.*;
import java.time.LocalDate;
import java.util.Properties;

/**
 * Sends automated email notifications via SMTP (JavaMail).
 * Configure SMTP credentials in database.properties.
 */
public class EmailService {

    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host",            AppConfig.getMailHost());
        props.put("mail.smtp.port",            AppConfig.getMailPort());
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        AppConfig.getMailUser(), AppConfig.getMailPassword());
            }
        });
    }

    /** Sends a book issue confirmation email. */
    public void sendIssueConfirmation(String toEmail, String userName,
                                      String bookTitle, LocalDate issueDate,
                                      LocalDate dueDate) throws MessagingException {
        String subject = "Book Issued Successfully – " + bookTitle;
        String body = "<h3>Book Issue Confirmation</h3>"
                + "<p>Dear " + userName + ",</p>"
                + "<p>The following book has been issued to you:</p>"
                + "<table border='1' cellpadding='6'>"
                + "<tr><td><b>Book Title</b></td><td>" + bookTitle + "</td></tr>"
                + "<tr><td><b>Issue Date</b></td><td>" + issueDate + "</td></tr>"
                + "<tr><td><b>Due Date</b></td><td>" + dueDate + "</td></tr>"
                + "</table>"
                + "<p>Please return the book on or before the due date to avoid fines.</p>"
                + "<p>Regards,<br>Book Bank Management System</p>";
        sendEmail(toEmail, subject, body);
    }

    /** Sends an overdue fine notification email. */
    public void sendFineNotice(String toEmail, String userName,
                               String bookTitle, double fineAmount) throws MessagingException {
        String subject = "Overdue Fine Notice – Book Bank";
        String body = "<h3>Overdue Fine Notice</h3>"
                + "<p>Dear " + userName + ",</p>"
                + "<p>A fine has been assessed for the late return of:</p>"
                + "<table border='1' cellpadding='6'>"
                + "<tr><td><b>Book Title</b></td><td>" + bookTitle + "</td></tr>"
                + "<tr><td><b>Fine Amount</b></td><td>₹" + String.format("%.2f", fineAmount) + "</td></tr>"
                + "</table>"
                + "<p>Please clear your fine at the book bank office at your earliest convenience.</p>"
                + "<p>Regards,<br>Book Bank Management System</p>";
        sendEmail(toEmail, subject, body);
    }

    /** Sends a due date reminder email. */
    public void sendDueDateReminder(String toEmail, String userName,
                                    String bookTitle, LocalDate dueDate) throws MessagingException {
        String subject = "Due Date Reminder – " + bookTitle;
        String body = "<h3>Book Return Reminder</h3>"
                + "<p>Dear " + userName + ",</p>"
                + "<p>This is a reminder that the following book is due soon:</p>"
                + "<table border='1' cellpadding='6'>"
                + "<tr><td><b>Book Title</b></td><td>" + bookTitle + "</td></tr>"
                + "<tr><td><b>Due Date</b></td><td>" + dueDate + "</td></tr>"
                + "</table>"
                + "<p>Please return the book on time to avoid late fees.</p>"
                + "<p>Regards,<br>Book Bank Management System</p>";
        sendEmail(toEmail, subject, body);
    }

    // ── Private helper ───────────────────────────────────────────────────────

    private void sendEmail(String to, String subject, String htmlBody) throws MessagingException {
        Message message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(AppConfig.getMailFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlBody, "text/html; charset=utf-8");
        Transport.send(message);
    }
}

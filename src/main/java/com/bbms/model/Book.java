package com.bbms.model;

/**
 * Represents a book title in the BBMS inventory.
 */
public class Book {

    private int    bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int    publicationYear;
    private String edition;
    private int    totalCopies;
    private int    availableCopies;
    private String department;

    public Book() {}

    public Book(String isbn, String title, String author, String publisher,
                int publicationYear, String edition,
                int totalCopies, int availableCopies, String department) {
        this.isbn            = isbn;
        this.title           = title;
        this.author          = author;
        this.publisher       = publisher;
        this.publicationYear = publicationYear;
        this.edition         = edition;
        this.totalCopies     = totalCopies;
        this.availableCopies = availableCopies;
        this.department      = department;
    }

    /**
     * Validates ISBN-13 format (digits only, length 13).
     * Full check-digit validation included.
     */
    public boolean validateISBN() {
        if (isbn == null) return false;
        String digits = isbn.replaceAll("[^0-9]", "");
        if (digits.length() != 13) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == (digits.charAt(12) - '0');
    }

    public boolean isAvailable() { return availableCopies > 0; }

    public void decreaseAvailableCopies() {
        if (availableCopies > 0) availableCopies--;
    }

    public void increaseAvailableCopies() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public int    getBookId()                    { return bookId; }
    public void   setBookId(int v)               { this.bookId = v; }

    public String getIsbn()                      { return isbn; }
    public void   setIsbn(String v)              { this.isbn = v; }

    public String getTitle()                     { return title; }
    public void   setTitle(String v)             { this.title = v; }

    public String getAuthor()                    { return author; }
    public void   setAuthor(String v)            { this.author = v; }

    public String getPublisher()                 { return publisher; }
    public void   setPublisher(String v)         { this.publisher = v; }

    public int    getPublicationYear()           { return publicationYear; }
    public void   setPublicationYear(int v)      { this.publicationYear = v; }

    public String getEdition()                   { return edition; }
    public void   setEdition(String v)           { this.edition = v; }

    public int    getTotalCopies()               { return totalCopies; }
    public void   setTotalCopies(int v)          { this.totalCopies = v; }

    public int    getAvailableCopies()           { return availableCopies; }
    public void   setAvailableCopies(int v)      { this.availableCopies = v; }

    public String getDepartment()                { return department; }
    public void   setDepartment(String v)        { this.department = v; }
}

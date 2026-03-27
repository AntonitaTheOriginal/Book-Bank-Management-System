package com.bbms.model;

import java.time.LocalDate;

/**
 * Represents a system user (Admin, Student, or Faculty).
 */
public class User {

    public enum UserType   { STUDENT, FACULTY, ADMIN }
    public enum UserStatus { ACTIVE, INACTIVE, SUSPENDED }

    private String    userId;
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    phone;
    private String    department;
    private UserType  userType;
    private UserStatus status;
    private LocalDate registrationDate;
    private String    passwordHash;

    public User() {}

    public User(String userId, String firstName, String lastName,
                String email, String phone, String department,
                UserType userType, UserStatus status,
                LocalDate registrationDate) {
        this.userId           = userId;
        this.firstName        = firstName;
        this.lastName         = lastName;
        this.email            = email;
        this.phone            = phone;
        this.department       = department;
        this.userType         = userType;
        this.status           = status;
        this.registrationDate = registrationDate;
    }

    /** Returns max books this user type may borrow simultaneously. */
    public int getBorrowingLimit() {
        if (userType == UserType.FACULTY) return 10;
        if (userType == UserType.STUDENT) return 5;
        return 0;
    }

    /** Returns the loan period in days for this user type. */
    public int getLoanDays() {
        return 120; // same for student and faculty per SRS
    }

    public String getFullName() { return firstName + " " + lastName; }

    // ── Getters & Setters ───────────────────────────────────────────────────

    public String    getUserId()           { return userId; }
    public void      setUserId(String v)   { this.userId = v; }

    public String    getFirstName()              { return firstName; }
    public void      setFirstName(String v)      { this.firstName = v; }

    public String    getLastName()               { return lastName; }
    public void      setLastName(String v)       { this.lastName = v; }

    public String    getEmail()                  { return email; }
    public void      setEmail(String v)          { this.email = v; }

    public String    getPhone()                  { return phone; }
    public void      setPhone(String v)          { this.phone = v; }

    public String    getDepartment()             { return department; }
    public void      setDepartment(String v)     { this.department = v; }

    public UserType  getUserType()               { return userType; }
    public void      setUserType(UserType v)     { this.userType = v; }

    public UserStatus getStatus()                { return status; }
    public void       setStatus(UserStatus v)    { this.status = v; }

    public LocalDate getRegistrationDate()             { return registrationDate; }
    public void      setRegistrationDate(LocalDate v)  { this.registrationDate = v; }

    public String    getPasswordHash()               { return passwordHash; }
    public void      setPasswordHash(String v)       { this.passwordHash = v; }
}

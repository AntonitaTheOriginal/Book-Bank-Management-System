# Book Bank Management System (BBMS) — Architecture & Design Document

> **Project:** Book Bank Management System — Batch 15, Object-Oriented Software Engineering  
> **Stack:** Java 11 · Jakarta EE (Servlet 4.0 / JSP 2.3) · MySQL 8 · Maven · Apache Tomcat  
> **Build Output:** `bbms.war` (deployed to Tomcat)

---

## Table of Contents

1. [Application Overview](#1-application-overview)
2. [Architectural Style](#2-architectural-style)
3. [Design Patterns Used](#3-design-patterns-used)
4. [User Interface Pattern](#4-user-interface-pattern)
5. [Package & Layer Breakdown](#5-package--layer-breakdown)
6. [Database Design](#6-database-design)
7. [Security Architecture](#7-security-architecture)
8. [Module Walkthroughs](#8-module-walkthroughs)
9. [Diagnostics & Testing](#9-diagnostics--testing)
10. [Configuration Management](#10-configuration-management)
11. [Technology Stack & Dependencies](#11-technology-stack--dependencies)
12. [Request–Response Flow (End-to-End Example)](#12-requestresponse-flow-end-to-end-example)

---

## 1. Application Overview

BBMS is a **university Book Bank Management System** that automates the lifecycle of physical book lending. It replaces manual registers with a web-based system supporting:

| Feature | Description |
|---|---|
| **Authentication** | Role-aware login using BCrypt-hashed passwords |
| **Book Catalog** | Add, edit, delete, search books with multi-criteria filtering |
| **Issue / Return** | Admin-controlled book lending to students and faculty |
| **Fine Management** | Automatic overdue fine calculation with pay/waive controls |
| **Dashboard** | Role-specific summary statistics and quick-access panels |
| **Reports** | Admin view of all transactions, overdue items, pending fines |
| **Email Notification** | JavaMail integration for overdue alerts (via `EmailService`) |

### User Roles

```
ADMIN      → Full access: CRUD books, manage users, issue/return books, manage fines
FACULTY    → View own transactions, view own fines; borrow up to 10 books (120-day loan)
STUDENT    → View own transactions, view own fines; borrow up to 5 books (120-day loan)
```

---

## 2. Architectural Style

### Classic Three-Tier Web Architecture (MVC on the Server)

BBMS follows a **three-tier architecture** implemented as a Java EE web application:

```
┌─────────────────────────────────────────────────────────────────┐
│  PRESENTATION TIER  (JSP + JSTL)                                │
│  /views/admin/*.jsp   /views/student/*.jsp   /views/common/*.jsp│
└──────────────────────┬──────────────────────────────────────────┘
                       │  HTTP Request/Response (forward / redirect)
┌──────────────────────▼──────────────────────────────────────────┐
│  BUSINESS LOGIC TIER  (Servlets + Services)                     │
│  Servlets: LoginServlet, BookServlet, TransactionServlet, ...   │
│  Services: AuthService, BookService, EmailService               │
└──────────────────────┬──────────────────────────────────────────┘
                       │  JDBC (PreparedStatement)
┌──────────────────────▼──────────────────────────────────────────┐
│  DATA TIER  (DAOs + MySQL)                                      │
│  BookDAO, UserDAO, TransactionDAO, FineDAO → bbms_db (MySQL)    │
└─────────────────────────────────────────────────────────────────┘
```

**Why this style?**

- The application is a **transactional, form-based CRUD system**. A classic three-tier MVC is the most appropriate match because it enforces clear separation of presentation, business logic, and data persistence.
- Using JSP + Servlets (the Java EE standard) means no additional framework overhead (no Spring, no Hibernate), keeping the codebase understandable for a student project while demonstrating core Java EE competency.
- The stateless HTTP nature of servlets maps naturally to request-oriented CRUD operations.

### MVC (Model–View–Controller) Variant

| MVC Role | BBMS Component |
|---|---|
| **Model** | `com.bbms.model` (Plain Old Java Objects: `Book`, `User`, `Transaction`, `Fine`) |
| **View** | JSP files under `src/main/webapp/views/` (rendered server-side) |
| **Controller** | `com.bbms.servlet` package (one servlet per resource/feature) |

The controller (Servlet) receives an HTTP request, invokes the service/DAO layer, attaches result objects as request attributes, and forwards to the appropriate JSP view.

---

## 3. Design Patterns Used

### 3.1 Data Access Object (DAO) Pattern

**Location:** `com.bbms.dao` — `BookDAO`, `UserDAO`, `TransactionDAO`, `FineDAO`

**What it does:** Every DAO class encapsulates all SQL for one domain entity. No SQL leaks into the service or servlet layers.

```java
// Example: BookDAO completely isolates SQL from business logic
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
```

**Why used:**
- Decouples persistence logic from business logic; the database engine or schema can change without touching servlets.
- `PreparedStatement` throughout the DAO layer prevents SQL injection.
- A private `mapRow(ResultSet rs)` method in each DAO converts raw JDBC rows into typed Java objects — a form of the **Mapper** sub-pattern.

---

### 3.2 Service Layer Pattern (Façade)

**Location:** `com.bbms.service` — `AuthService`, `BookService`, `EmailService`

**What it does:** The Service classes orchestrate multi-step business operations that span more than one DAO call and enforce business rules.

```java
// BookService.issueBook() — a multi-step business operation
public String issueBook(String userId, int bookId) throws SQLException {
    // 1. Validate user exists and is active
    // 2. Check borrowing limit (5 for student, 10 for faculty)
    // 3. Check user has no unpaid fines above threshold
    // 4. Verify book is available
    // 5. Atomically: insert transaction + decrement available_copies
    // 6. Trigger email notification
    // Returns "SUCCESS:..." or "ERROR:..." string
}
```

**Why used:**
- The Servlet (controller) becomes thin — it only reads HTTP parameters, calls one service method, and forwards.
- Business rules (borrowing limits, fine thresholds, loan periods) are centralised in one place.
- The `AuthService` specifically acts as a **Security Façade**, hiding BCrypt internals from the servlet.

---

### 3.3 Singleton (Static Utility) Pattern

**Location:** `DBConnection`, `AppConfig`

**What it does:** Both classes use Java's `static {}` initializer to load configuration once at class-loading time and expose only static factory/accessor methods.

```java
// AppConfig — loaded once, accessed everywhere
static {
    try (InputStream is = AppConfig.class.getClassLoader()
            .getResourceAsStream("database.properties")) {
        props.load(is);
    } catch (IOException e) {
        throw new RuntimeException("Cannot load application config", e);
    }
}
public static double getFineRatePerDay() {
    return Double.parseDouble(props.getProperty("app.fine.rate", "1.0"));
}
```

**Why used:**
- Configuration (DB URL, credentials, fine rates) must be loaded once and be globally available without passing objects around.
- The static initializer ensures a fail-fast on startup if the properties file is missing.

---

### 3.4 Filter Chain Pattern (Chain of Responsibility)

**Location:** `com.bbms.util.AuthFilter`, `com.bbms.util.EncodingFilter`

**What it does:** Servlet Filters intercept every matching request *before* it reaches the servlet.

```
Browser Request
    │
    ▼
EncodingFilter (/*) — sets UTF-8 on every request/response
    │
    ▼
AuthFilter (/dashboard, /books, /transactions, /users, /fines, /reports)
    │  → if not logged in → redirect to /login
    ▼
Target Servlet
```

**`AuthFilter`** checks the HTTP session for the `"user"` attribute. If absent, it redirects to `/login`. Protected URLs are declared both in `@WebFilter` and in `web.xml`.

**`EncodingFilter`** applies UTF-8 encoding to every request and response, preventing mojibake with multilingual book titles or names.

**Why used:**
- Cross-cutting concerns (authentication, encoding) must not be duplicated in every servlet. The filter pipeline is the standard Java EE mechanism for this.
- Follows the **Chain of Responsibility** GoF pattern: each filter does its job and passes the request down the chain.

---

### 3.5 Strategy Pattern (via Enums + Business Rules)

**Location:** `User.UserType` enum + `User.getBorrowingLimit()` / `User.getLoanDays()`

**What it does:** The borrowing rules differ by user type. Rather than scattered `if/else` blocks across the codebase, the rule is embedded in the model:

```java
public int getBorrowingLimit() {
    if (userType == UserType.FACULTY) return 10;
    if (userType == UserType.STUDENT) return 5;
    return 0;  // ADMIN cannot borrow
}
```

The `Fine.calculateFineAmount(long overdueDays)` is a static strategy that encapsulates:
- **Grace period** (2 days from `AppConfig`)
- **Rate** (₹1/day from `AppConfig`)
- **Cap** (₹50 max from `AppConfig`)

**Why used:**
- Business rules are localised to the model/service, not scattered across JSPs or servlets.
- Adding a new user type (e.g., `LIBRARIAN`) requires changing only the `UserType` enum and model methods.

---

### 3.6 Front Controller (via Servlet Action Dispatch)

Each servlet serves as a **mini Front Controller** for its domain using an `action` query parameter to dispatch sub-operations:

```java
// BookServlet.doGet()
switch (action) {
    case "add":    → forward to addBook.jsp
    case "edit":   → forward to editBook.jsp
    case "delete": → delete + redirect
    default:       → list all books
}
```

This avoids creating a separate servlet for every single operation while keeping all book-related logic in one place.

---

### 3.7 Template Method (JSP Includes / `mapRow`)

Every DAO has a **private `mapRow(ResultSet rs)` method** that converts a JDBC row to a model object. This is the canonical mapping logic reused across all query methods (get by ID, list all, search). This is an instance of the **Template Method** pattern at the data mapping level.

---

## 4. User Interface Pattern

### Server-Side Rendered MVC (JSP + JSTL)

The UI is divided into three view groups:

```
src/main/webapp/views/
├── admin/
│   ├── dashboard.jsp      ← Admin KPIs: books, users, transactions, overdue
│   ├── addBook.jsp / editBook.jsp
│   ├── addUser.jsp / editUser.jsp
│   ├── userList.jsp
│   ├── issueBook.jsp      ← Issue a book to a user
│   ├── returnBook.jsp     ← Select active transaction to return
│   ├── fineList.jsp       ← All fines with pay/waive actions
│   └── reports.jsp        ← Aggregate reports
├── student/
│   ├── dashboard.jsp      ← My borrowed books, my fines
│   └── myFines.jsp        ← Student fine detail view
└── common/
    ├── login.jsp          ← Authentication form
    ├── books.jsp          ← Searchable book catalog (both roles)
    ├── transactions.jsp   ← Transaction history (role-filtered)
    └── error.jsp          ← 404/500 error page
```

### UI Patterns Employed

| Pattern | Where |
|---|---|
| **Role-Based View Switching** | `DashboardServlet` and `FineServlet` forward to completely different JSPs based on `user.getUserType()` |
| **Flash Message Pattern** | `session.setAttribute("flash", "...")` is set before every redirect; the JSP reads and clears it — ensures the "Book deleted successfully" message survives the POST–Redirect–GET cycle |
| **Post–Redirect–Get (PRG)** | All POST handlers (add/edit/delete) end with `resp.sendRedirect(...)` to prevent double-form-submission on browser refresh |
| **Server-Side Form Validation** | Invalid ISBN-13, duplicate ISBN, missing fields — all validated in the Servlet before the DAO is called. Errors are attached as `req.setAttribute("error", ...)` and the form is re-displayed |
| **Progressive Disclosure** | Admin pages show action buttons (Edit, Delete, Issue, Pay, Waive); Student pages are read-only |

### JSTL & EL Usage

JSP pages use JSTL (`<c:forEach>`, `<c:if>`, `<c:choose>`) and Expression Language (`${book.title}`) to render data. No scriptlets (`<% ... %>`) are used in views, keeping presentation logic clean.

---

## 5. Package & Layer Breakdown

```
com.bbms/
├── model/                   ← Domain entities (POJOs)
│   ├── Book.java            ISBN validation, copy management
│   ├── User.java            Role enums, borrowing limits
│   ├── Transaction.java     Overdue calculation, due date
│   └── Fine.java            Fine calculation with grace period + cap
│
├── dao/                     ← Database access (JDBC only)
│   ├── BookDAO.java         CRUD + multi-criteria search
│   ├── UserDAO.java         CRUD + password update
│   ├── TransactionDAO.java  Issue/return, active/overdue queries
│   └── FineDAO.java         Fine CRUD, pay/waive, pending totals
│
├── service/                 ← Business logic orchestration
│   ├── AuthService.java     BCrypt authentication + password change
│   ├── BookService.java     Issue/return workflow, copy management
│   └── EmailService.java    JavaMail overdue notifications
│
├── servlet/                 ← HTTP controllers (one per resource)
│   ├── LoginServlet.java    GET: login page / POST: authenticate
│   ├── LogoutServlet.java   Invalidates session, redirects to login
│   ├── DashboardServlet.java Role-based KPI dashboard
│   ├── BookServlet.java     Book CRUD + search
│   ├── UserServlet.java     User CRUD (admin only)
│   ├── TransactionServlet.java Issue/return/list
│   ├── FineServlet.java     Fine list/pay/waive
│   └── ReportServlet.java   Aggregate reporting view
│
└── util/                    ← Cross-cutting infrastructure
    ├── DBConnection.java    JDBC connection factory
    ├── AppConfig.java       Property-file configuration reader
    ├── AuthFilter.java      Session-based authentication filter
    ├── EncodingFilter.java  UTF-8 encoding filter
    ├── HashGenerator.java   CLI tool: print a BCrypt hash
    ├── AuthTester.java      Diagnostic: test login for ADMIN001
    ├── DBTester.java        Diagnostic: connect + list all users
    ├── DbCheck.java         Diagnostic: raw JDBC connectivity check
    └── TestDBConnection.java Diagnostic: connection pool test
```

---

## 6. Database Design

The database (`bbms_db`) has **4 core tables** with enforced referential integrity:

```
users ──────────────────────────────────────────────────────────┐
│ PK: user_id (VARCHAR 20)                                      │
│ user_type: ENUM(STUDENT, FACULTY, ADMIN)                      │
│ status:    ENUM(ACTIVE, INACTIVE, SUSPENDED)                  │
│ password_hash: BCrypt hash (60-char)                          │
└───────────────────────────────────────────────────────────────┘
        │ FK: fk_txn_user
        ▼
transactions ───────────────────────────────────────────────────┐
│ PK: transaction_id (INT AUTO_INCREMENT)                       │
│ FK: user_id → users.user_id                                   │
│ FK: book_id → books.book_id                                   │
│ status: ENUM(ISSUED, RETURNED, OVERDUE)                       │
│ issue_date, due_date, return_date                             │
└───────────────────────────────────────────────────────────────┘
        │ FK: fk_fine_txn                    ▲ FK: fk_txn_book
        ▼                                   │
fines ─────────────────────────────  books ─────────────────────
│ PK: fine_id (INT AUTO_INCREMENT) │  PK: book_id (INT AI)      │
│ FK: transaction_id               │  isbn: UNIQUE              │
│ status: ENUM(PENDING,PAID,WAIVED)│  available_copies + CHECK  │
│ amount: DECIMAL(6,2)             │  constraint (0 ≤ avail ≤   │
│ issued_date, paid_date           │  total)                    │
└──────────────────────────────────┘ ──────────────────────────┘
```

### Key Database Constraints

| Constraint | Purpose |
|---|---|
| `UNIQUE KEY uk_isbn` on `books` | Prevents duplicate book entries |
| `UNIQUE KEY uk_email` on `users` | Prevents same email for two accounts |
| `CHECK (available_copies <= total_copies AND available_copies >= 0)` | Guard against inventory going negative |
| Cascading FK relationships | Transactions cannot reference non-existent users or books |

### Database Views (for Reports)

```sql
v_active_transactions  → All currently ISSUED books with user/book details
v_overdue_transactions → Subset of above where DATEDIFF > 0
v_pending_fines        → All PENDING fines joined with user and book details
```

### Stored Procedure & Scheduled Event

```sql
sp_mark_overdue()        → Updates ISSUED → OVERDUE for past-due-date rows
ev_mark_overdue          → MySQL Event Scheduler calls sp_mark_overdue() daily
```

This ensures the database status stays accurate even without application activity.

---

## 7. Security Architecture

### Authentication

1. Admin/librarian enters **University ID + Password** on `login.jsp`.
2. `LoginServlet.doPost()` calls `AuthService.authenticate(userId, plainPassword)`.
3. `AuthService` retrieves the stored **BCrypt hash** from `UserDAO.getUserById()`.
4. `BCrypt.checkpw(plainPassword, storedHash)` — BCrypt handles salt internally.
5. On success: `HttpSession` is created with `"user"` attribute (User object).
6. Session timeout: **30 minutes** (set in `web.xml` and in `LoginServlet`).

### Password Security

- Passwords are never stored in plaintext. The database stores a `$2a$10$...` BCrypt hash.
- **10 rounds** of BCrypt work factor (`BCrypt.gensalt(10)`) — a good balance for an academic system.
- The `HashGenerator` utility class can generate a fresh hash from the command line for seeding.

### Authorization

- **`AuthFilter`** guards: `/dashboard`, `/books`, `/transactions`, `/users`, `/fines`, `/reports`.
- Admin-only operations (add/edit/delete book, issue, waive fine) are re-checked inside each Servlet with a `requireAdmin()` helper that sends non-admins back to `/dashboard`.

```java
// Pattern used in BookServlet and TransactionServlet
private void requireAdmin(HttpSession session, HttpServletResponse resp)
        throws IOException {
    User u = (User) session.getAttribute("user");
    if (u == null || u.getUserType() != User.UserType.ADMIN)
        resp.sendRedirect("dashboard");
}
```

### SQL Injection Prevention

Every database query uses `PreparedStatement` with parameterised placeholders (`?`). No string concatenation of user input into SQL.

---

## 8. Module Walkthroughs

### 8.1 Book Issue Workflow

```
Admin fills issueBook.jsp form (userId + bookId)
    │
    ▼
TransactionServlet.doPost("issue")
    │─ validates non-blank input
    ▼
BookService.issueBook(userId, bookId)
    │─ [1] UserDAO.getUserById(userId)       — user exists?
    │─ [2] user.getStatus() == ACTIVE        — not suspended?
    │─ [3] TransactionDAO.countActiveByUser()≤ borrowingLimit?
    │─ [4] FineDAO.getTotalPendingFineByUser() < maxThreshold?
    │─ [5] BookDAO.getBookById(bookId)       — book exists?
    │─ [6] book.isAvailable()               — copies > 0?
    │─ [7] TransactionDAO.addTransaction()  — insert record
    │─ [8] BookDAO.decrementAvailableCopies()
    │─ [9] EmailService.sendIssueConfirmation() (best-effort)
    │
    Returns: "SUCCESS: Book issued. Due: YYYY-MM-DD"
         OR: "ERROR: User has reached borrowing limit."
    │
    ▼
TransactionServlet — flash message set → redirect to /transactions
```

### 8.2 Book Return & Fine Generation

```
Admin selects transaction from returnBook.jsp
    │
    ▼
TransactionServlet.doPost("return")
    │
    ▼
BookService.returnBook(userId, bookId)
    │─ Find active transaction (status=ISSUED)
    │─ Set returnDate = today, status = RETURNED
    │─ TransactionDAO.returnBook() — update DB
    │─ BookDAO.incrementAvailableCopies()
    │─ transaction.getOverdueDays() > 0?
    │     └─ Fine.calculateFineAmount(overdueDays)
    │           ─ grace period (2 days)
    │           ─ rate (₹1/day) × chargeable days
    │           ─ Math.min(result, ₹50 cap)
    │     └─ FineDAO.addFine(transactionId, amount)
    │
    Returns "SUCCESS: Book returned. Fine: ₹X.XX"
```

### 8.3 Fine Management

```
Admin visits /fines
    │─ FineDAO.getAllFines() → fineList.jsp (all fines, all users)
    │
    ├─ action=pay?id=N  → FineDAO.markAsPaid(N)  → sets paid_date=today, status=PAID
    └─ action=waive?id=N → FineDAO.markAsWaived(N) → status=WAIVED

Student visits /fines
    └─ FineDAO.getFinesByUser(userId) → myFines.jsp (read-only, their fines only)
```

---

## 9. Diagnostics & Testing

The project has **no JUnit test suite**. Instead, testing is performed through a set of **manual diagnostic tools** and **runtime debug logging** developed and used during the debugging iterations.

### 9.1 Diagnostic Utility Classes (in `com.bbms.util`)

These are standalone `main()` programs run via Maven (`mvn exec:java`) or directly from an IDE.

#### `DBConnection` — Basic JDBC Test
**Purpose:** Verifies raw JDBC connectivity (driver loaded, URL reachable, credentials valid).
```java
// Used in DbCheck.java and TestDBConnection.java
try (Connection conn = DBConnection.getConnection()) {
    System.out.println("SUCCESS: Connected.");
    System.out.println("URL: " + conn.getMetaData().getURL());
}
```
**What this tests:**
- `database.properties` is correctly loaded from the classpath
- MySQL server is running on the configured port (3307)
- `db.username` / `db.password` are accepted by MySQL

#### `DBTester.java` — User Table Verification
**Purpose:** Connects to DB, retrieves all users, and prints ID, name, type, status, **and the stored BCrypt hash**.
```
--- Registered Users (6) ---
ID: ADMIN001      | Name: System Administrator    | Role: ADMIN    | Status: ACTIVE | Hash: $2a$10$N9qo...
ID: STU2024001    | Name: Arjun Mehta             | Role: STUDENT  | Status: ACTIVE | Hash: $2a$10$hiB...
```
**What this tests:**
- Database is correctly seeded (SQL script was run)
- `password_hash` column is not null (otherwise `BCrypt.checkpw` crashes)
- `user_type` and `status` strings match the Java enum values exactly (`STUDENT`, not `Student`)

#### `AuthTester.java` — End-to-End Authentication Test
**Purpose:** Runs the full `AuthService.authenticate()` flow for a known credential pair without spinning up Tomcat.
```java
AuthService authService = new AuthService();
User user = authService.authenticate("ADMIN001", "Admin@123");
// Prints: SUCCESS or FAILURE with debug details
```
**What this tests:**
- `BCrypt.checkpw(plaintext, storedHash)` returns `true`
- User status is `ACTIVE`
- `AuthService` → `UserDAO` → `DBConnection` integration works end-to-end
- This was the primary tool used to diagnose the "invalid credentials" bug sequence in debugging sessions

#### `HashGenerator.java` — Password Hash Generator
**Purpose:** Generates a fresh BCrypt hash for a given plaintext password. Used when re-seeding the database.
```java
String hash = BCrypt.hashpw("Admin@123", BCrypt.gensalt(10));
System.out.println(hash);
// → $2a$10$...
```
**What this tests / verifies:**
- The hash format (`$2a$10$...`) matches what BCrypt expects for comparison
- Used to regenerate seed hashes in `bbms_database.sql` when the stored hash was suspected to be corrupted

#### `DbCheck.java` / `TestDBConnection.java`
Additional connection diagnostic tools created during the MySQL port troubleshooting (port 3307 vs 3306) to isolate whether the failure was network-level or credential-level.

---

### 9.2 In-Code Debug Logging (Runtime Testing)

`AuthService.authenticate()` has **explicit debug print statements** at each failure point:

```java
if (user == null) {
    System.out.println("[DEBUG] Auth FAILED: User ID '" + userId + "' not found.");
}
if (user.getStatus() != User.UserStatus.ACTIVE) {
    System.out.println("[DEBUG] Auth FAILED: User is not ACTIVE. Status: " + user.getStatus());
}
if (user.getPasswordHash() == null) {
    System.out.println("[DEBUG] Auth FAILED: Password hash is null.");
}
if (!BCrypt.checkpw(plainPassword, user.getPasswordHash())) {
    System.out.println("[DEBUG] Auth FAILED: Password mismatch.");
}
System.out.println("[DEBUG] Auth SUCCESS: Logged in as " + userId);
```

**Purpose:** These print to the Tomcat console log (`catalina.out`). During debugging, the exact failure reason could be read from the server console, eliminating all ambiguity about which step failed.

---

### 9.3 Database Verification Queries (in SQL Script)

The `bbms_database.sql` script itself contains test queries at the bottom:

```sql
-- VERIFICATION QUERIES (run after setup)
SELECT * FROM users;
SELECT * FROM books;
SELECT COUNT(*) AS total_books FROM books;
SELECT COUNT(*) AS total_users FROM users;

SELECT 'BBMS Database setup complete!' AS Status;
SELECT CONCAT(COUNT(*), ' users created')  AS Info FROM users  UNION ALL
SELECT CONCAT(COUNT(*), ' books inserted') AS Info FROM books;
```

**Expected output:**
```
Status: BBMS Database setup complete!
Info:   6 users created
Info:   14 books inserted
```

---

### 9.4 Business Logic Validation Tests (Model-Level)

Some business rules are validated directly in model or DAO methods:

| Test | Where | Rule Tested |
|---|---|---|
| `Book.validateISBN()` | `Book.java` | ISBN-13 check-digit algorithm |
| `book.isAvailable()` | `Book.java` | `availableCopies > 0` |
| `BookDAO.deleteBook()` internal check | `BookDAO.java` | Block delete if active transactions exist |
| `transaction.isOverdue()` | `Transaction.java` | `now().isAfter(dueDate)` |
| `transaction.getOverdueDays()` | `Transaction.java` | `ChronoUnit.DAYS.between(dueDate, checkDate)` |
| Fine grace + cap | `Fine.calculateFineAmount()` | `overdue - grace ≤ 0 → ₹0`; `rate × days → max ₹50` |

**ISBN-13 Validation Deep Dive:**
```java
public boolean validateISBN() {
    String digits = isbn.replaceAll("[^0-9]", "");
    if (digits.length() != 13) return false;
    int sum = 0;
    for (int i = 0; i < 12; i++) {
        int d = digits.charAt(i) - '0';
        sum += (i % 2 == 0) ? d : d * 3;   // alternating weight 1 and 3
    }
    int check = (10 - (sum % 10)) % 10;
    return check == (digits.charAt(12) - '0');  // compare to check digit
}
```
The `BookServlet` calls `book.validateISBN()` before any DAO call and re-renders the form with an error if it fails — preventing invalid data from ever reaching the database.

---

### 9.5 Servlet-Level Validation

| Scenario | Validation | Response |
|---|---|---|
| Blank userId or password at login | `isBlank()` check | Redisplay login.jsp with error |
| Duplicate ISBN on book add | `BookDAO.getBookByIsbn() != null` | Redisplay addBook.jsp with error |
| Invalid ISBN-13 format | `book.validateISBN()` returns false | Redisplay addBook.jsp with error |
| Non-numeric year/copies input | `try { Integer.parseInt(...) } catch` | Logged as `System.err`, defaults used |
| Non-numeric bookId in transaction | `try { Integer.parseInt(...) } catch` | Error attribute set, form re-displayed |
| Non-numeric fineId in pay/waive | `try { Integer.parseInt(...) } catch (NumberFormatException)` | Flash error set, redirect |

---

## 10. Configuration Management

All tunable values live in `src/main/resources/database.properties`:

```properties
# Database
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3307/bbms_db?useSSL=false&serverTimezone=UTC
db.username=root
db.password=...

# Fine rules
app.fine.rate=1.0          # ₹1 per day
app.fine.max=50.0          # Max fine ₹50
app.fine.grace.days=2      # 2-day grace period

# Borrowing limits
app.loan.days.student=120
app.loan.days.faculty=120
app.max.books.student=5
app.max.books.faculty=10
app.max.fine.threshold=50.0  # Block issue if pending fines exceed this

# Email (JavaMail)
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.username=...
mail.password=...
mail.from=bbms@university.edu
```

`AppConfig` reads these once on class load. Changing a fine rate or loan period requires only editing this file and redeploying — no code change needed.

---

## 11. Technology Stack & Dependencies

| Component | Technology | Version |
|---|---|---|
| Language | Java | 11 |
| Web Container | Apache Tomcat | 9.x (Servlet 4.0) |
| Build Tool | Maven | 3.x |
| Servlet API | `javax.servlet-api` | 4.0.1 |
| JSP API | `javax.servlet.jsp-api` | 2.3.3 |
| JSTL | `javax.servlet:jstl` | 1.2 |
| Database | MySQL | 8.x |
| JDBC Driver | `mysql-connector-java` | 8.0.33 |
| Password Hashing | `org.mindrot:jbcrypt` | 0.4 |
| Email | `com.sun.mail:javax.mail` | 1.6.2 |
| Packaging | WAR | — |

---

## 12. Request–Response Flow (End-to-End Example)

**Scenario: Admin logs in, then issues a book to a student.**

```
1. Browser → GET /bbms/login
      ↓ AuthFilter: no session → passes through (login is unprotected)
      ↓ LoginServlet.doGet() → forward to /views/common/login.jsp

2. Admin fills form → POST /bbms/login
      ↓ LoginServlet.doPost()
      ↓ AuthService.authenticate("ADMIN001", "Admin@123")
      ↓   UserDAO.getUserById("ADMIN001") → SELECT * FROM users WHERE user_id=?
      ↓   BCrypt.checkpw("Admin@123", "$2a$10$N9qo...") → TRUE
      ↓ HttpSession created: session.setAttribute("user", userObject)
      ↓ sendRedirect → /bbms/dashboard

3. Browser → GET /bbms/dashboard
      ↓ AuthFilter: session has "user" → passes through
      ↓ DashboardServlet.doGet()
      ↓   user.getUserType() == ADMIN
      ↓   BookService.countTotalBooks() → SELECT COUNT(*) FROM books  → 14
      ↓   UserDAO.countActiveUsers()   → SELECT COUNT(*) FROM users WHERE status='ACTIVE' → 6
      ↓   TransactionDAO.countTotalTransactions() → ...
      ↓   TransactionDAO.getOverdueTransactions() → list
      ↓   FineDAO.getAllPendingFines() → list
      ↓ forward to /views/admin/dashboard.jsp (renders with KPI data)

4. Admin clicks "Issue Book" → GET /bbms/transactions?action=issue
      ↓ AuthFilter: passes
      ↓ TransactionServlet.doGet("issue")
      ↓   requireAdmin() → user is ADMIN → OK
      ↓   bookService.getAllBooks() → SELECT * FROM books ORDER BY title → 14 books
      ↓ forward to /views/admin/issueBook.jsp

5. Admin fills form → POST /bbms/transactions  (action=issue, userId=STU2024001, bookId=5)
      ↓ TransactionServlet.doPost("issue")
      ↓ BookService.issueBook("STU2024001", 5)
      ↓   UserDAO.getUserById("STU2024001") → found, ACTIVE
      ↓   TransactionDAO.countActiveByUser("STU2024001") → 0 < 5 limit → OK
      ↓   FineDAO.getTotalPendingFineByUser("STU2024001") → ₹0 < ₹50 threshold → OK
      ↓   BookDAO.getBookById(5) → found, availableCopies=5 > 0 → OK
      ↓   TransactionDAO.addTransaction(...) → INSERT INTO transactions → id=1
      ↓   BookDAO.decrementAvailableCopies(5) → UPDATE books SET available_copies=available_copies-1
      ↓   EmailService.sendIssueConfirmation(...) → (best-effort SMTP)
      ↓   Returns "SUCCESS: Book 'Introduction to Algorithms' issued. Due: 2026-07-25"
      ↓ session.setAttribute("flash", "Book ... issued. Due: 2026-07-25")
      ↓ sendRedirect → /bbms/transactions

6. Browser → GET /bbms/transactions
      ↓ TransactionDAO.getAllTransactions() → full list
      ↓ forward to /views/common/transactions.jsp
      ↓ JSP reads and clears flash message → "Book ... issued. Due: 2026-07-25" displayed
```

---

*Document generated: 2026-03-27 | BBMS v1.0*

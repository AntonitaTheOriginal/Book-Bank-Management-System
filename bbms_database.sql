-- ============================================================
--  Book Bank Management System (BBMS) – Database Schema
--  Batch 15 | Object-Oriented Software Engineering
--
--  HOW TO RUN:
--    mysql -u root -p < bbms_database.sql
--  OR open MySQL Workbench and run this file.
-- ============================================================

-- 1. Create & select database
CREATE DATABASE IF NOT EXISTS bbms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bbms_db;

-- ============================================================
-- TABLE: users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id           VARCHAR(20)  NOT NULL,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    email             VARCHAR(100) NOT NULL,
    phone             VARCHAR(15),
    department        VARCHAR(50),
    user_type         ENUM('STUDENT','FACULTY','ADMIN') NOT NULL DEFAULT 'STUDENT',
    status            ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    registration_date DATE         NOT NULL,
    password_hash     VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE: books
-- ============================================================
CREATE TABLE IF NOT EXISTS books (
    book_id           INT          NOT NULL AUTO_INCREMENT,
    isbn              VARCHAR(17)  NOT NULL,
    title             VARCHAR(255) NOT NULL,
    author            VARCHAR(255) NOT NULL,
    publisher         VARCHAR(100),
    publication_year  INT,
    edition           VARCHAR(20),
    total_copies      INT          NOT NULL DEFAULT 1,
    available_copies  INT          NOT NULL DEFAULT 1,
    department        VARCHAR(50),
    PRIMARY KEY (book_id),
    UNIQUE KEY uk_isbn (isbn),
    CONSTRAINT chk_copies CHECK (available_copies <= total_copies AND available_copies >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE: transactions
-- ============================================================
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id    INT  NOT NULL AUTO_INCREMENT,
    user_id           VARCHAR(20) NOT NULL,
    book_id           INT         NOT NULL,
    issue_date        DATE        NOT NULL,
    due_date          DATE        NOT NULL,
    return_date       DATE,
    status            ENUM('ISSUED','RETURNED','OVERDUE') NOT NULL DEFAULT 'ISSUED',
    PRIMARY KEY (transaction_id),
    CONSTRAINT fk_txn_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_txn_book FOREIGN KEY (book_id) REFERENCES books(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- TABLE: fines
-- ============================================================
CREATE TABLE IF NOT EXISTS fines (
    fine_id           INT           NOT NULL AUTO_INCREMENT,
    transaction_id    INT           NOT NULL,
    amount            DECIMAL(6,2)  NOT NULL,
    issued_date       DATE          NOT NULL,
    paid_date         DATE,
    status            ENUM('PENDING','PAID','WAIVED') NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (fine_id),
    CONSTRAINT fk_fine_txn FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- INDEXES for performance
-- ============================================================
CREATE INDEX idx_txn_user   ON transactions(user_id);
CREATE INDEX idx_txn_book   ON transactions(book_id);
CREATE INDEX idx_txn_status ON transactions(status);
CREATE INDEX idx_fine_txn   ON fines(transaction_id);
CREATE INDEX idx_fine_status ON fines(status);
CREATE INDEX idx_book_dept  ON books(department);
CREATE INDEX idx_book_title ON books(title);

-- ============================================================
-- SEED DATA – Sample Users
-- Passwords are BCrypt hashes of the shown plain-text password.
-- All sample accounts use password: Admin@123 / Student@123 / Faculty@123
-- Generate fresh hashes using the app's AuthService.hashPassword() if needed.
-- ============================================================

-- Admin account  (password: Admin@123)
INSERT INTO users (user_id, first_name, last_name, email, phone, department,
                   user_type, status, registration_date, password_hash)
VALUES (
    'ADMIN001',
    'System',
    'Administrator',
    'admin@university.edu',
    '9876543210',
    'Administration',
    'ADMIN',
    'ACTIVE',
    CURDATE(),
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
    -- plain text: Admin@123
);

-- Faculty accounts  (password: Faculty@123)
INSERT INTO users (user_id, first_name, last_name, email, phone, department,
                   user_type, status, registration_date, password_hash)
VALUES
(
    'FAC2024001',
    'Rajesh',
    'Kumar',
    'rajesh.kumar@university.edu',
    '9876543201',
    'Computer Science',
    'FACULTY',
    'ACTIVE',
    CURDATE(),
    '$2a$10$8K1p/3UsTmK3UCEaOZ5GL.zJQMLEbV5GQ5KRbA7cxfH62Pr25CNOW'
    -- plain text: Faculty@123
),
(
    'FAC2024002',
    'Priya',
    'Sharma',
    'priya.sharma@university.edu',
    '9876543202',
    'Electronics',
    'FACULTY',
    'ACTIVE',
    CURDATE(),
    '$2a$10$8K1p/3UsTmK3UCEaOZ5GL.zJQMLEbV5GQ5KRbA7cxfH62Pr25CNOW'
);

-- Student accounts  (password: Student@123)
INSERT INTO users (user_id, first_name, last_name, email, phone, department,
                   user_type, status, registration_date, password_hash)
VALUES
(
    'STU2024001',
    'Arjun',
    'Mehta',
    'arjun.mehta@student.university.edu',
    '9123456781',
    'Computer Science',
    'STUDENT',
    'ACTIVE',
    CURDATE(),
    '$2a$10$hiBdxO9FnH4V.LqJxG0CbuF3HhY4WnkBL9iBKHq1pQ7M.wfaQlFSS'
    -- plain text: Student@123
),
(
    'STU2024002',
    'Sneha',
    'Reddy',
    'sneha.reddy@student.university.edu',
    '9123456782',
    'Computer Science',
    'STUDENT',
    'ACTIVE',
    CURDATE(),
    '$2a$10$hiBdxO9FnH4V.LqJxG0CbuF3HhY4WnkBL9iBKHq1pQ7M.wfaQlFSS'
),
(
    'STU2024003',
    'Vikram',
    'Singh',
    'vikram.singh@student.university.edu',
    '9123456783',
    'Electronics',
    'STUDENT',
    'ACTIVE',
    CURDATE(),
    '$2a$10$hiBdxO9FnH4V.LqJxG0CbuF3HhY4WnkBL9iBKHq1pQ7M.wfaQlFSS'
);

-- ============================================================
-- SEED DATA – Sample Books
-- ============================================================
INSERT INTO books (isbn, title, author, publisher, publication_year, edition,
                   total_copies, available_copies, department)
VALUES
-- Computer Science
('978-0-13-468599-1','Clean Code','Robert C. Martin','Prentice Hall',2008,'1st Edition',3,3,'Computer Science'),
('978-0-13-110362-7','The C Programming Language','Brian W. Kernighan','Prentice Hall',1988,'2nd Edition',4,4,'Computer Science'),
('978-0-13-235088-4','The Pragmatic Programmer','David Thomas','Addison-Wesley',2019,'20th Anniversary',3,3,'Computer Science'),
('978-0-596-51774-8','Head First Design Patterns','Eric Freeman','O\'Reilly Media',2004,'1st Edition',2,2,'Computer Science'),
('978-0-13-597583-5','Introduction to Algorithms','Thomas H. Cormen','MIT Press',2022,'4th Edition',5,5,'Computer Science'),
('978-0-13-468557-1','Refactoring','Martin Fowler','Addison-Wesley',2018,'2nd Edition',2,2,'Computer Science'),

-- Electronics
('978-0-13-277408-5','Microelectronics Circuit Analysis','Donald Neamen','McGraw-Hill',2010,'4th Edition',4,4,'Electronics'),
('978-0-07-338046-7','Electronic Devices and Circuit Theory','Robert Boylestad','Pearson',2012,'11th Edition',3,3,'Electronics'),
('978-0-19-964276-2','The Art of Electronics','Paul Horowitz','Cambridge University Press',2015,'3rd Edition',2,2,'Electronics'),

-- Mathematics
('978-0-07-338087-0','Advanced Engineering Mathematics','Erwin Kreyszig','Wiley',2011,'10th Edition',5,5,'Mathematics'),
('978-0-13-143955-3','Discrete Mathematics and Its Applications','Kenneth H. Rosen','McGraw-Hill',2018,'8th Edition',4,4,'Mathematics'),

-- Physics
('978-0-13-805326-4','University Physics','Hugh D. Young','Pearson',2015,'14th Edition',3,3,'Physics'),

-- Mechanical
('978-0-07-340122-2','Engineering Mechanics: Dynamics','Meriam & Kraige','Wiley',2012,'7th Edition',2,2,'Mechanical'),
('978-0-07-299286-3','Thermodynamics: An Engineering Approach','Cengel & Boles','McGraw-Hill',2014,'8th Edition',3,3,'Mechanical');

-- ============================================================
-- USEFUL VIEWS for reports
-- ============================================================

CREATE OR REPLACE VIEW v_active_transactions AS
SELECT
    t.transaction_id,
    t.user_id,
    CONCAT(u.first_name, ' ', u.last_name) AS user_name,
    u.email,
    b.book_id,
    b.title  AS book_title,
    b.isbn,
    t.issue_date,
    t.due_date,
    DATEDIFF(CURDATE(), t.due_date) AS days_overdue
FROM transactions t
JOIN users u ON t.user_id = u.user_id
JOIN books b ON t.book_id = b.book_id
WHERE t.status = 'ISSUED';

CREATE OR REPLACE VIEW v_overdue_transactions AS
SELECT * FROM v_active_transactions
WHERE days_overdue > 0;

CREATE OR REPLACE VIEW v_pending_fines AS
SELECT
    f.fine_id,
    t.user_id,
    CONCAT(u.first_name, ' ', u.last_name) AS user_name,
    b.title  AS book_title,
    f.amount,
    f.issued_date
FROM fines f
JOIN transactions t ON f.transaction_id = t.transaction_id
JOIN users u ON t.user_id = u.user_id
JOIN books b ON t.book_id = b.book_id
WHERE f.status = 'PENDING';

-- ============================================================
-- STORED PROCEDURE: mark overdue transactions
-- Run this daily via a scheduler (e.g. MySQL Event Scheduler)
-- ============================================================
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS sp_mark_overdue()
BEGIN
    UPDATE transactions
    SET    status = 'OVERDUE'
    WHERE  status = 'ISSUED'
    AND    due_date < CURDATE();
END //
DELIMITER ;

-- Enable the event scheduler and create daily job
SET GLOBAL event_scheduler = ON;

CREATE EVENT IF NOT EXISTS ev_mark_overdue
    ON SCHEDULE EVERY 1 DAY
    STARTS CURRENT_TIMESTAMP
    DO CALL sp_mark_overdue();

-- ============================================================
-- VERIFICATION QUERIES (run these to confirm setup)
-- ============================================================
-- SELECT * FROM users;
-- SELECT * FROM books;
-- SELECT COUNT(*) AS total_books FROM books;
-- SELECT COUNT(*) AS total_users FROM users;

SELECT 'BBMS Database setup complete!' AS Status;
SELECT CONCAT(COUNT(*), ' users created')  AS Info FROM users  UNION ALL
SELECT CONCAT(COUNT(*), ' books inserted') AS Info FROM books;

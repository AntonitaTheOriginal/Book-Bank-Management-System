# 📚 Book Bank Management System (BBMS)
**Batch 15 – Object-Oriented Software Engineering**

A full web-based university book bank management system built with Java (Servlets/JSP), MySQL, and Apache Tomcat.

---

## ✅ Prerequisites

| Tool | Version | Download |
|---|---|---|
| Java JDK | 11 or higher | https://adoptium.net |
| Apache Tomcat | 9.x | https://tomcat.apache.org |
| MySQL Server | 8.0 | https://dev.mysql.com |
| Maven | 3.6+ | https://maven.apache.org |
| Eclipse IDE (EE) | Latest | https://www.eclipse.org (optional) |

---

## 🚀 Setup Instructions (Step by Step)

### Step 1 – Set Up the Database

1. Open **MySQL Workbench** or your terminal
2. Run the SQL file:
   ```bash
   mysql -u root -p < bbms_database.sql
   ```
3. This will:
   - Create the `bbms_db` database
   - Create all 4 tables (users, books, transactions, fines)
   - Insert sample data (6 users, 14 books)

---

### Step 2 – Configure Database Connection

Open the file:
```
src/main/resources/database.properties
```

Update these lines with your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/bbms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD_HERE
```

---

### Step 3 – Build the Project

Open a terminal in the project root folder and run:
```bash
mvn clean package
```
This generates: `target/bbms.war`

---

### Step 4 – Deploy to Tomcat

1. Copy `target/bbms.war` to Tomcat's `webapps/` folder
2. Start Tomcat:
   - **Windows:** `bin\startup.bat`
   - **Linux/Mac:** `bin/startup.sh`
3. Open your browser and go to:
   ```
   http://localhost:8080/bbms
   ```

---

## 🔑 Default Login Credentials

| Role | University ID | Password |
|---|---|---|
| **Admin** | ADMIN001 | Admin@123 |
| **Faculty** | FAC2024001 | Faculty@123 |
| **Faculty** | FAC2024002 | Faculty@123 |
| **Student** | STU2024001 | Student@123 |
| **Student** | STU2024002 | Student@123 |
| **Student** | STU2024003 | Student@123 |

---

## 📁 Project Structure

```
bbms/
├── pom.xml                              ← Maven build config
├── bbms_database.sql                    ← Full DB schema + seed data
└── src/main/
    ├── java/com/bbms/
    │   ├── model/                       ← User, Book, Transaction, Fine
    │   ├── dao/                         ← UserDAO, BookDAO, TransactionDAO, FineDAO
    │   ├── service/                     ← AuthService, BookService, EmailService
    │   ├── servlet/                     ← All servlets (Login, Book, Transaction, etc.)
    │   └── util/                        ← DBConnection, AppConfig, AuthFilter
    ├── resources/
    │   └── database.properties          ← DB + SMTP config
    └── webapp/
        ├── css/style.css               ← Main stylesheet
        ├── index.jsp                   ← Redirects to login
        ├── WEB-INF/web.xml            ← Servlet configuration
        └── views/
            ├── common/                 ← login.jsp, navbar.jsp, books.jsp, transactions.jsp
            ├── admin/                  ← dashboard, addBook, editBook, issueBook, returnBook, fineList, reports
            └── student/               ← dashboard, myFines
```

---

## 🌐 URL Routes

| URL | Description | Access |
|---|---|---|
| `/bbms/login` | Login page | Public |
| `/bbms/dashboard` | Role-based dashboard | All |
| `/bbms/books` | Browse book inventory | All |
| `/bbms/books?action=add` | Add new book | Admin |
| `/bbms/transactions` | View transactions | All |
| `/bbms/transactions?action=issue` | Issue a book | Admin |
| `/bbms/transactions?action=return` | Return a book | Admin |
| `/bbms/fines` | Manage fines | All |
| `/bbms/users` | User management | Admin |
| `/bbms/reports` | Reports & analytics | Admin |
| `/bbms/logout` | Logout | All |

---

## ⚙️ Business Rules (from SRS)

- Loan period: **120 days** for both students and faculty
- Max books: **5 for students**, **10 for faculty**
- Fine: **₹1 per day overdue**, max **₹50 per book**
- Grace period: **2 days** (no fine assessed)
- Users with pending fines > **₹50** cannot borrow books
- Books with active issues **cannot be deleted**

---

## 📧 Email Notifications (Optional)

To enable email alerts, update `database.properties`:
```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.username=your_gmail@gmail.com
mail.password=your_gmail_app_password
mail.from=noreply@bbms.university.edu
```

> **Note:** For Gmail, generate an App Password at myaccount.google.com → Security → App Passwords.
> Email failures do not affect core transactions.

---

## 🛠 Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 11 |
| Web Framework | Servlets + JSP (MVC pattern) |
| Server | Apache Tomcat 9.x |
| Database | MySQL 8.0 |
| Password Hashing | BCrypt (jbcrypt) |
| Email | JavaMail API |
| Build Tool | Maven |
| Frontend | HTML5, CSS3 (custom) |

---

## 🔧 Running in Eclipse IDE

1. **File → Import → Maven → Existing Maven Project** → select this folder
2. Right-click project → **Properties → Project Facets** → Enable "Dynamic Web Module" & "Java"
3. Right-click project → **Run As → Run on Server** → choose your Tomcat installation
4. Eclipse will deploy automatically

---

*Batch 15 – Book Bank Management System | OOSE Lab Project*

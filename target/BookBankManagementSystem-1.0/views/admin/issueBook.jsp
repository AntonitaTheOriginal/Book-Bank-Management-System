<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Issue Book – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">📤 Issue Book</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <%-- ── Issue Form ─────────────────────────────────────────────────────── --%>
    <div class="card" style="max-width:650px;">
        <div class="card-title">Issue Book to User</div>

        <%-- Step 1: Look up user's transactions by typing their ID and pressing Lookup --%>
        <form method="get" action="${pageContext.request.contextPath}/transactions" style="margin-bottom:16px;">
            <input type="hidden" name="action" value="issue">
            <div class="form-group">
                <label class="form-label" for="userIdLookup">University ID *</label>
                <div style="display:flex; gap:8px;">
                    <input type="text" id="userIdLookup" name="userId" class="form-control"
                           placeholder="e.g. STU2024001" value="${filterUserId}" required>
                    <button type="submit" class="btn btn-outline">🔍 Lookup</button>
                </div>
                <span class="text-muted">Enter the student or faculty university ID, then click Lookup to see their current loans.</span>
            </div>
        </form>

        <%-- Step 2: Issue the book (only shown after a user ID is typed) --%>
        <c:if test="${not empty filterUserId}">
            <form method="post" action="${pageContext.request.contextPath}/transactions">
                <input type="hidden" name="action" value="issue">
                <input type="hidden" name="userId" value="${filterUserId}">

                <div class="form-group">
                    <label class="form-label" for="bookId">Select Book *</label>
                    <c:choose>
                        <c:when test="${empty books}">
                            <p class="text-muted" style="padding:8px 0;">⚠ No books are currently available for borrowing.</p>
                        </c:when>
                        <c:otherwise>
                            <select id="bookId" name="bookId" class="form-control" required>
                                <option value="">-- Choose an Available Book --</option>
                                <c:forEach var="book" items="${books}">
                                    <option value="${book.bookId}">
                                        ${book.title} by ${book.author}
                                        (ISBN: ${book.isbn})
                                        [${book.availableCopies}/${book.totalCopies} available]
                                    </option>
                                </c:forEach>
                            </select>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${not empty books}">
                    <div style="display:flex; gap:10px; margin-top:8px;">
                        <button type="submit" class="btn btn-primary">Issue Book</button>
                        <a href="${pageContext.request.contextPath}/transactions" class="btn btn-outline">Cancel</a>
                    </div>
                </c:if>
            </form>
        </c:if>
    </div>

    <%-- ── Current Loans for this User ───────────────────────────────────── --%>
    <c:if test="${not empty filterUserId}">
        <div class="card" style="max-width:900px; margin-top:20px;">
            <div class="card-title">📋 Current Loans for: <strong>${filterUserId}</strong></div>
            <c:choose>
                <c:when test="${empty userTransactions}">
                    <p class="text-muted">No transaction history found for this user.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>Txn ID</th>
                                    <th>Book</th>
                                    <th>Issue Date</th>
                                    <th>Due Date</th>
                                    <th>Return Date</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="t" items="${userTransactions}">
                                <tr>
                                    <td>#${t.transactionId}</td>
                                    <td><strong>${t.bookTitle}</strong><br>
                                        <span class="text-muted"><code>${t.bookIsbn}</code></span></td>
                                    <td>${t.issueDate}</td>
                                    <td>${t.dueDate}</td>
                                    <td>${t.returnDate != null ? t.returnDate : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.status == 'RETURNED'}">
                                                <span class="badge badge-success">RETURNED</span>
                                            </c:when>
                                            <c:when test="${t.overdueDays > 0}">
                                                <span class="badge badge-danger">OVERDUE (${t.overdueDays}d)</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">ISSUED</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

    <%-- ── Issue Rules ────────────────────────────────────────────────────── --%>
    <div class="card" style="max-width:650px; margin-top:20px;">
        <div class="card-title">Issue Rules</div>
        <ul style="padding-left:20px; line-height:2;">
            <li>Students may borrow up to <strong>5 books</strong> simultaneously</li>
            <li>Faculty may borrow up to <strong>10 books</strong> simultaneously</li>
            <li>Loan period: <strong>120 days</strong> for all users</li>
            <li>Users with unpaid fines exceeding <strong>₹50</strong> cannot borrow</li>
            <li>A user cannot borrow the same book more than once at a time</li>
        </ul>
    </div>
</div>
</body>
</html>

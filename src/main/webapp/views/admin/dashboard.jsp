<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>

<div class="container">
    <h1 class="page-title">Admin Dashboard</h1>

    <!-- Stats -->
    <div class="stat-grid">
        <div class="stat-card blue">
            <div class="stat-number">${totalBooks}</div>
            <div class="stat-label">Total Books</div>
        </div>
        <div class="stat-card green">
            <div class="stat-number">${totalUsers}</div>
            <div class="stat-label">Active Users</div>
        </div>
        <div class="stat-card orange">
            <div class="stat-number">${totalTransactions}</div>
            <div class="stat-label">Total Transactions</div>
        </div>
        <div class="stat-card red">
            <div class="stat-number">${overdueList.size()}</div>
            <div class="stat-label">Overdue Books</div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="card">
        <div class="card-title">Quick Actions</div>
        <div style="display:flex; gap:10px; flex-wrap:wrap;">
            <a href="${pageContext.request.contextPath}/transactions?action=issue" class="btn btn-primary">📤 Issue Book</a>
            <a href="${pageContext.request.contextPath}/transactions?action=return" class="btn btn-success">📥 Return Book</a>
            <a href="${pageContext.request.contextPath}/books?action=add" class="btn btn-outline">➕ Add Book</a>
            <a href="${pageContext.request.contextPath}/users?action=add" class="btn btn-outline">👤 Register User</a>
            <a href="${pageContext.request.contextPath}/reports" class="btn btn-outline">📊 Reports</a>
        </div>
    </div>

    <!-- Overdue Table -->
    <div class="card">
        <div class="card-title">⚠️ Overdue Books (${overdueList.size()})</div>
        <c:choose>
            <c:when test="${empty overdueList}">
                <p class="text-muted">No overdue books at this time.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Txn ID</th><th>User</th><th>Book</th>
                                <th>Due Date</th><th>Days Overdue</th><th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="txn" items="${overdueList}">
                            <tr>
                                <td>#${txn.transactionId}</td>
                                <td>${txn.userId} – ${txn.userName}</td>
                                <td>${txn.bookTitle}</td>
                                <td><span class="badge badge-danger">${txn.dueDate}</span></td>
                                <td>${txn.overdueDays} days</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/transactions?action=return"
                                       class="btn btn-warning btn-sm">Process Return</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Pending Fines -->
    <div class="card">
        <div class="card-title">💰 Pending Fines (${pendingFines.size()})</div>
        <c:choose>
            <c:when test="${empty pendingFines}">
                <p class="text-muted">No pending fines.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>Fine ID</th><th>User</th><th>Book</th><th>Amount</th><th>Date</th><th>Action</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="fine" items="${pendingFines}">
                            <tr>
                                <td>#${fine.fineId}</td>
                                <td>${fine.userId} – ${fine.userName}</td>
                                <td>${fine.bookTitle}</td>
                                <td><strong>₹<fmt:formatNumber value="${fine.amount}" pattern="#,##0.00"/></strong></td>
                                <td>${fine.issuedDate}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/fines?action=pay&id=${fine.fineId}"
                                       class="btn btn-success btn-sm">Mark Paid</a>
                                    <a href="${pageContext.request.contextPath}/fines?action=waive&id=${fine.fineId}"
                                       class="btn btn-sm btn-outline">Waive</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>

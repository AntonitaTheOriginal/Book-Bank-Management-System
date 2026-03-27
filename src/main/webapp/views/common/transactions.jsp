<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    com.bbms.model.User tUser = (com.bbms.model.User) session.getAttribute("user");
    boolean tIsAdmin = tUser != null && tUser.getUserType() == com.bbms.model.User.UserType.ADMIN;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Transactions – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <div class="d-flex justify-between align-center" style="margin-bottom:16px;">
        <h1 class="page-title mb-0">📋 Transactions</h1>
        <% if (tIsAdmin) { %>
        <div style="display:flex; gap:8px;">
            <a href="${pageContext.request.contextPath}/transactions?action=issue" class="btn btn-primary">📤 Issue Book</a>
            <a href="${pageContext.request.contextPath}/transactions?action=return" class="btn btn-success">📥 Return Book</a>
        </div>
        <% } %>
    </div>

    <div class="card">
        <div class="card-title">Transaction History (${transactions.size()})</div>
        <c:choose>
            <c:when test="${empty transactions}">
                <p class="text-muted">No transactions found.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Txn ID</th>
                                <% if (tIsAdmin) { %><th>User</th><% } %>
                                <th>Book</th><th>Issue Date</th><th>Due Date</th>
                                <th>Return Date</th><th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="t" items="${transactions}">
                            <tr>
                                <td>#${t.transactionId}</td>
                                <% if (tIsAdmin) { %>
                                <td>${t.userId}<br><span class="text-muted">${t.userName}</span></td>
                                <% } %>
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
</div>
</body>
</html>

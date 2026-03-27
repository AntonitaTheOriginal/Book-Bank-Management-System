<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Return Book – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">📥 Return Book</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card" style="max-width:600px;">
        <div class="card-title">Process Book Return</div>
        <form method="post" action="${pageContext.request.contextPath}/transactions">
            <input type="hidden" name="action" value="return">
            <div class="form-group">
                <label class="form-label" for="userId">University ID *</label>
                <input type="text" id="userId" name="userId" class="form-control"
                       placeholder="e.g. STU2024001" required>
            </div>
            <div class="form-group">
                <label class="form-label" for="bookId">Book ID *</label>
                <select id="bookId" name="bookId" class="form-control" required>
                    <option value="">-- Select Active Transaction --</option>
                    <c:forEach var="txn" items="${activeTransactions}">
                        <option value="${txn.bookId}">
                            [${txn.userId}] ${txn.bookTitle} – Due: ${txn.dueDate}
                            ${txn.overdueDays > 0 ? '⚠️ OVERDUE' : ''}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div style="display:flex; gap:10px; margin-top:8px;">
                <button type="submit" class="btn btn-success">Process Return</button>
                <a href="${pageContext.request.contextPath}/transactions" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>

    <div class="card">
        <div class="card-title">Currently Issued Books (${activeTransactions.size()})</div>
        <c:choose>
            <c:when test="${empty activeTransactions}">
                <p class="text-muted">No books currently issued.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>Txn ID</th><th>User</th><th>Book</th><th>Issue Date</th><th>Due Date</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="txn" items="${activeTransactions}">
                            <tr>
                                <td>#${txn.transactionId}</td>
                                <td>${txn.userId}</td>
                                <td>${txn.bookTitle}</td>
                                <td>${txn.issueDate}</td>
                                <td>${txn.dueDate}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${txn.overdueDays > 0}">
                                            <span class="badge badge-danger">Overdue (${txn.overdueDays}d)</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-success">On Time</span>
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

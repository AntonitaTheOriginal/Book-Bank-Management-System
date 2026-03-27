<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Reports – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .report-tabs { display:flex; gap:8px; margin-bottom:20px; flex-wrap:wrap; }
        .report-tabs a {
            padding:8px 18px; border-radius:4px; text-decoration:none;
            background:#e8eaf6; color:#283593; font-size:.88rem; font-weight:500;
        }
        .report-tabs a.active { background:#3f51b5; color:#fff; }
        @media print { .navbar, .report-tabs, .no-print { display:none; } }
    </style>
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <div class="d-flex justify-between align-center" style="margin-bottom:16px;">
        <h1 class="page-title mb-0">📊 ${reportTitle}</h1>
        <button onclick="window.print()" class="btn btn-outline no-print">🖨️ Print</button>
    </div>

    <div class="report-tabs no-print">
        <a href="?type=summary"      class="${reportType=='summary'?'active':''}">Summary</a>
        <a href="?type=transactions" class="${reportType=='transactions'?'active':''}">Transactions</a>
        <a href="?type=overdue"      class="${reportType=='overdue'?'active':''}">Overdue</a>
        <a href="?type=fines"        class="${reportType=='fines'?'active':''}">Fines</a>
        <a href="?type=inventory"    class="${reportType=='inventory'?'active':''}">Inventory</a>
    </div>

    <!-- Summary -->
    <c:if test="${reportType == 'summary'}">
        <div class="stat-grid">
            <div class="stat-card blue">  <div class="stat-number">${totalBooks}</div>        <div class="stat-label">Total Books</div></div>
            <div class="stat-card green"> <div class="stat-number">${totalUsers}</div>        <div class="stat-label">Active Users</div></div>
            <div class="stat-card orange"><div class="stat-number">${totalTransactions}</div> <div class="stat-label">Transactions</div></div>
            <div class="stat-card red">  <div class="stat-number">${overdueCount}</div>       <div class="stat-label">Overdue Books</div></div>
        </div>
    </c:if>

    <!-- Transactions / Overdue -->
    <c:if test="${reportType == 'transactions' || reportType == 'overdue'}">
        <div class="card">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr><th>Txn ID</th><th>User</th><th>Book</th><th>Issue Date</th><th>Due Date</th><th>Return Date</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="t" items="${transactions}">
                        <tr>
                            <td>#${t.transactionId}</td>
                            <td>${t.userId}<br><span class="text-muted">${t.userName}</span></td>
                            <td>${t.bookTitle}</td>
                            <td>${t.issueDate}</td>
                            <td>${t.dueDate}</td>
                            <td>${t.returnDate != null ? t.returnDate : '—'}</td>
                            <td>
                                <span class="badge ${t.status=='RETURNED'?'badge-success':t.status=='OVERDUE'?'badge-danger':'badge-warning'}">${t.status}</span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <!-- Fines -->
    <c:if test="${reportType == 'fines'}">
        <div class="card">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr><th>Fine ID</th><th>User</th><th>Book</th><th>Amount</th><th>Issued</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="f" items="${fines}">
                        <tr>
                            <td>#${f.fineId}</td>
                            <td>${f.userId} – ${f.userName}</td>
                            <td>${f.bookTitle}</td>
                            <td>₹<fmt:formatNumber value="${f.amount}" pattern="#,##0.00"/></td>
                            <td>${f.issuedDate}</td>
                            <td><span class="badge ${f.status=='PAID'?'badge-success':f.status=='WAIVED'?'badge-secondary':'badge-danger'}">${f.status}</span></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>

    <!-- Inventory -->
    <c:if test="${reportType == 'inventory'}">
        <div class="card">
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr><th>ISBN</th><th>Title</th><th>Author</th><th>Dept</th><th>Total</th><th>Available</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="b" items="${books}">
                        <tr>
                            <td><code>${b.isbn}</code></td>
                            <td>${b.title}</td>
                            <td>${b.author}</td>
                            <td>${b.department}</td>
                            <td>${b.totalCopies}</td>
                            <td>
                                <span class="badge ${b.availableCopies > 0 ? 'badge-success' : 'badge-danger'}">${b.availableCopies}</span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:if>
</div>
</body>
</html>

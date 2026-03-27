<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Dashboard – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<%
    com.bbms.model.User dashUser = (com.bbms.model.User) session.getAttribute("user");
%>
<div class="container">
    <h1 class="page-title">Welcome, <%=dashUser.getFullName()%> 👋</h1>

    <!-- Quick Stats -->
    <div class="stat-grid">
        <div class="stat-card blue">
            <div class="stat-number">${myTransactions.size()}</div>
            <div class="stat-label">Total Borrowed</div>
        </div>
        <div class="stat-card orange">
            <div class="stat-number">
                <c:set var="activeCount" value="0"/>
                <c:forEach var="t" items="${myTransactions}">
                    <c:if test="${t.status == 'ISSUED'}">
                        <c:set var="activeCount" value="${activeCount + 1}"/>
                    </c:if>
                </c:forEach>
                ${activeCount}
            </div>
            <div class="stat-label">Currently Borrowed</div>
        </div>
        <div class="stat-card red">
            <div class="stat-number">${myFines.size()}</div>
            <div class="stat-label">Fines Issued</div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="card">
        <div class="card-title">Quick Actions</div>
        <div style="display:flex; gap:10px; flex-wrap:wrap;">
            <a href="${pageContext.request.contextPath}/books" class="btn btn-primary">🔍 Search Books</a>
            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-outline">📋 My Transactions</a>
            <a href="${pageContext.request.contextPath}/fines" class="btn btn-outline">💰 My Fines</a>
        </div>
    </div>

    <!-- Currently Borrowed Books -->
    <div class="card">
        <div class="card-title">📚 Currently Borrowed Books</div>
        <c:set var="hasBorrowed" value="false"/>
        <c:forEach var="t" items="${myTransactions}">
            <c:if test="${t.status == 'ISSUED'}">
                <c:set var="hasBorrowed" value="true"/>
            </c:if>
        </c:forEach>

        <c:choose>
            <c:when test="${!hasBorrowed}">
                <p class="text-muted">You have no books currently borrowed.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>Book Title</th><th>ISBN</th><th>Issue Date</th><th>Due Date</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="t" items="${myTransactions}">
                            <c:if test="${t.status == 'ISSUED'}">
                                <tr>
                                    <td><strong>${t.bookTitle}</strong></td>
                                    <td><code>${t.bookIsbn}</code></td>
                                    <td>${t.issueDate}</td>
                                    <td>${t.dueDate}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.overdueDays > 0}">
                                                <span class="badge badge-danger">Overdue by ${t.overdueDays} days</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-success">On Time</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Pending Fines -->
    <div class="card">
        <div class="card-title">💰 Outstanding Fines</div>
        <c:set var="hasPending" value="false"/>
        <c:forEach var="f" items="${myFines}">
            <c:if test="${f.status == 'PENDING'}"><c:set var="hasPending" value="true"/></c:if>
        </c:forEach>
        <c:choose>
            <c:when test="${!hasPending}">
                <p class="text-muted">✓ You have no outstanding fines.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>Fine ID</th><th>Book</th><th>Amount</th><th>Date Issued</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="f" items="${myFines}">
                            <c:if test="${f.status == 'PENDING'}">
                                <tr>
                                    <td>#${f.fineId}</td>
                                    <td>${f.bookTitle}</td>
                                    <td><strong>₹<fmt:formatNumber value="${f.amount}" pattern="#,##0.00"/></strong></td>
                                    <td>${f.issuedDate}</td>
                                    <td><span class="badge badge-danger">PENDING</span></td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
                <p class="text-muted mt-2">Please visit the book bank office to clear your fines.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>

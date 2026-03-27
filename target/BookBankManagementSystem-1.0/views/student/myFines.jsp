<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Fines – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">💰 My Fines</h1>

    <c:if test="${totalPending > 0}">
        <div class="alert alert-warning">
            ⚠️ You have <strong>₹<fmt:formatNumber value="${totalPending}" pattern="#,##0.00"/></strong> in pending fines.
            Please visit the book bank office to clear your dues.
        </div>
    </c:if>

    <div class="card">
        <div class="card-title">Fine History</div>
        <c:choose>
            <c:when test="${empty fines}">
                <p class="text-muted">✓ You have no fines recorded.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>Fine ID</th><th>Book</th><th>Amount</th><th>Issued Date</th><th>Paid Date</th><th>Status</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="f" items="${fines}">
                            <tr>
                                <td>#${f.fineId}</td>
                                <td>${f.bookTitle}</td>
                                <td><strong>₹<fmt:formatNumber value="${f.amount}" pattern="#,##0.00"/></strong></td>
                                <td>${f.issuedDate}</td>
                                <td>${f.paidDate != null ? f.paidDate : '—'}</td>
                                <td>
                                    <span class="badge ${f.status=='PAID'?'badge-success':f.status=='WAIVED'?'badge-secondary':'badge-danger'}">
                                        ${f.status}
                                    </span>
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

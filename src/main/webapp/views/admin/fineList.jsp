<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Fines – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">💰 Fine Management</h1>

    <div class="card">
        <div class="card-title">All Fines (${fines.size()})</div>
        <c:choose>
            <c:when test="${empty fines}">
                <p class="text-muted">No fines recorded.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>Fine ID</th><th>User</th><th>Book</th><th>Amount</th><th>Issued</th><th>Paid</th><th>Status</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="fine" items="${fines}">
                            <tr>
                                <td>#${fine.fineId}</td>
                                <td>${fine.userId}<br><span class="text-muted">${fine.userName}</span></td>
                                <td>${fine.bookTitle}</td>
                                <td><strong>₹<fmt:formatNumber value="${fine.amount}" pattern="#,##0.00"/></strong></td>
                                <td>${fine.issuedDate}</td>
                                <td>${fine.paidDate != null ? fine.paidDate : '-'}</td>
                                <td>
                                    <span class="badge ${fine.status == 'PAID' ? 'badge-success' : fine.status == 'WAIVED' ? 'badge-secondary' : 'badge-danger'}">
                                        ${fine.status}
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${fine.status == 'PENDING'}">
                                        <a href="${pageContext.request.contextPath}/fines?action=pay&id=${fine.fineId}"
                                           class="btn btn-success btn-sm">Mark Paid</a>
                                        <a href="${pageContext.request.contextPath}/fines?action=waive&id=${fine.fineId}"
                                           class="btn btn-sm btn-outline"
                                           onclick="return confirm('Waive this fine?')">Waive</a>
                                    </c:if>
                                    <c:if test="${fine.status != 'PENDING'}">
                                        <span class="text-muted">—</span>
                                    </c:if>
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

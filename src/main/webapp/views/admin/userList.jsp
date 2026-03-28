<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Users – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <div class="d-flex justify-between align-center" style="margin-bottom:16px;">
        <h1 class="page-title mb-0">👥 User Management</h1>
        <a href="${pageContext.request.contextPath}/users?action=add" class="btn btn-primary">➕ Register User</a>
    </div>

    <div class="card">
        <div class="card-title">Registered Users (${users.size()})</div>
        <c:choose>
            <c:when test="${empty users}">
                <p class="text-muted">No users registered yet.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr><th>ID</th><th>Name</th><th>Email</th><th>Dept</th><th>Type</th><th>Status</th><th>Registered</th><th>Actions</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td><code>${u.userId}</code></td>
                                <td><strong>${u.fullName}</strong></td>
                                <td>${u.email}</td>
                                <td>${u.department}</td>
                                <td>
                                    <span class="badge ${u.userType == 'ADMIN' ? 'badge-info' : u.userType == 'FACULTY' ? 'badge-warning' : 'badge-secondary'}">
                                        ${u.userType}
                                    </span>
                                </td>
                                <td>
                                    <span class="badge ${u.status == 'ACTIVE' ? 'badge-success' : 'badge-danger'}">${u.status}</span>
                                </td>
                                <td>${u.registrationDate}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/users?action=edit&id=${u.userId}" class="btn btn-warning btn-sm">Edit</a>
                                    <c:choose>
                                        <c:when test="${u.userId == 'ADMIN001'}">
                                            <span class="badge badge-secondary" title="Primary admin cannot be deleted">🔒 Protected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/users?action=delete&id=${u.userId}" class="btn btn-danger btn-sm"
                                               onclick="return confirm('Remove this user?')">Delete</a>
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

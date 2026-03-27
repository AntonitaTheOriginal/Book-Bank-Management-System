<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register User – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">👤 Register New User</h1>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <div class="card" style="max-width:650px;">
        <div class="card-title">User Details</div>
        <form method="post" action="${pageContext.request.contextPath}/users">
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label class="form-label">University ID *</label>
                <input type="text" name="userId" class="form-control" required
                       placeholder="e.g. STU2024001 or FAC2024001"
                       value="${formUser.userId}">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">First Name *</label>
                    <input type="text" name="firstName" class="form-control" required
                           placeholder="First name" value="${formUser.firstName}">
                </div>
                <div class="form-group">
                    <label class="form-label">Last Name *</label>
                    <input type="text" name="lastName" class="form-control" required
                           placeholder="Last name" value="${formUser.lastName}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Email *</label>
                    <input type="email" name="email" class="form-control" required
                           placeholder="university@email.com" value="${formUser.email}">
                </div>
                <div class="form-group">
                    <label class="form-label">Phone</label>
                    <input type="text" name="phone" class="form-control"
                           placeholder="Phone number" value="${formUser.phone}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Department *</label>
                    <input type="text" name="department" class="form-control" required
                           placeholder="e.g. Computer Science" value="${formUser.department}">
                </div>
                <div class="form-group">
                    <label class="form-label">User Type *</label>
                    <select name="userType" class="form-control" required>
                        <option value="STUDENT">Student</option>
                        <option value="FACULTY">Faculty</option>
                        <option value="ADMIN">Admin</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Status</label>
                    <select name="status" class="form-control">
                        <option value="ACTIVE">Active</option>
                        <option value="INACTIVE">Inactive</option>
                        <option value="SUSPENDED">Suspended</option>
                    </select>
                </div>
                <div class="form-group">
                    <label class="form-label">Password *</label>
                    <input type="password" name="password" class="form-control" required
                           placeholder="Set initial password">
                </div>
            </div>

            <div style="display:flex; gap:10px; margin-top:8px;">
                <button type="submit" class="btn btn-primary">Register User</button>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>

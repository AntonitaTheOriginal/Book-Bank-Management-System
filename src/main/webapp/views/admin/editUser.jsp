<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">✏️ Edit User</h1>

    <div class="card" style="max-width:650px;">
        <div class="card-title">Edit User Details</div>
        <form method="post" action="${pageContext.request.contextPath}/users">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="userId" value="${editUser.userId}">

            <div class="form-group">
                <label class="form-label">University ID</label>
                <input type="text" class="form-control" value="${editUser.userId}" disabled>
                <span class="text-muted">University ID cannot be changed</span>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">First Name *</label>
                    <input type="text" name="firstName" class="form-control" required value="${editUser.firstName}">
                </div>
                <div class="form-group">
                    <label class="form-label">Last Name *</label>
                    <input type="text" name="lastName" class="form-control" required value="${editUser.lastName}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Email *</label>
                    <input type="email" name="email" class="form-control" required value="${editUser.email}">
                </div>
                <div class="form-group">
                    <label class="form-label">Phone</label>
                    <input type="text" name="phone" class="form-control" value="${editUser.phone}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Department *</label>
                    <input type="text" name="department" class="form-control" required value="${editUser.department}">
                </div>
                <div class="form-group">
                    <label class="form-label">User Type *</label>
                    <select name="userType" class="form-control" required>
                        <option value="STUDENT"  ${editUser.userType == 'STUDENT'  ? 'selected' : ''}>Student</option>
                        <option value="FACULTY"  ${editUser.userType == 'FACULTY'  ? 'selected' : ''}>Faculty</option>
                        <option value="ADMIN"    ${editUser.userType == 'ADMIN'    ? 'selected' : ''}>Admin</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Status</label>
                <select name="status" class="form-control">
                    <option value="ACTIVE"    ${editUser.status == 'ACTIVE'    ? 'selected' : ''}>Active</option>
                    <option value="INACTIVE"  ${editUser.status == 'INACTIVE'  ? 'selected' : ''}>Inactive</option>
                    <option value="SUSPENDED" ${editUser.status == 'SUSPENDED' ? 'selected' : ''}>Suspended</option>
                </select>
            </div>

            <div style="display:flex; gap:10px; margin-top:8px;">
                <button type="submit" class="btn btn-warning">Update User</button>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>

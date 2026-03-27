<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login – Book Bank Management System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="login-page">
    <div class="login-card">
        <div class="login-logo">
            <div class="login-icon">📚</div>
            <h1>Book Bank</h1>
            <p>Management System – University Portal</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">${error}</div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label class="form-label" for="userId">University ID</label>
                <input type="text" id="userId" name="userId" class="form-control"
                       placeholder="e.g. STU2024001" required autofocus
                       value="${param.userId}">
            </div>
            <div class="form-group">
                <label class="form-label" for="password">Password</label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="Enter your password" required>
            </div>
            <button type="submit" class="btn btn-primary w-100" style="margin-top:8px; padding:12px;">
                Sign In
            </button>
        </form>

        <p class="text-muted" style="text-align:center; margin-top:20px; font-size:.8rem;">
            Batch 15 – Object-Oriented Software Engineering
        </p>
    </div>
</div>
</body>
</html>

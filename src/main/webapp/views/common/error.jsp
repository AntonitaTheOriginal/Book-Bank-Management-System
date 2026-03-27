<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div style="display:flex; align-items:center; justify-content:center; min-height:100vh; flex-direction:column; gap:16px; background:#f0f2f5;">
    <div style="font-size:4rem;">⚠️</div>
    <h1 style="color:#1a237e;">Something went wrong</h1>
    <p style="color:#666;">An error occurred while processing your request.</p>
    <% if (exception != null) { %>
    <div class="alert alert-danger" style="max-width:600px;"><%=exception.getMessage()%></div>
    <% } %>
    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">← Back to Dashboard</a>
</div>
</body>
</html>

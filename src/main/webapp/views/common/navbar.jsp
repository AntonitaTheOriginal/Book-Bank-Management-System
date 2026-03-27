<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    com.bbms.model.User navUser = (com.bbms.model.User) session.getAttribute("user");
    boolean isAdmin = navUser != null && navUser.getUserType() == com.bbms.model.User.UserType.ADMIN;
    String cp = request.getContextPath();
    String uri = request.getRequestURI();
%>
<nav class="navbar">
    <div class="navbar-brand">📚 <span>Book Bank</span> Management System</div>

    <div class="navbar-nav">
        <a href="<%=cp%>/dashboard"     class='<%=uri.contains("dashboard") ? "active" : ""%>'>Dashboard</a>
        <a href="<%=cp%>/books"         class='<%=uri.contains("/books")    ? "active" : ""%>'>Books</a>
        <a href="<%=cp%>/transactions"  class='<%=uri.contains("transaction")? "active" : ""%>'>Transactions</a>
        <a href="<%=cp%>/fines"         class='<%=uri.contains("fine")      ? "active" : ""%>'>Fines</a>

        <% if (isAdmin) { %>
        <a href="<%=cp%>/users"   class='<%=uri.contains("/users")  ? "active" : ""%>'>Users</a>
        <a href="<%=cp%>/reports" class='<%=uri.contains("report")  ? "active" : ""%>'>Reports</a>
        <% } %>
    </div>

    <div class="navbar-nav">
        <% if (navUser != null) { %>
        <span class="navbar-user">👤 <%=navUser.getFullName()%> (<%=navUser.getUserType()%>)</span>
        <% } %>
        <a href="<%=cp%>/logout">Logout</a>
    </div>
</nav>

<%-- Flash message display --%>
<%
    String flash = (String) session.getAttribute("flash");
    if (flash != null) {
        session.removeAttribute("flash");
%>
<div style="padding:0 20px; margin-top:12px;">
    <div class="alert alert-success">✓ <%=flash%></div>
</div>
<% } %>

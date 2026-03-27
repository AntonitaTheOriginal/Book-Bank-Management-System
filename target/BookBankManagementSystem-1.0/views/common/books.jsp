<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    com.bbms.model.User bUser = (com.bbms.model.User) session.getAttribute("user");
    boolean bIsAdmin = bUser != null && bUser.getUserType() == com.bbms.model.User.UserType.ADMIN;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Books – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <div class="d-flex justify-between align-center" style="margin-bottom:16px;">
        <h1 class="page-title mb-0">📚 Book Inventory</h1>
        <% if (bIsAdmin) { %>
        <a href="${pageContext.request.contextPath}/books?action=add" class="btn btn-primary">➕ Add Book</a>
        <% } %>
    </div>

    <!-- Search Bar -->
    <form method="get" action="${pageContext.request.contextPath}/books" class="search-bar">
        <input type="hidden" name="action" value="search">
        <div class="form-group">
            <label class="form-label">Search</label>
            <input type="text" name="keyword" class="form-control" placeholder="Title / Author / ISBN"
                   value="${param.keyword}" style="width:250px;">
        </div>
        <div class="form-group">
            <label class="form-label">Department</label>
            <select name="department" class="form-control">
                <option value="">All Departments</option>
                <c:forEach var="dept" items="${departments}">
                    <option value="${dept}" ${param.department == dept ? 'selected' : ''}>${dept}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-group">
            <label class="form-label">Year</label>
            <input type="number" name="year" class="form-control" placeholder="Year"
                   value="${param.year}" style="width:100px;">
        </div>
        <div class="form-group">
            <label class="form-label">Available Only</label>
            <select name="available" class="form-control">
                <option value="">All</option>
                <option value="true" ${param.available == 'true' ? 'selected' : ''}>Available</option>
            </select>
        </div>
        <button type="submit" class="btn btn-primary">Search</button>
        <a href="${pageContext.request.contextPath}/books" class="btn btn-outline">Clear</a>
    </form>

    <!-- Books Table -->
    <div class="card">
        <div class="card-title">Books (${books.size()} results)</div>
        <c:choose>
            <c:when test="${empty books}">
                <p class="text-muted">No books found.</p>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>ISBN</th><th>Title</th><th>Author</th>
                                <th>Dept</th><th>Year</th><th>Available</th>
                                <% if (bIsAdmin) { %><th>Actions</th><% } %>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="book" items="${books}">
                            <tr>
                                <td><code>${book.isbn}</code></td>
                                <td><strong>${book.title}</strong><br>
                                    <span class="text-muted">${book.edition}</span></td>
                                <td>${book.author}<br>
                                    <span class="text-muted">${book.publisher}</span></td>
                                <td>${book.department}</td>
                                <td>${book.publicationYear}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${book.availableCopies > 0}">
                                            <span class="badge badge-success">${book.availableCopies}/${book.totalCopies}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">Checked Out</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <% if (bIsAdmin) { %>
                                <td>
                                    <a href="${pageContext.request.contextPath}/books?action=edit&id=${book.bookId}"
                                       class="btn btn-warning btn-sm">Edit</a>
                                    <a href="${pageContext.request.contextPath}/books?action=delete&id=${book.bookId}"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Delete this book?')">Delete</a>
                                </td>
                                <% } %>
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

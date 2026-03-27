<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Book – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">➕ Add New Book</h1>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <div class="card" style="max-width:700px;">
        <div class="card-title">Book Details</div>
        <form method="post" action="${pageContext.request.contextPath}/books">
            <input type="hidden" name="action" value="add">

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">ISBN-13 *</label>
                    <input type="text" name="isbn" class="form-control" required
                           placeholder="978-x-xxx-xxxxx-x" value="${book.isbn}">
                </div>
                <div class="form-group">
                    <label class="form-label">Publication Year *</label>
                    <input type="number" name="publicationYear" class="form-control" required
                           placeholder="e.g. 2022" min="1900" max="2099" value="${book.publicationYear}">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Title *</label>
                <input type="text" name="title" class="form-control" required
                       placeholder="Full book title" value="${book.title}">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Author *</label>
                    <input type="text" name="author" class="form-control" required
                           placeholder="Author name(s)" value="${book.author}">
                </div>
                <div class="form-group">
                    <label class="form-label">Publisher</label>
                    <input type="text" name="publisher" class="form-control"
                           placeholder="Publisher name" value="${book.publisher}">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Edition</label>
                    <input type="text" name="edition" class="form-control"
                           placeholder="e.g. 3rd Edition" value="${book.edition}">
                </div>
                <div class="form-group">
                    <label class="form-label">Total Copies *</label>
                    <input type="number" name="totalCopies" class="form-control" required
                           placeholder="Number of copies" min="1" value="${book.totalCopies}">
                </div>
            </div>

            <div class="form-group">
                <label class="form-label">Department *</label>
                <input type="text" name="department" class="form-control" required
                       list="deptList" placeholder="e.g. Computer Science" value="${book.department}">
                <datalist id="deptList">
                    <c:forEach var="d" items="${departments}"><option value="${d}"></c:forEach>
                </datalist>
            </div>

            <div style="display:flex; gap:10px; margin-top:8px;">
                <button type="submit" class="btn btn-primary">Add Book</button>
                <a href="${pageContext.request.contextPath}/books" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>

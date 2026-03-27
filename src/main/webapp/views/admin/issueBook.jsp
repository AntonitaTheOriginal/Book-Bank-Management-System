<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Issue Book – BBMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/views/common/navbar.jsp" %>
<div class="container">
    <h1 class="page-title">📤 Issue Book</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="card" style="max-width:600px;">
        <div class="card-title">Issue Book to User</div>
        <form method="post" action="${pageContext.request.contextPath}/transactions">
            <input type="hidden" name="action" value="issue">

            <div class="form-group">
                <label class="form-label" for="userId">University ID *</label>
                <input type="text" id="userId" name="userId" class="form-control"
                       placeholder="e.g. STU2024001" required value="${param.userId}">
                <span class="text-muted">Enter the student or faculty university ID</span>
            </div>

            <div class="form-group">
                <label class="form-label" for="bookId">Select Book *</label>
                <select id="bookId" name="bookId" class="form-control" required>
                    <option value="">-- Choose a Book --</option>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.bookId}"
                            ${book.availableCopies == 0 ? 'disabled' : ''}>
                            ${book.title} by ${book.author}
                            (ISBN: ${book.isbn})
                            [${book.availableCopies}/${book.totalCopies} available]
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div style="display:flex; gap:10px; margin-top:8px;">
                <button type="submit" class="btn btn-primary">Issue Book</button>
                <a href="${pageContext.request.contextPath}/transactions" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>

    <div class="card" style="max-width:600px;">
        <div class="card-title">Issue Rules</div>
        <ul style="padding-left:20px; line-height:2;">
            <li>Students may borrow up to <strong>5 books</strong> simultaneously</li>
            <li>Faculty may borrow up to <strong>10 books</strong> simultaneously</li>
            <li>Loan period: <strong>120 days</strong> for all users</li>
            <li>Users with unpaid fines exceeding <strong>₹50</strong> cannot borrow</li>
        </ul>
    </div>
</div>
</body>
</html>

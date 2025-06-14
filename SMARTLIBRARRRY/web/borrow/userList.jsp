<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Borrow Records</title>
</head>
<body>
    <h2>User Borrow Records</h2>

    <!-- Show success or error messages -->
    <c:if test="${param.success != null}">
        <p style="color:green;">Operation successful!</p>
    </c:if>
    <c:if test="${param.deleted != null}">
        <p style="color:green;">Record deleted successfully!</p>
    </c:if>

    <h3>Users who have Returned Books</h3>
    <!-- Table to display users who returned books -->
    <table border="1">
        <thead>
            <tr>
                <th>User ID</th>
                <th>Book ID</th>
                <th>Loan Date</th>
                <th>Return Date</th>
                <th>Penalty</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="borrow" items="${returnedBorrowList}">
                <tr>
                    <td>${borrow.userId}</td>
                    <td>${borrow.bukuId}</td>
                    <td>${borrow.tanggalPinjam}</td>
                    <td>${borrow.tanggalPengembalian}</td>
                    <td>${borrow.denda}</td>
                    <td>
                        <a href="borrow?action=delete&id=${borrow.id}" onclick="return confirm('Are you sure?')">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h3>Users who have Not Returned Books</h3>
    <!-- Table to display users who have not returned books -->
    <table border="1">
        <thead>
            <tr>
                <th>User ID</th>
                <th>Book ID</th>
                <th>Loan Date</th>
                <th>Due Date</th>
                <th>Penalty</th>
                <th>Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="borrow" items="${unreturnedBorrowList}">
                <tr>
                    <td>${borrow.userId}</td>
                    <td>${borrow.bukuId}</td>
                    <td>${borrow.tanggalPinjam}</td>
                    <td>${borrow.tenggatKembali}</td>
                    <td>${borrow.denda}</td>
                    <td>
                        <a href="borrow?action=edit&id=${borrow.id}">Edit</a> |
                        <a href="borrow?action=delete&id=${borrow.id}" onclick="return confirm('Are you sure?')">Delete</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <br>
    <a href="index.jsp">Back to Home</a>
</body>
</html>

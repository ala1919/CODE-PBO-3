<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout</title>
</head>
<body>
    <h2>Checkout</h2>

    <p>User: ${cart.createdBy}</p>
    <h3>Books to be Checked Out</h3>

    <table border="1">
        <thead>
            <tr>
                <th>Book Title</th>
                <th>Author</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="book" items="${cart.books}">
                <tr>
                    <td>${book.judul}</td>
                    <td>${book.pengarang}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <form action="cart?action=finalize" method="POST">
        <input type="hidden" name="cartId" value="${cart.id}">
        <button type="submit">Finalize Checkout</button>
    </form>
</body>
</html>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Borrow</title>
</head>
<body>
    <h2>Edit Borrow Record</h2>

    <form action="borrow?action=update&id=${borrow.id}" method="POST">
        <label for="bukuId">Book ID:</label>
        <input type="number" id="bukuId" name="bukuId" value="${borrow.bukuId}" required><br><br>

        <label for="userId">User ID:</label>
        <input type="number" id="userId" name="userId" value="${borrow.userId}" required><br><br>

        <label for="tanggalPinjam">Loan Date:</label>
        <input type="date" id="tanggalPinjam" name="tanggalPinjam" value="${borrow.tanggalPinjam}" required><br><br>

        <label for="tenggatKembali">Due Date:</label>
        <input type="date" id="tenggatKembali" name="tenggatKembali" value="${borrow.tenggatKembali}" required><br><br>

        <label for="tanggalPengembalian">Return Date:</label>
        <input type="date" id="tanggalPengembalian" name="tanggalPengembalian" value="${borrow.tanggalPengembalian}"><br><br>

        <label for="denda">Penalty:</label>
        <input type="number" id="denda" name="denda" value="${borrow.denda}" required><br><br>

        <label for="returned">Returned:</label>
        <input type="checkbox" id="returned" name="returned" ${borrow.returned ? 'checked' : ''}><br><br>

        <button type="submit">Update Borrow Record</button>
    </form>

    <br>
    <a href="borrow?action=index">Back to Borrow List</a>
</body>
</html>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Book</title>
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Arial, sans-serif;
        }
        .form-container {
            background: #fff;
            max-width: 420px;
            margin: 40px auto;
            padding: 32px 28px 24px 28px;
            border-radius: 14px;
            box-shadow: 0 4px 24px rgba(0,0,0,0.08);
        }
        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #0d6efd;
        }
        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
        }
        input, select {
            width: 100%;
            padding: 8px 10px;
            margin-bottom: 18px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            font-size: 1rem;
        }
        button[type="submit"] {
            width: 100%;
            background: #0d6efd;
            color: #fff;
            border: none;
            padding: 10px 0;
            border-radius: 6px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.2s;
        }
        button[type="submit"]:hover {
            background: #084298;
        }
        a {
            display: block;
            text-align: center;
            margin-top: 18px;
            color: #0d6efd;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Edit Book</h2>
        <form action="buku?action=update&id=${book.id}" method="POST">
            <label for="judul">Title:</label>
            <input type="text" id="judul" name="judul" value="${book.judul}" required>

            <label for="pengarang">Author:</label>
            <input type="text" id="pengarang" name="pengarang" value="${book.pengarang}" required>

            <label for="penerbit">Publisher:</label>
            <input type="text" id="penerbit" name="penerbit" value="${book.penerbit}" required>

            <label for="maxPinjam">Max Loan Period:</label>
            <input type="number" id="maxPinjam" name="maxPinjam" value="${book.maxPinjam}" required>

            <label for="tahunTerbit">Year of Publication:</label>
            <input type="number" id="tahunTerbit" name="tahunTerbit" value="${book.tahunTerbit}" required>

            <label for="kategoriId">Category ID:</label>
            <input type="number" id="kategoriId" name="kategoriId" value="${book.kategoriId}" required>

            <button type="submit">Update Book</button>
        </form>
        <a href="buku?action=list">Back to Book List</a>
    </div>
</body>
</html>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Welcome</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container my-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white text-center">
                <h2>Kelompok 6 SMART LIBRARY</h2>
            </div>
            <div class="card-body text-center">
                <h3 class="mb-4">Daftar Nama</h3>
                <ul class="list-unstyled">
                    <li>nama</li>
                    <li>nama</li>
                    <li>nama</li>
                    <li>nama</li>
                </ul>
                <br>
                <!-- Button to Login and Register -->
                <a href="<%= request.getContextPath() %>/auth/login.jsp" class="btn btn-primary mx-2">
                    Login
                </a>
                <a href="<%= request.getContextPath() %>/auth/register.jsp" class="btn btn-success mx-2">
                    Register
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js"></script>
</body>
</html>

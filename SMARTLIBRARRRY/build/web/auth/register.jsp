<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - Perpustakaan</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 400px;
            margin: 50px auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .form-container {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            color: #555;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        button:hover {
            background-color: #0056b3;
        }
        .error {
            color: #dc3545;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }
        .login-link {
            text-align: center;
            margin-top: 20px;
        }
        .login-link a {
            color: #007bff;
            text-decoration: none;
        }
        .login-link a:hover {
            text-decoration: underline;
        }
        #adminCodeGroup {
            display: none;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Daftar Akun Baru</h2>
        
        <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error">
                <%= request.getAttribute("errorMessage") %>
            </div>
        <% } %>
        
        <form action="<%= request.getContextPath() %>/auth" method="post">
            <input type="hidden" name="action" value="register">
            
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" 
                       value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>" 
                       required>
            </div>
            
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" 
                       value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>" 
                       required>
            </div>
            
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <label for="role">Role:</label>
                <select id="role" name="role" required onchange="toggleAdminCode()">
                    <option value="">Pilih Role</option>
                    <option value="User" <%= "User".equals(request.getParameter("role")) ? "selected" : "" %>>User</option>
                    <option value="Admin" <%= "Admin".equals(request.getParameter("role")) ? "selected" : "" %>>Admin</option>
                </select>
            </div>
            
            <div class="form-group" id="adminCodeGroup">
                <label for="adminCode">Admin Code:</label>
                <input type="password" id="adminCode" name="adminCode" 
                       placeholder="Masukkan kode admin">
                <small style="color: #666;">Diperlukan untuk registrasi sebagai Admin</small>
            </div>
            
            <button type="submit">Daftar</button>
        </form>
        
        <div class="login-link">
            <p>Sudah punya akun? <a href="<%= request.getContextPath() %>/auth?action=login">Login di sini</a></p>
        </div>
    </div>
    
    <script>
        function toggleAdminCode() {
            var role = document.getElementById('role').value;
            var adminCodeGroup = document.getElementById('adminCodeGroup');
            var adminCodeInput = document.getElementById('adminCode');
            
            if (role === 'Admin') {
                adminCodeGroup.style.display = 'block';
                adminCodeInput.required = true;
            } else {
                adminCodeGroup.style.display = 'none';
                adminCodeInput.required = false;
                adminCodeInput.value = '';
            }
        }
        
        // Check on page load
        window.onload = function() {
            toggleAdminCode();
        };
    </script>
</body>
</html>
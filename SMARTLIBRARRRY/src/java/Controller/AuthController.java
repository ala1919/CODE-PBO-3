package Controller;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Base64;

@WebServlet(name = "AuthController", urlPatterns = {"/auth"})
public class AuthController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    
    // Static block to load MySQL driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            System.err.println("Please add mysql-connector-java-x.x.x.jar to your classpath");
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            handleRegistration(request, response);
        } else if ("login".equals(action)) {
            handleLogin(request, response);
        }
    }

    private void handleRegistration(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String adminCode = request.getParameter("adminCode");
        StringBuilder errorMessage = new StringBuilder();

        // Debug: Print received parameters
        System.out.println("=== DEBUG REGISTRATION ===");
        System.out.println("Email: " + email);
        System.out.println("Username: " + username);
        System.out.println("Password: " + (password != null ? "[PROVIDED]" : "[NULL]"));
        System.out.println("Role: " + role);
        System.out.println("Admin Code: " + adminCode);

        // Validate inputs
        if (email == null || email.trim().isEmpty()) {
            errorMessage.append("Email tidak boleh kosong!<br>");
        }
        if (username == null || username.trim().isEmpty()) {
            errorMessage.append("Username tidak boleh kosong!<br>");
        }
        if (password == null || password.trim().isEmpty()) {
            errorMessage.append("Password tidak boleh kosong!<br>");
        }
        if (role == null || role.trim().isEmpty()) {
            errorMessage.append("Role tidak boleh kosong!<br>");
        }

        // Check for existing email or username (only if inputs are valid)
        if (errorMessage.length() == 0) {
            if (isEmailExists(email)) {
                errorMessage.append("Email sudah digunakan!<br>");
            }
            if (isUsernameExists(username)) {
                errorMessage.append("Username sudah digunakan!<br>");
            }
        }

        // Validate admin code for ADMIN role
        if ("Admin".equalsIgnoreCase(role) && (adminCode == null || !adminCode.equals("SECRET123"))) {
            errorMessage.append("Admin Code salah!<br>");
        }

        if (errorMessage.length() > 0) {
            System.out.println("Validation errors: " + errorMessage.toString());
            request.setAttribute("errorMessage", errorMessage.toString());
            request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            // Hash password with PBKDF2
            System.out.println("Hashing password...");
            String hashedPassword = hashPassword(password);
            System.out.println("Password hashed successfully");

            // Save user to database
            System.out.println("Saving user to database...");
            saveUser(username, email, hashedPassword, role);
            System.out.println("User saved successfully!");

            // Set success message and redirect
            request.getSession().setAttribute("successMessage", "Registrasi berhasil! Silakan login.");
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Gagal menyimpan data: " + e.getMessage());
            request.getRequestDispatcher("/auth/register.jsp").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Check for success message from registration
        HttpSession session = request.getSession();
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }

        User user = validateLogin(username, password);
        if (user != null) {
            // Create session - PERBAIKAN: Tambahkan userId
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId()); // TAMBAHAN INI
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());

            System.out.println("Login successful - User ID set in session: " + user.getId());

            // Redirect to book index page
            response.sendRedirect(request.getContextPath() + "/buku?action=list");
        } else {
            request.setAttribute("errorMessage", "Username atau Password salah!");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }

    private User validateLogin(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("Attempting login for username: " + username);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established for login");
            
            // PERBAIKAN: Tambahkan id ke query
            String query = "SELECT id, username, password, role FROM users WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                System.out.println("User found, verifying password...");
                if (verifyPassword(password, storedHash)) {
                    System.out.println("Password verified successfully");
                    // PERBAIKAN: Tambahkan id ke constructor
                    return new User(rs.getInt("id"), username, rs.getString("role"));
                } else {
                    System.out.println("Password verification failed");
                }
            } else {
                System.out.println("User not found in database");
            }
        } catch (Exception e) {
            System.err.println("Login validation error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean isEmailExists(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT email FROM users WHERE email = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean isUsernameExists(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            String query = "SELECT username FROM users WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void saveUser(String username, String email, String hashedPassword, String role) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            // Test database connection first
            System.out.println("Testing database connection...");
            System.out.println("Connection URL: " + DB_URL);
            System.out.println("Database User: " + DB_USER);
            
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection successful");
            
            // Test if table exists
            if (!tableExists(conn, "users")) {
                throw new RuntimeException("Table 'users' does not exist in database 'perpus'");
            }
            
            String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role.toUpperCase());
            
            System.out.println("Executing insert query...");
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Role: " + role.toUpperCase());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected == 0) {
                throw new RuntimeException("No rows were inserted");
            }
            
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean tableExists(Connection conn, String tableName) {
        try {
            String query = "SHOW TABLES LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            rs.close();
            stmt.close();
            System.out.println("Table '" + tableName + "' exists: " + exists);
            return exists;
        } catch (Exception e) {
            System.err.println("Error checking table existence: " + e.getMessage());
            return false;
        }
    }

    // Hash password using PBKDF2
    private String hashPassword(String password) {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Create PBKDF2 hash
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Combine salt and hash, encode as base64
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            System.err.println("Password hashing error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    // Verify password against stored hash
    private boolean verifyPassword(String inputPassword, String storedHash) {
        try {
            // Decode stored hash
            byte[] combined = Base64.getDecoder().decode(storedHash);

            // Extract salt and hash
            byte[] salt = new byte[SALT_LENGTH];
            byte[] hash = new byte[combined.length - salt.length];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            System.arraycopy(combined, salt.length, hash, 0, hash.length);

            // Compute hash of input password
            PBEKeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), salt, ITERATIONS, hash.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();

            // Compare hashes
            if (hash.length != testHash.length) return false;
            for (int i = 0; i < hash.length; i++) {
                if (hash[i] != testHash[i]) return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Password verification error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // PERBAIKAN: Inner class User dengan field id
    private static class User {
        private int id;
        private String username;
        private String role;

        public User(int id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }
    }
}
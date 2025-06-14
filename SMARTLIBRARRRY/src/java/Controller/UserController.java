package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Models.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "UserController", urlPatterns = {"/user"})
public class UserController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // View profile
        if ("viewProfile".equals(action)) {
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("user");
            if (username != null) {
                User user = getUserByUsername(username);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/profile.jsp").forward(request, response);
            } else {
                response.sendRedirect("login.jsp");
            }
        }
        // Redirect to index if action is invalid
        else {
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Update profile
        if ("updateProfile".equals(action)) {
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("user");
            if (username != null) {
                String email = request.getParameter("email");
                String password = request.getParameter("password");

                // Update user information in the database
                updateUserProfile(username, email, password);
                session.setAttribute("user", username);  // Retain user session
                response.sendRedirect("profile.jsp?success");
            } else {
                response.sendRedirect("login.jsp");
            }
        }
        // Delete account
        else if ("deleteAccount".equals(action)) {
            HttpSession session = request.getSession();
            String username = (String) session.getAttribute("user");
            if (username != null) {
                deleteUserAccount(username);
                session.invalidate(); // Invalidate session after account deletion
                response.sendRedirect("index.jsp?accountDeleted");
            } else {
                response.sendRedirect("login.jsp");
            }
        }
        // Default redirect to index if action is invalid
        else {
            response.sendRedirect("index.jsp");
        }
    }

    // Get user by username from the database
    private User getUserByUsername(String username) {
        User user = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    user = new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("username"),
                        rs.getInt("role_id")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // Update user profile in the database
    private void updateUserProfile(String username, String email, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE users SET email = ?, password = ? WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setString(2, password); // Assume password is already hashed
                stmt.setString(3, username);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete user account from the database
    private void deleteUserAccount(String username) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

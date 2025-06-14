package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Models.Role;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "RoleController", urlPatterns = {"/role"})
public class RoleController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("view".equals(action)) {
            int roleId = Integer.parseInt(request.getParameter("id"));
            Role role = getRoleById(roleId);
            request.setAttribute("role", role);
            request.getRequestDispatcher("/role/view.jsp").forward(request, response);
        } else if ("list".equals(action)) {
            List<Role> roleList = getAllRoles();
            request.setAttribute("roleList", roleList);
            request.getRequestDispatcher("/role/list.jsp").forward(request, response);
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String role = request.getParameter("role");

            Role newRole = new Role(0, name, role);
            saveRole(newRole);

            response.sendRedirect("role/list.jsp?success");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            String role = request.getParameter("role");

            Role updatedRole = new Role(id, name, role);
            updateRole(updatedRole);

            response.sendRedirect("role/list.jsp?success");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            deleteRole(id);

            response.sendRedirect("role/list.jsp?deleted");
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    // Retrieve Role by ID
    private Role getRoleById(int id) {
        Role role = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM roles WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    role = new Role(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }

    // Retrieve all Roles from the database
    private List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM roles";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Role role = new Role(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("role")
                    );
                    roleList.add(role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roleList;
    }

    // Save a new Role to the database
    private void saveRole(Role role) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO roles (name, role) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, role.getName());
                stmt.setString(2, role.getRole());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update an existing Role in the database
    private void updateRole(Role role) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE roles SET name = ?, role = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, role.getName());
                stmt.setString(2, role.getRole());
                stmt.setInt(3, role.getId());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete a Role from the database
    private void deleteRole(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM roles WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

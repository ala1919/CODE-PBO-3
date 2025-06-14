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
import Models.Category;
import java.util.ArrayList;

@WebServlet(name = "CategoryController", urlPatterns = {"/category"})
public class CategoryController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("view".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Category category = getCategoryById(id);
            request.setAttribute("category", category);
            request.getRequestDispatcher("/category/view.jsp").forward(request, response);
        } else if ("list".equals(action)) {
            request.setAttribute("categoryList", getAllCategories());
            request.getRequestDispatcher("/category/list.jsp").forward(request, response);
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
            Category category = new Category(0, name);
            saveCategory(category);
            response.sendRedirect("category/list.jsp?success");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String name = request.getParameter("name");
            Category category = new Category(id, name);
            updateCategory(category);
            response.sendRedirect("category/list.jsp?success");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            deleteCategory(id);
            response.sendRedirect("category/list.jsp?deleted");
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    // Retrieve category by ID
    private Category getCategoryById(int id) {
        Category category = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM categories WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    category = new Category(
                        rs.getInt("id"),
                        rs.getString("nama")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }

    // Retrieve all categories from the database
    private java.util.List<Category> getAllCategories() {
        java.util.List<Category> categoryList = new java.util.ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM categories";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("nama")
                    );
                    categoryList.add(category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryList;
    }

    // Save new category to the database
    private void saveCategory(Category category) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO categories (nama) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, category.getName());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update category in the database
    private void updateCategory(Category category) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE categories SET nama = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, category.getName());
                stmt.setInt(2, category.getId());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete category from the database
    private void deleteCategory(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM categories WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void someMethod(HttpServletRequest request) {
        ArrayList<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
        for (Category category : categories) {
            // Do something with each category
        }
    }
}

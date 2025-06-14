package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Models.Admin;

@WebServlet(name = "AdminController", urlPatterns = {"/admin"})
public class AdminController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("view".equals(action)) {
            // Retrieve and display the admin information (for example, by id)
            int id = Integer.parseInt(request.getParameter("id"));
            Admin admin = getAdminById(id);
            request.setAttribute("admin", admin);
            request.getRequestDispatcher("/admin/view.jsp").forward(request, response);
        } else {
            // Default action or invalid action - redirect to dashboard or list page
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            // Handle adding a new admin
            String nip = request.getParameter("nip");
            Admin admin = new Admin();
            admin.setNip(nip);

            // Save the new admin
            saveAdmin(admin);

            response.sendRedirect("admin/dashboard.jsp?success");
        } else if ("update".equals(action)) {
            // Handle updating admin information
            int id = Integer.parseInt(request.getParameter("id"));
            String nip = request.getParameter("nip");

            Admin admin = new Admin(id, nip);
            updateAdmin(admin);

            response.sendRedirect("admin/dashboard.jsp?success");
        } else if ("delete".equals(action)) {
            // Handle deleting an admin
            int id = Integer.parseInt(request.getParameter("id"));
            deleteAdmin(id);

            response.sendRedirect("admin/dashboard.jsp?deleted");
        } else {
            // Default action or invalid action - redirect to dashboard
            response.sendRedirect("index.jsp");
        }
    }

    // Method to retrieve admin by id from the database
    private Admin getAdminById(int id) {
        // Implement database logic to get Admin by id
        // Using a dummy admin for demonstration purposes
        return new Admin(id, "123456789");
    }

    // Method to save a new admin to the database
    private void saveAdmin(Admin admin) {
        // Implement database logic to insert new admin into the database
        System.out.println("Admin with NIP: " + admin.getNip() + " saved to the database.");
    }

    // Method to update an admin's information
    private void updateAdmin(Admin admin) {
        // Implement database logic to update the admin record
        System.out.println("Admin with ID: " + admin.getId() + " updated.");
    }

    // Method to delete an admin from the database
    private void deleteAdmin(int id) {
        // Implement database logic to delete an admin by id
        System.out.println("Admin with ID: " + id + " deleted from the database.");
    }
}



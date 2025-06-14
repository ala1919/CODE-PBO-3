package Controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import Models.Buku;
import Models.Category;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "BukuController", urlPatterns = {"/buku"})
public class BukuController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("view".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Buku buku = getBukuById(id);
            request.setAttribute("buku", buku);
            request.getRequestDispatcher("/buku/view.jsp").forward(request, response);
        } else if ("list".equals(action)) {
            handleListAction(request, response);
        } else if ("add".equals(action)) {
            // Ambil daftar kategori untuk form add
            ArrayList<Category> categories = getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/buku/add.jsp").forward(request, response);
        } else if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Buku buku = getBukuById(id);
            ArrayList<Category> categories = getAllCategories();
            request.setAttribute("book", buku);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/buku/edit.jsp").forward(request, response);
        } else if ("search".equals(action)) {
            String query = request.getParameter("query");
            ArrayList<Buku> bukuList = searchBuku(query);
            request.setAttribute("bukuList", bukuList);
            ArrayList<Category> categories = getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/buku/index.jsp").forward(request, response);
        } else {
            // Default action - tampilkan semua buku dengan filter kategori jika ada
            handleListAction(request, response);
        }
    }

    private void handleListAction(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String categoryIdParam = request.getParameter("categoryId");
        ArrayList<Buku> bukuList;
        
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            // Filter berdasarkan kategori
            int categoryId = Integer.parseInt(categoryIdParam);
            bukuList = getBukuByCategory(categoryId);
        } else {
            // Tampilkan semua buku
            bukuList = getAllBuku();
        }
        
        request.setAttribute("bukuList", bukuList);
        ArrayList<Category> categories = getAllCategories();
        request.setAttribute("categories", categories);
        request.getRequestDispatcher("/buku/index.jsp").forward(request, response);
    }

    private ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM categories ORDER BY name";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Category category = new Category(
                        rs.getInt("id"),
                        rs.getString("name")
                    );
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Method baru untuk mengambil buku berdasarkan kategori
    private ArrayList<Buku> getBukuByCategory(int categoryId) {
        ArrayList<Buku> bukuList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM books WHERE kategori_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, categoryId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Buku buku = new Buku(
                        rs.getInt("id"),
                        rs.getBoolean("available"),
                        rs.getString("judul"),
                        rs.getInt("max_pinjam"),
                        rs.getString("penerbit"),
                        rs.getString("pengarang"),
                        rs.getInt("tahun_terbit"),
                        rs.getInt("kategori_id")
                    );
                    bukuList.add(buku);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bukuList;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String judul = request.getParameter("judul");
            boolean available = Boolean.parseBoolean(request.getParameter("available"));
            int maxPinjam = Integer.parseInt(request.getParameter("maxPinjam"));
            String penerbit = request.getParameter("penerbit");
            String pengarang = request.getParameter("pengarang");
            int tahunTerbit = Integer.parseInt(request.getParameter("tahunTerbit"));
            int kategoriId = Integer.parseInt(request.getParameter("kategoriId"));

            Buku buku = new Buku(0, available, judul, maxPinjam, penerbit, pengarang, tahunTerbit, kategoriId);
            saveBuku(buku);
            response.sendRedirect(request.getContextPath() + "/buku?action=list&success=true");
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String judul = request.getParameter("judul");
            boolean available = Boolean.parseBoolean(request.getParameter("available"));
            int maxPinjam = Integer.parseInt(request.getParameter("maxPinjam"));
            String penerbit = request.getParameter("penerbit");
            String pengarang = request.getParameter("pengarang");
            int tahunTerbit = Integer.parseInt(request.getParameter("tahunTerbit"));
            int kategoriId = Integer.parseInt(request.getParameter("kategoriId"));

            Buku buku = new Buku(id, available, judul, maxPinjam, penerbit, pengarang, tahunTerbit, kategoriId);
            updateBuku(buku);

            response.sendRedirect(request.getContextPath() + "/buku?action=list&success=true");
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            deleteBuku(id);
            response.sendRedirect(request.getContextPath() + "/buku?action=list&deleted=true");
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    // Retrieve Buku by ID
    private Buku getBukuById(int id) {
        Buku buku = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM books WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    buku = new Buku(
                        rs.getInt("id"),
                        rs.getBoolean("available"),
                        rs.getString("judul"),
                        rs.getInt("max_pinjam"),
                        rs.getString("penerbit"),
                        rs.getString("pengarang"),
                        rs.getInt("tahun_terbit"),
                        rs.getInt("kategori_id")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buku;
    }

    // Retrieve all Buku from the database
    private ArrayList<Buku> getAllBuku() {
        ArrayList<Buku> bukuList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM books ORDER BY judul";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Buku buku = new Buku(
                        rs.getInt("id"),
                        rs.getBoolean("available"),
                        rs.getString("judul"),
                        rs.getInt("max_pinjam"),
                        rs.getString("penerbit"),
                        rs.getString("pengarang"),
                        rs.getInt("tahun_terbit"),
                        rs.getInt("kategori_id")
                    );
                    bukuList.add(buku);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bukuList;
    }

    // Save new Buku to the database
    private void saveBuku(Buku buku) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO books (available, judul, max_pinjam, penerbit, pengarang, tahun_terbit, kategori_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setBoolean(1, buku.isAvailable());
                stmt.setString(2, buku.getJudul());
                stmt.setInt(3, buku.getMaxPinjam());
                stmt.setString(4, buku.getPenerbit());
                stmt.setString(5, buku.getPengarang());
                stmt.setInt(6, buku.getTahunTerbit());
                stmt.setInt(7, buku.getKategoriId());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update Buku in the database
    private void updateBuku(Buku buku) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE books SET available = ?, judul = ?, max_pinjam = ?, penerbit = ?, pengarang = ?, tahun_terbit = ?, kategori_id = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setBoolean(1, buku.isAvailable());
                stmt.setString(2, buku.getJudul());
                stmt.setInt(3, buku.getMaxPinjam());
                stmt.setString(4, buku.getPenerbit());
                stmt.setString(5, buku.getPengarang());
                stmt.setInt(6, buku.getTahunTerbit());
                stmt.setInt(7, buku.getKategoriId());
                stmt.setInt(8, buku.getId());
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete Buku from the database
    private void deleteBuku(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Search Buku by title or author
    private ArrayList<Buku> searchBuku(String keyword) {
        ArrayList<Buku> bukuList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM books WHERE judul LIKE ? OR pengarang LIKE ? OR penerbit LIKE ? ORDER BY judul";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String like = "%" + keyword + "%";
                stmt.setString(1, like);
                stmt.setString(2, like);
                stmt.setString(3, like);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Buku buku = new Buku(
                        rs.getInt("id"),
                        rs.getBoolean("available"),
                        rs.getString("judul"),
                        rs.getInt("max_pinjam"),
                        rs.getString("penerbit"),
                        rs.getString("pengarang"),
                        rs.getInt("tahun_terbit"),
                        rs.getInt("kategori_id")
                    );
                    bukuList.add(buku);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bukuList;
    }
}
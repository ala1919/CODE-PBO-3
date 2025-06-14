package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Models.Borrow;
import Models.Buku;

@WebServlet(name = "BorrowController", urlPatterns = {"/borrow"})
public class BorrowController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        String role = (String) session.getAttribute("role");
        Integer userId = (Integer) session.getAttribute("userId");

        if ("view".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Borrow borrow = getBorrowById(id);
            request.setAttribute("borrow", borrow);
            request.getRequestDispatcher("/borrow/view.jsp").forward(request, response);
            
        } else if ("list".equals(action)) {
            // Handle "View Pinjam" button from book list page
            if ("USER".equals(role) && userId != null) {
                // For regular users, show only their borrow records
                request.setAttribute("returnedBorrowList", getUserReturnedBorrows(userId));
                request.setAttribute("unreturnedBorrowList", getUserUnreturnedBorrows(userId));
                request.getRequestDispatcher("/borrow/index.jsp").forward(request, response);
            } else if ("ADMIN".equals(role)) {
                // For admin, show all borrow records
                request.setAttribute("returnedBorrowList", getReturnedBorrows());
                request.setAttribute("unreturnedBorrowList", getUnreturnedBorrows());
                request.getRequestDispatcher("/borrow/adminList.jsp").forward(request, response);
            } else {
                response.sendRedirect("login.jsp");
            }
            
        } else if ("index".equals(action)) {
            // Retrieve lists of borrowed books, separated into returned and unreturned
            if ("USER".equals(role) && userId != null) {
                request.setAttribute("returnedBorrowList", getUserReturnedBorrows(userId));
                request.setAttribute("unreturnedBorrowList", getUserUnreturnedBorrows(userId));
                request.getRequestDispatcher("/borrow/index.jsp").forward(request, response);
            } else {
                request.setAttribute("returnedBorrowList", getReturnedBorrows());
                request.setAttribute("unreturnedBorrowList", getUnreturnedBorrows());
                request.getRequestDispatcher("/borrow/index.jsp").forward(request, response);
            }
            
        } else {
            // Default case - redirect to main page instead of index.jsp
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            int denda = Integer.parseInt(request.getParameter("denda"));
            boolean returned = Boolean.parseBoolean(request.getParameter("returned"));
            String tanggalPengembalian = request.getParameter("tanggalPengembalian");
            String tanggalPinjam = request.getParameter("tanggalPinjam");
            String tenggatKembali = request.getParameter("tenggatKembali");
            int bukuId = Integer.parseInt(request.getParameter("bukuId"));
            int userId = Integer.parseInt(request.getParameter("userId"));

            Borrow borrow = new Borrow(0, denda, returned, tanggalPengembalian, tanggalPinjam, tenggatKembali, bukuId, userId);
            saveBorrow(borrow);

            response.sendRedirect("borrow?action=list&success=true");
            
        } else if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            int denda = Integer.parseInt(request.getParameter("denda"));
            boolean returned = Boolean.parseBoolean(request.getParameter("returned"));
            String tanggalPengembalian = request.getParameter("tanggalPengembalian");
            String tanggalPinjam = request.getParameter("tanggalPinjam");
            String tenggatKembali = request.getParameter("tenggatKembali");
            int bukuId = Integer.parseInt(request.getParameter("bukuId"));
            int userId = Integer.parseInt(request.getParameter("userId"));

            Borrow borrow = new Borrow(id, denda, returned, tanggalPengembalian, tanggalPinjam, tenggatKembali, bukuId, userId);
            updateBorrow(borrow);

            response.sendRedirect("borrow?action=list&updated=true");
            
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            deleteBorrow(id);

            response.sendRedirect("borrow?action=list&deleted=true");
            
        } else if ("checkout".equals(action)) {
            // Handle checkout process - move all cart items to borrow table
            processCheckout(request, response);
            
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    // Process checkout - move all cart items to borrow table
    private void processCheckout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Get cartId from request parameter
        String cartIdParam = request.getParameter("cartId");
        if (cartIdParam == null || cartIdParam.isEmpty()) {
            response.sendRedirect("cart?action=list&error=empty");
            return;
        }
        
        int cartId = Integer.parseInt(cartIdParam);
        
        try {
            // Get all book IDs from the specific cart
            java.util.List<Integer> cartBookIds = getCartBookIdsByCartId(cartId);
            
            if (cartBookIds.isEmpty()) {
                // No items in cart
                response.sendRedirect("cart?action=list&error=empty");
                return;
            }
            
            // Get current date for borrowing
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String tanggalPinjam = currentDate.format(formatter);
            
            // Create borrow records for each book in cart
            boolean success = true;
            for (Integer bukuId : cartBookIds) {
                // Get max pinjam days for this book to calculate due date
                int maxPinjamDays = getMaxPinjamDays(bukuId);
                LocalDate dueDate = currentDate.plusDays(maxPinjamDays);
                String tenggatKembali = dueDate.format(formatter);
                
                Borrow borrow = new Borrow(
                    0,                          // id (auto-generated)
                    0,                          // denda (initially 0)
                    false,                      // returned (initially false)
                    null,                       // tanggalPengembalian (null until returned)
                    tanggalPinjam,              // tanggalPinjam
                    tenggatKembali,             // tenggatKembali
                    bukuId,                     // bukuId
                    userId                      // userId
                );
                
                if (!saveBorrow(borrow)) {
                    success = false;
                    break;
                }
            }
            
            if (success) {
                // Clear the cart after successful checkout
                clearCartByCartId(cartId);
                response.sendRedirect("borrow?action=list&checkout=success");
            } else {
                response.sendRedirect("cart?action=list&error=checkout");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("cart?action=list&error=system");
        }
    }

    // Get book IDs from specific cart
    private java.util.List<Integer> getCartBookIdsByCartId(int cartId) {
        java.util.List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT buku_id FROM cart_item WHERE cart_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cartId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookIds.add(rs.getInt("buku_id"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return bookIds;
    }

    // Get max pinjam days for a book
    private int getMaxPinjamDays(int bukuId) {
        String sql = "SELECT max_pinjam FROM buku WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bukuId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("max_pinjam");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return 7; // Default 7 days if not found
    }

    // Clear cart by cart ID
    private void clearCartByCartId(int cartId) {
        String sql = "DELETE FROM cart_item WHERE cart_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save borrow record to database
    private boolean saveBorrow(Borrow borrow) {
        String sql = "INSERT INTO borrow (denda, returned, tanggal_pengembalian, tanggal_pinjam, tenggat_kembali, buku_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, borrow.getDenda());
            stmt.setBoolean(2, borrow.isReturned());
            stmt.setString(3, borrow.getTanggalPengembalian());
            stmt.setString(4, borrow.getTanggalPinjam());
            stmt.setString(5, borrow.getTenggatKembali());
            stmt.setInt(6, borrow.getBukuId());
            stmt.setInt(7, borrow.getUserId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update borrow record
    private void updateBorrow(Borrow borrow) {
        String sql = "UPDATE borrow SET denda = ?, returned = ?, tanggal_pengembalian = ?, tanggal_pinjam = ?, tenggat_kembali = ?, buku_id = ?, user_id = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, borrow.getDenda());
            stmt.setBoolean(2, borrow.isReturned());
            stmt.setString(3, borrow.getTanggalPengembalian());
            stmt.setString(4, borrow.getTanggalPinjam());
            stmt.setString(5, borrow.getTenggatKembali());
            stmt.setInt(6, borrow.getBukuId());
            stmt.setInt(7, borrow.getUserId());
            stmt.setInt(8, borrow.getId());
            
            stmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete borrow record
    private void deleteBorrow(int id) {
        String sql = "DELETE FROM borrow WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get borrow by ID
    private Borrow getBorrowById(int id) {
        String sql = "SELECT * FROM borrow WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Borrow(
                    rs.getInt("id"),
                    rs.getInt("denda"),
                    rs.getBoolean("returned"),
                    rs.getString("tanggal_pengembalian"),
                    rs.getString("tanggal_pinjam"),
                    rs.getString("tenggat_kembali"),
                    rs.getInt("buku_id"),
                    rs.getInt("user_id")
                );
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    // Get all returned borrows
    private ArrayList<Borrow> getReturnedBorrows() {
        ArrayList<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrow WHERE returned = true ORDER BY tanggal_pengembalian DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrow borrow = new Borrow(
                    rs.getInt("id"),
                    rs.getInt("denda"),
                    rs.getBoolean("returned"),
                    rs.getString("tanggal_pengembalian"),
                    rs.getString("tanggal_pinjam"),
                    rs.getString("tenggat_kembali"),
                    rs.getInt("buku_id"),
                    rs.getInt("user_id")
                );
                borrows.add(borrow);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return borrows;
    }

    // Get all unreturned borrows
    private ArrayList<Borrow> getUnreturnedBorrows() {
        ArrayList<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrow WHERE returned = false ORDER BY tenggat_kembali ASC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrow borrow = new Borrow(
                    rs.getInt("id"),
                    rs.getInt("denda"),
                    rs.getBoolean("returned"),
                    rs.getString("tanggal_pengembalian"),
                    rs.getString("tanggal_pinjam"),
                    rs.getString("tenggat_kembali"),
                    rs.getInt("buku_id"),
                    rs.getInt("user_id")
                );
                borrows.add(borrow);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return borrows;
    }

    // Get user's returned borrows
    private ArrayList<Borrow> getUserReturnedBorrows(int userId) {
        ArrayList<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrow WHERE user_id = ? AND returned = true ORDER BY tanggal_pengembalian DESC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrow borrow = new Borrow(
                    rs.getInt("id"),
                    rs.getInt("denda"),
                    rs.getBoolean("returned"),
                    rs.getString("tanggal_pengembalian"),
                    rs.getString("tanggal_pinjam"),
                    rs.getString("tenggat_kembali"),
                    rs.getInt("buku_id"),
                    rs.getInt("user_id")
                );
                borrows.add(borrow);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return borrows;
    }

    // Get user's unreturned borrows
    private ArrayList<Borrow> getUserUnreturnedBorrows(int userId) {
        ArrayList<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM borrow WHERE user_id = ? AND returned = false ORDER BY tenggat_kembali ASC";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Borrow borrow = new Borrow(
                    rs.getInt("id"),
                    rs.getInt("denda"),
                    rs.getBoolean("returned"),
                    rs.getString("tanggal_pengembalian"),
                    rs.getString("tanggal_pinjam"),
                    rs.getString("tenggat_kembali"),
                    rs.getInt("buku_id"),
                    rs.getInt("user_id")
                );
                borrows.add(borrow);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return borrows;
    }
}
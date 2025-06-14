package Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import Models.Buku;
import Models.Cart;

@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/perpus";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            // Handle GET request for add to cart
            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            
            if (userIdObj == null) {
                response.sendRedirect("index.jsp?error=notLoggedIn");
                return;
            }
            
            int userId = (Integer) userIdObj;
            int bukuId = Integer.parseInt(request.getParameter("bukuId"));
            
            // Cek apakah user sudah memiliki cart aktif
            Cart existingCart = getActiveCartByUser(userId);
            if (existingCart != null) {
                // Jika sudah ada cart aktif, tambahkan buku ke cart yang sudah ada
                addBookToCart(existingCart.getId(), bukuId);
            } else {
                // Jika belum ada cart aktif, buat cart baru
                LocalDate checkoutDate = LocalDate.now();
                int cartId = createNewCart(userId, checkoutDate);
                addBookToCart(cartId, bukuId);
            }
            
            // Update cart count di session
            updateCartCount(session, userId);
            
            // Redirect kembali ke halaman buku dengan mempertahankan parameter filter/search
            String redirectUrl = buildRedirectUrl(request);
            response.sendRedirect(redirectUrl);
            
        } else if ("list".equals(action)) {
            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            
            if (userIdObj == null) {
                response.sendRedirect("index.jsp?error=notLoggedIn");
                return;
            }
            
            int userId = (Integer) userIdObj;
            List<Cart> cartList = getCartsByUser(userId);
            request.setAttribute("cartList", cartList);
            request.getRequestDispatcher("/cart/index.jsp").forward(request, response);
            
        } else if ("view".equals(action)) {
            int cartId = Integer.parseInt(request.getParameter("id"));
            Cart cart = getCartById(cartId);
            request.setAttribute("cart", cart);
            request.getRequestDispatcher("/cart/view.jsp").forward(request, response);
            
        } else if ("remove".equals(action)) {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            int bukuId = Integer.parseInt(request.getParameter("bukuId"));
            removeBookFromCart(cartId, bukuId);
            
            // Update cart count di session
            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            if (userIdObj != null) {
                updateCartCount(session, (Integer) userIdObj);
            }
            
            response.sendRedirect("cart?action=list&removed=true");
            
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    // Method untuk membangun URL redirect yang mempertahankan parameter filter/search
    private String buildRedirectUrl(HttpServletRequest request) {
        StringBuilder redirectUrl = new StringBuilder();
        redirectUrl.append(request.getContextPath()).append("/buku");
        
        // Ambil parameter dari referer header untuk mempertahankan filter/search
        String referer = request.getHeader("referer");
        if (referer != null && referer.contains("/buku")) {
            // Extract parameters dari referer URL
            String[] parts = referer.split("\\?");
            if (parts.length > 1) {
                String queryString = parts[1];
                String[] params = queryString.split("&");
                
                List<String> preservedParams = new ArrayList<>();
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];
                        
                        // Pertahankan parameter filter dan search
                        if ("categoryId".equals(key) || "query".equals(key) || "action".equals(key)) {
                            preservedParams.add(param);
                        }
                    }
                }
                
                if (!preservedParams.isEmpty()) {
                    redirectUrl.append("?");
                    redirectUrl.append(String.join("&", preservedParams));
                    redirectUrl.append("&cartSuccess=true");
                } else {
                    redirectUrl.append("?action=list&cartSuccess=true");
                }
            } else {
                redirectUrl.append("?action=list&cartSuccess=true");
            }
        } else {
            redirectUrl.append("?action=list&cartSuccess=true");
        }
        
        return redirectUrl.toString();
    }

    // Method untuk update cart count di session
    private void updateCartCount(HttpSession session, int userId) {
        Cart activeCart = getActiveCartByUser(userId);
        if (activeCart != null) {
            List<Buku> books = getBooksByCartId(activeCart.getId());
            session.setAttribute("cartCount", books.size());
        } else {
            session.setAttribute("cartCount", 0);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            
            if (userIdObj == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            
            int userId = (Integer) userIdObj;
            int bukuId = Integer.parseInt(request.getParameter("bukuId"));
            
            // Cek apakah user sudah memiliki cart aktif
            Cart existingCart = getActiveCartByUser(userId);
            
            if (existingCart != null) {
                // Jika sudah ada cart aktif, tambahkan buku ke cart yang sudah ada
                addBookToCart(existingCart.getId(), bukuId);
            } else {
                // Jika belum ada cart aktif, buat cart baru
                LocalDate checkoutDate = LocalDate.now();
                int cartId = createNewCart(userId, checkoutDate);
                addBookToCart(cartId, bukuId);
            }

            // Update cart count di session
            updateCartCount(session, userId);

            // Redirect kembali ke halaman buku dengan parameter success
            String redirectUrl = buildRedirectUrl(request);
            response.sendRedirect(redirectUrl);
            
        } else if ("remove".equals(action)) {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            int bukuId = Integer.parseInt(request.getParameter("bukuId"));
            removeBookFromCart(cartId, bukuId);

            // Update cart count di session
            HttpSession session = request.getSession();
            Object userIdObj = session.getAttribute("userId");
            if (userIdObj != null) {
                updateCartCount(session, (Integer) userIdObj);
            }

            response.sendRedirect("cart?action=list&removed=true");
        } else if ("finalize".equals(action)) {
            int cartId = Integer.parseInt(request.getParameter("cartId"));
            finalizeCheckout(cartId);

            response.sendRedirect("cart?action=checkout&completed=true");
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    private Cart getCartById(int cartId) {
        Cart cart = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM cart WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cartId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cart = new Cart(
                        rs.getInt("id"),
                        rs.getDate("checkout_date").toLocalDate(),
                        getBooksByCartId(cartId),
                        rs.getInt("created_by")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cart;
    }

    private List<Cart> getCartsByUser(int userId) {
        List<Cart> cartList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM cart WHERE created_by = ? ORDER BY checkout_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Cart cart = new Cart(
                        rs.getInt("id"),
                        rs.getDate("checkout_date").toLocalDate(),
                        getBooksByCartId(rs.getInt("id")),
                        rs.getInt("created_by")
                    );
                    cartList.add(cart);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartList;
    }

    private Cart getActiveCartByUser(int userId) {
        Cart cart = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM cart WHERE created_by = ? AND (status IS NULL OR status != 'checked_out') ORDER BY id DESC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cart = new Cart(
                        rs.getInt("id"),
                        rs.getDate("checkout_date").toLocalDate(),
                        getBooksByCartId(rs.getInt("id")),
                        rs.getInt("created_by")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cart;
    }

    private List<Buku> getBooksByCartId(int cartId) {
        List<Buku> bukuList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT b.id, b.available, b.judul, b.max_pinjam, b.penerbit, b.pengarang, b.tahun_terbit, b.kategori_id " +
                           "FROM cart_buku cb " +
                           "JOIN books b ON cb.buku_id = b.id " +
                           "WHERE cb.cart_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cartId);
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

    private int createNewCart(int userId, LocalDate checkoutDate) {
        int cartId = 0;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO cart (checkout_date, created_by) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, java.sql.Date.valueOf(checkoutDate));
                stmt.setInt(2, userId);
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    cartId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartId;
    }

    private void addBookToCart(int cartId, int bukuId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Cek apakah buku sudah ada di cart
            String checkQuery = "SELECT COUNT(*) FROM cart_buku WHERE cart_id = ? AND buku_id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, cartId);
                checkStmt.setInt(2, bukuId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Buku sudah ada di cart, tidak perlu menambahkan lagi
                    return;
                }
            }
            
            // Tambahkan buku ke cart
            String query = "INSERT INTO cart_buku (cart_id, buku_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cartId);
                stmt.setInt(2, bukuId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeBookFromCart(int cartId, int bukuId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM cart_buku WHERE cart_id = ? AND buku_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cartId);
                stmt.setInt(2, bukuId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finalizeCheckout(int cartId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE cart SET status = 'checked_out' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, cartId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
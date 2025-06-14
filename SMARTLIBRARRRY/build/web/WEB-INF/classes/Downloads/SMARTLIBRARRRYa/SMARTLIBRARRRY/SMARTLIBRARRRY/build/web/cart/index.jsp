<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="Models.Cart"%>
<%@page import="Models.Buku"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Keranjang Belanja</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container my-5">
        <div class="card shadow">
            <div class="card-header bg-success text-white text-center">
                <h2><i class="bi bi-cart"></i> Keranjang Belanja Anda</h2>
            </div>
            <div class="card-body">

                <!-- Success Messages -->
                <% if ("true".equals(request.getParameter("success"))) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle-fill"></i>
                        <strong>Berhasil!</strong> Buku berhasil ditambahkan ke keranjang!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>
                <% if ("true".equals(request.getParameter("removed"))) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle-fill"></i>
                        <strong>Berhasil!</strong> Buku berhasil dihapus dari keranjang!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <!-- Navigation -->
                <div class="mb-4">
                    <a href="${pageContext.request.contextPath}/buku" class="btn btn-primary">
                        <i class="bi bi-arrow-left"></i> Kembali ke Daftar Buku
                    </a>
                </div>

                <!-- Cart Items -->
                <%
                    List<Cart> cartList = (List<Cart>) request.getAttribute("cartList");
                    Cart activeCart = null;
                    List<Buku> allBooks = new ArrayList<>();
                    
                    // Cari cart aktif (yang masih bisa dimodifikasi) dan kumpulkan semua bukunya
                    if (cartList != null && !cartList.isEmpty()) {
                        // Cari cart aktif (biasanya yang terbaru dan belum di-checkout)
                        for (Cart cart : cartList) {
                            List<Buku> bukus = cart.getBukus();
                            if (bukus != null && !bukus.isEmpty()) {
                                if (activeCart == null) {
                                    activeCart = cart; // Set cart pertama yang memiliki buku sebagai active cart
                                }
                                // Tambahkan semua buku, hindari duplikasi berdasarkan ID
                                for (Buku buku : bukus) {
                                    boolean exists = false;
                                    for (Buku existing : allBooks) {
                                        if (existing.getId() == buku.getId()) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (!exists) {
                                        allBooks.add(buku);
                                    }
                                }
                            }
                        }
                        
                        // Jika tidak ada cart dengan buku, ambil cart pertama sebagai referensi
                        if (activeCart == null && !cartList.isEmpty()) {
                            activeCart = cartList.get(0);
                        }
                    }
                    
                    if (!allBooks.isEmpty() && activeCart != null) {
                %>
                    <div class="card mb-4">
                        <div class="card-header bg-light">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0">
                                    <i class="bi bi-basket"></i> Keranjang Belanja Anda
                                </h5>
                                <small class="text-muted">Tanggal: <%= activeCart.getCheckoutDate() %></small>
                            </div>
                        </div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-hover table-striped">
                                    <thead class="table-primary">
                                        <tr>
                                            <th scope="col" class="text-center">Judul Buku</th>
                                            <th scope="col" class="text-center">Pengarang</th>
                                            <th scope="col" class="text-center">Penerbit</th>
                                            <th scope="col" class="text-center">Tahun Terbit</th>
                                            <th scope="col" class="text-center">Max Pinjam</th>
                                            <th scope="col" class="text-center">Aksi</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (Buku book : allBooks) {
                                        %>
                                            <tr>
                                                <td class="text-center"><%= book.getJudul() %></td>
                                                <td class="text-center"><%= book.getPengarang() %></td>
                                                <td class="text-center"><%= book.getPenerbit() %></td>
                                                <td class="text-center"><%= book.getTahunTerbit() %></td>
                                                <td class="text-center"><%= book.getMaxPinjam() %> hari</td>
                                                <td class="text-center">
                                                    <a href="${pageContext.request.contextPath}/cart?action=remove&cartId=<%= activeCart.getId() %>&bukuId=<%= book.getId() %>" 
                                                       class="btn btn-danger btn-sm"
                                                       onclick="return confirm('Apakah Anda yakin ingin menghapus buku \"<%= book.getJudul() %>\" dari keranjang?')">
                                                       <i class="bi bi-trash"></i> Hapus
                                                    </a>
                                                </td>
                                            </tr>
                                        <%
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <strong>Total Buku: <%= allBooks.size() %> item</strong>
                                </div>
                                <div>
                                    <form action="${pageContext.request.contextPath}/borrow" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="checkout">
                                        <button type="submit" class="btn btn-success"
                                                onclick="return confirm('Apakah Anda yakin ingin meminjam semua buku yang ada di keranjang?')">
                                            <i class="bi bi-check-circle"></i> Proses Checkout
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                <%
                    } else {
                %>
                    <div class="text-center py-5">
                        <i class="bi bi-cart-x" style="font-size: 5rem; color: #6c757d;"></i>
                        <h3 class="text-muted mt-3">Keranjang Anda Kosong</h3>
                        <p class="text-muted">Belum ada buku yang ditambahkan ke keranjang.</p>
                        <a href="${pageContext.request.contextPath}/buku" class="btn btn-primary btn-lg mt-3">
                            <i class="bi bi-book"></i> Mulai Pinjam Buku
                        </a>
                    </div>
                <%
                    }
                %>

            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js"></script>
</html>
<%@page import="java.util.ArrayList"%>
<%@page import="Models.Category"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Daftar Buku</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        /* Fixed Success Alert Styles */
        .success-notification {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            min-width: 350px;
            max-width: 500px;
            animation: slideInRight 0.5s ease-out;
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            border: none;
            border-left: 5px solid #28a745;
        }
        
        @keyframes slideInRight {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
        
        .success-notification .alert-content {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .success-notification .success-icon {
            font-size: 24px;
            color: #28a745;
        }
        
        .success-notification .success-text {
            flex: 1;
        }
        
        .success-notification .success-title {
            font-weight: bold;
            margin-bottom: 2px;
            font-size: 16px;
        }
        
        .success-notification .success-message {
            font-size: 14px;
            margin: 0;
            opacity: 0.9;
        }
        
        /* Enhanced Button Styles */
        .btn-cart-add {
            background: linear-gradient(45deg, #ffc107, #ffca2c);
            border: none;
            color: #000;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-cart-add:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255, 193, 7, 0.4);
            color: #000;
        }
        
        .btn-cart-adding {
            background: #6c757d !important;
            color: white !important;
        }
        
        /* Enhanced Loading Modal */
        .loading-content {
            text-align: center;
            padding: 20px;
        }
        
        .loading-spinner {
            width: 3rem;
            height: 3rem;
            border-width: 0.3em;
        }
        
        /* Toast Notification Alternative */
        .toast-notification {
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            min-width: 350px;
        }
        
        .toast-success {
            background: linear-gradient(135deg, #28a745, #20c997);
            color: white;
            border: none;
        }
        
        .toast-success .toast-header {
            background: rgba(255,255,255,0.1);
            color: white;
            border-bottom: 1px solid rgba(255,255,255,0.2);
        }
        
        .toast-success .btn-close {
            filter: invert(1) grayscale(100%) brightness(200%);
        }
    </style>
</head>
<body>
    <div class="container my-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white text-center">
                <h2>Daftar Buku</h2>
            </div>
            <div class="card-body">

                <!-- Enhanced Success Notification -->
                <% if ("true".equals(request.getParameter("cartSuccess"))) { %>
                    <div class="alert alert-success success-notification" role="alert" id="successAlert">
                        <div class="alert-content">
                            <i class="bi bi-check-circle-fill success-icon"></i>
                            <div class="success-text">
                                <div class="success-title">Berhasil!</div>
                                <p class="success-message">Buku telah ditambahkan ke keranjang Anda.</p>
                            </div>
                            <button type="button" class="btn-close" onclick="closeSuccessAlert()"></button>
                        </div>
                    </div>
                <% } %>

                <!-- Alternative Toast Notification -->
                <div class="toast-container position-fixed top-0 end-0 p-3">
                    <% if ("true".equals(request.getParameter("cartSuccess"))) { %>
                        <div id="successToast" class="toast toast-success show" role="alert">
                            <div class="toast-header">
                                <i class="bi bi-check-circle-fill me-2"></i>
                                <strong class="me-auto">Berhasil</strong>
                                <small>Baru saja</small>
                                <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
                            </div>
                            <div class="toast-body">
                                <strong>Buku berhasil ditambahkan ke keranjang!</strong><br>
                                Anda dapat melihat keranjang dengan mengklik tombol "View Cart".
                            </div>
                        </div>
                    <% } %>
                </div>

                <!-- Filter by Category and Search form -->
                <div class="d-flex justify-content-between">
                    <!-- Category Filter -->
                    <form action="${pageContext.request.contextPath}/buku" method="get" class="form-inline d-flex my-2 my-lg-0">
                        <select name="categoryId" class="form-control me-2" style="max-width: 300px;">
                            <option value="" selected>Semua Kategori</option>
                            <% 
                                ArrayList<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
                                if (categories != null) {
                                    for (Category category : categories) {
                            %>
                                <option value="<%= category.getId() %>" 
                                    <%= request.getParameter("categoryId") != null && request.getParameter("categoryId").equals(String.valueOf(category.getId())) ? "selected" : "" %>>
                                    <%= category.getName() %>
                                </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <button type="submit" class="btn btn-primary">Filter</button>
                    </form>

                    <!-- Search Form -->
                    <form action="${pageContext.request.contextPath}/buku" method="get" class="form-inline d-flex my-2 my-lg-0">
                        <input type="hidden" name="action" value="search">
                        <input name="query" class="form-control me-2" type="search" placeholder="Search buku" 
                               aria-label="Search" style="max-width: 300px;" value="<%= request.getParameter("query") != null ? request.getParameter("query") : "" %>">
                        <button type="submit" class="btn btn-primary">Search</button>
                    </form>
                </div>

                <!-- Action Buttons for Admin and User -->
                <% 
                    String role = (String) session.getAttribute("role");
                    if ("ADMIN".equals(role)) {
                %>
                    <a class="btn btn-primary m-3 ms-0" href="/">Kembali</a>
                    <a class="btn btn-primary m-3 ms-0" href="${pageContext.request.contextPath}/buku?action=add">Add Buku</a>
                    <a class="btn btn-success m-3 ms-0" href="/checkouts">Checkout</a>
                    <a class="btn btn-success m-3 ms-0" href="/borrows">Data Peminjaman</a>
                <%
                    }
                %>
                <% 
                    if ("USER".equals(role)) {
                %>
                    <a class="btn btn-primary m-3 ms-0" href="/">Kembali</a>
                    <a class="btn btn-success m-3 ms-0" href="${pageContext.request.contextPath}/cart?action=list">
                        <i class="bi bi-cart"></i> View Cart
                        <% 
                            // Optional: Show cart count
                            Integer cartCount = (Integer) session.getAttribute("cartCount");
                            if (cartCount != null && cartCount > 0) {
                        %>
                            <span class="badge bg-danger"><%= cartCount %></span>
                        <% } %>
                    </a>
                    <a class="btn btn-success m-3 ms-0" href="${pageContext.request.contextPath}/borrow?action=list">
                        <i class="bi bi-box-arrow-in-right"></i> View Pinjam
                        <% 
                            // Optional: Show borrow count
                            Integer borrowCount = (Integer) session.getAttribute("borrowCount");
                            if (borrowCount != null && borrowCount > 0) {
                        %>
                            <span class="badge bg-danger"><%= borrowCount %></span>
                        <% } %>
                    </a>

                <%
                    }
                %>

                <!-- Clear Search/Filter Button -->
                <% if (request.getParameter("query") != null || request.getParameter("categoryId") != null) { %>
                    <a class="btn btn-secondary m-3 ms-0" href="${pageContext.request.contextPath}/buku">
                        <i class="bi bi-x-circle"></i> Clear Filter
                    </a>
                <% } %>

                <!-- Table of Books -->
                <table class="table table-hover table-striped table-bordered align-middle">
                    <thead class="table-primary">
                        <tr>
                            <th scope="col" class="text-center">Judul</th>
                            <th scope="col" class="text-center">Pengarang</th>
                            <th scope="col" class="text-center">Penerbit</th>
                            <th scope="col" class="text-center">Tahun Terbit</th>
                            <th scope="col" class="text-center">Kategori</th>
                            <th scope="col" class="text-center">Durasi Peminjaman</th>
                            <th scope="col" class="text-center">Status</th>
                            <th scope="col" class="text-center">Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            ArrayList<Models.Buku> bukuList = (ArrayList<Models.Buku>) request.getAttribute("bukuList");
                            if (bukuList != null && !bukuList.isEmpty()) {
                                for (Models.Buku buku : bukuList) {
                        %>
                            <tr>
                                <td class="text-center"><%= buku.getJudul() %></td>
                                <td class="text-center"><%= buku.getPengarang() %></td>
                                <td class="text-center"><%= buku.getPenerbit() %></td>
                                <td class="text-center"><%= buku.getTahunTerbit() %></td>
                                <td class="text-center">
                                    <%
                                        // Find category name by ID
                                        String categoryName = "Unknown";
                                        if (categories != null) {
                                            for (Category cat : categories) {
                                                if (cat.getId() == buku.getKategoriId()) {
                                                    categoryName = cat.getName();
                                                    break;
                                                }
                                            }
                                        }
                                    %>
                                    <%= categoryName %>
                                </td>
                                <td class="text-center"><%= buku.getMaxPinjam() %> hari</td>
                                <td class="text-center">
                                    <% if (buku.isAvailable()) { %>
                                        <span class="badge bg-success">Tersedia</span>
                                    <% } else { %>
                                        <span class="badge bg-danger">Tidak Tersedia</span>
                                    <% } %>
                                </td>

                                <!-- Admin Actions: Edit and Delete -->
                                <!-- User Actions: Add to Cart if available -->
                                <td class="text-center">
                                    <% if ("ADMIN".equals(role)) { %>
                                        <div class="btn-group" role="group">
                                            <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/buku?action=edit&id=<%= buku.getId() %>">
                                                <i class="bi bi-pencil"></i> Edit
                                            </a>
                                            <form action="${pageContext.request.contextPath}/buku" method="post" style="display:inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="<%= buku.getId() %>">
                                                <button type="submit" class="btn btn-danger btn-sm" 
                                                        onclick="return confirm('Apakah Anda yakin ingin menghapus buku ini?')">
                                                    <i class="bi bi-trash"></i> Delete
                                                </button>
                                            </form>
                                        </div>
                                    <% } %>
                                    <% if ("USER".equals(role)) { %>
                                        <% if (buku.isAvailable()) { %>
                                            <a class="btn btn-cart-add btn-sm add-to-cart-btn" 
                                               href="${pageContext.request.contextPath}/cart?action=add&bukuId=<%= buku.getId() %>"
                                               onclick="return handleAddToCart(this, '<%= buku.getJudul() %>')"
                                               data-book-title="<%= buku.getJudul() %>">
                                                <i class="bi bi-cart-plus"></i> Add to Cart
                                            </a>
                                        <% } else { %>
                                            <button class="btn btn-secondary btn-sm" disabled>
                                                <i class="bi bi-x-circle"></i> Tidak Tersedia
                                            </button>
                                        <% } %>
                                    <% } %>
                                </td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="8" class="text-center py-4">
                                    <div class="text-muted">
                                        <i class="bi bi-book"></i>
                                        <h5>Tidak ada buku yang ditemukan</h5>
                                        <p>Silakan coba dengan kata kunci atau kategori yang berbeda.</p>
                                    </div>
                                </td>
                            </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>

            </div>
        </div>
    </div>

    <!-- Enhanced Loading Modal -->
    <div class="modal fade" id="loadingModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
        <div class="modal-dialog modal-sm modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-body loading-content">
                    <div class="spinner-border text-primary loading-spinner" role="status">
                        <span class="visually-hidden">Loading...</span>
                    </div>
                    <h5 class="mt-3 mb-2">Menambahkan ke keranjang...</h5>
                    <p class="text-muted mb-0">Mohon tunggu sebentar</p>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js"></script>
    
    <script>
        // Enhanced Add to Cart Function
        function handleAddToCart(element, bookTitle) {
            // Show loading modal
            var loadingModal = new bootstrap.Modal(document.getElementById('loadingModal'));
            loadingModal.show();
            
            // Disable the button to prevent double clicks
            element.style.pointerEvents = 'none';
            element.classList.add('btn-cart-adding');
            element.innerHTML = '<i class="bi bi-hourglass-split"></i> Adding...';
            
            // Store original content for potential restoration
            element.setAttribute('data-original-content', '<i class="bi bi-cart-plus"></i> Add to Cart');
            
            // Let the normal link behavior continue
            return true;
        }
        
        // Function to close success alert
        function closeSuccessAlert() {
            var alert = document.getElementById('successAlert');
            if (alert) {
                alert.style.transform = 'translateX(100%)';
                setTimeout(function() {
                    alert.remove();
                }, 300);
            }
        }
        
        // Auto-hide success alert after 6 seconds
        setTimeout(function() {
            var alert = document.getElementById('successAlert');
            if (alert) {
                closeSuccessAlert();
            }
        }, 6000);
        
        // Auto-hide toast after 8 seconds
        setTimeout(function() {
            var toast = document.getElementById('successToast');
            if (toast) {
                var bsToast = bootstrap.Toast.getInstance(toast);
                if (bsToast) {
                    bsToast.hide();
                }
            }
        }, 8000);
        
        // Hide loading modal and restore buttons if page loads with success parameter
        <% if ("true".equals(request.getParameter("cartSuccess"))) { %>
            // Hide loading modal
            var loadingModal = bootstrap.Modal.getInstance(document.getElementById('loadingModal'));
            if (loadingModal) {
                loadingModal.hide();
            }
            
            // Restore all add to cart buttons
            document.querySelectorAll('.add-to-cart-btn').forEach(function(btn) {
                btn.style.pointerEvents = 'auto';
                btn.classList.remove('btn-cart-adding');
                var originalContent = btn.getAttribute('data-original-content');
                if (originalContent) {
                    btn.innerHTML = originalContent;
                }
            });
            
            // Show success notification with sound (if supported)
            try {
                // Optional: Play success sound
                var audio = new Audio('data:audio/wav;base64,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');
                audio.play().catch(function() {
                    // Ignore audio errors
                });
            } catch (e) {
                // Ignore audio errors
            }
        <% } %>
        
        // Enhanced error handling for failed requests
        window.addEventListener('beforeunload', function() {
            // Restore buttons if user navigates away during loading
            document.querySelectorAll('.add-to-cart-btn').forEach(function(btn) {
                btn.style.pointerEvents = 'auto';
                btn.classList.remove('btn-cart-adding');
                var originalContent = btn.getAttribute('data-original-content');
                if (originalContent) {
                    btn.innerHTML = originalContent;
                }
            });
        });
    </script>
</body>
</html>
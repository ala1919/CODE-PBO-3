<%@page import="java.util.List"%>
<%@page import="Models.Borrow"%>
<%@page import="Models.Buku"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Daftar Pinjaman Buku</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container my-5">
        <div class="card shadow">
            <div class="card-header bg-primary text-white text-center">
                <h2><i class="bi bi-book"></i> Daftar Pinjaman Buku Anda</h2>
            </div>
            <div class="card-body">

                <!-- Success Messages -->
                <% if ("success".equals(request.getParameter("checkout"))) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle-fill"></i>
                        <strong>Berhasil!</strong> Semua buku berhasil dipinjam! Selamat membaca!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>
                <% if ("true".equals(request.getParameter("success"))) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle-fill"></i>
                        <strong>Berhasil!</strong> Operasi berhasil dilakukan!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>
                <% if ("true".equals(request.getParameter("deleted"))) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle-fill"></i>
                        <strong>Berhasil!</strong> Record berhasil dihapus!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                <% } %>

                <!-- Navigation -->
                <div class="mb-4">
                    <a href="${pageContext.request.contextPath}/buku" class="btn btn-primary">
                        <i class="bi bi-arrow-left"></i> Kembali ke Daftar Buku
                    </a>
                    <a href="${pageContext.request.contextPath}/cart?action=list" class="btn btn-success">
                        <i class="bi bi-cart"></i> Lihat Keranjang
                    </a>
                </div>

                <!-- Unreturned Books Section -->
                <div class="card mb-4">
                    <div class="card-header bg-warning text-dark">
                        <h4 class="mb-0"><i class="bi bi-hourglass-split"></i> Buku yang Sedang Dipinjam</h4>
                    </div>
                    <div class="card-body">
                        <%
                            List<Borrow> unreturnedBorrowList = (List<Borrow>) request.getAttribute("unreturnedBorrowList");
                            if (unreturnedBorrowList != null && !unreturnedBorrowList.isEmpty()) {
                        %>
                            <div class="table-responsive">
                                <table class="table table-hover table-striped">
                                    <thead class="table-warning">
                                        <tr>
                                            <th scope="col" class="text-center">ID Buku</th>
                                            <th scope="col" class="text-center">Tanggal Pinjam</th>
                                            <th scope="col" class="text-center">Tenggat Kembali</th>
                                            <th scope="col" class="text-center">Denda</th>
                                            <th scope="col" class="text-center">Status</th>
                                            <th scope="col" class="text-center">Aksi</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (Borrow borrow : unreturnedBorrowList) {
                                        %>
                                            <tr>
                                                <td class="text-center"><%= borrow.getBukuId() %></td>
                                                <td class="text-center"><%= borrow.getTanggalPinjam() %></td>
                                                <td class="text-center"><%= borrow.getTenggatKembali() %></td>
                                                <td class="text-center">
                                                    <span class="badge bg-<%= borrow.getDenda() > 0 ? "danger" : "success" %>">
                                                        Rp <%= String.format("%,d", borrow.getDenda()) %>
                                                    </span>
                                                </td>
                                                <td class="text-center">
                                                    <span class="badge bg-warning text-dark">Dipinjam</span>
                                                </td>
                                                <td class="text-center">
                                                    <a href="borrow?action=view&id=<%= borrow.getId() %>" class="btn btn-info btn-sm">
                                                        <i class="bi bi-eye"></i> Detail
                                                    </a>
                                                </td>
                                            </tr>
                                        <%
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </div>
                            <div class="mt-3">
                                <strong>Total Buku Dipinjam: <%= unreturnedBorrowList.size() %> item</strong>
                            </div>
                        <%
                            } else {
                        %>
                            <div class="text-center py-4">
                                <i class="bi bi-check-circle" style="font-size: 3rem; color: #28a745;"></i>
                                <h5 class="text-muted mt-3">Tidak Ada Buku yang Sedang Dipinjam</h5>
                                <p class="text-muted">Semua buku sudah dikembalikan atau belum ada yang dipinjam.</p>
                            </div>
                        <%
                            }
                        %>
                    </div>
                </div>

                <!-- Returned Books Section -->
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h4 class="mb-0"><i class="bi bi-check-circle"></i> Riwayat Buku yang Sudah Dikembalikan</h4>
                    </div>
                    <div class="card-body">
                        <%
                            List<Borrow> returnedBorrowList = (List<Borrow>) request.getAttribute("returnedBorrowList");
                            if (returnedBorrowList != null && !returnedBorrowList.isEmpty()) {
                        %>
                            <div class="table-responsive">
                                <table class="table table-hover table-striped">
                                    <thead class="table-success">
                                        <tr>
                                            <th scope="col" class="text-center">ID Buku</th>
                                            <th scope="col" class="text-center">Tanggal Pinjam</th>
                                            <th scope="col" class="text-center">Tanggal Kembali</th>
                                            <th scope="col" class="text-center">Denda</th>
                                            <th scope="col" class="text-center">Status</th>
                                            <th scope="col" class="text-center">Aksi</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <%
                                            for (Borrow borrow : returnedBorrowList) {
                                        %>
                                            <tr>
                                                <td class="text-center"><%= borrow.getBukuId() %></td>
                                                <td class="text-center"><%= borrow.getTanggalPinjam() %></td>
                                                <td class="text-center"><%= borrow.getTanggalPengembalian() %></td>
                                                <td class="text-center">
                                                    <span class="badge bg-<%= borrow.getDenda() > 0 ? "danger" : "success" %>">
                                                        Rp <%= String.format("%,d", borrow.getDenda()) %>
                                                    </span>
                                                </td>
                                                <td class="text-center">
                                                    <span class="badge bg-success">Dikembalikan</span>
                                                </td>
                                                <td class="text-center">
                                                    <a href="borrow?action=view&id=<%= borrow.getId() %>" class="btn btn-info btn-sm">
                                                        <i class="bi bi-eye"></i> Detail
                                                    </a>
                                                </td>
                                            </tr>
                                        <%
                                            }
                                        %>
                                    </tbody>
                                </table>
                            </div>
                            <div class="mt-3">
                                <strong>Total Riwayat: <%= returnedBorrowList.size() %> item</strong>
                            </div>
                        <%
                            } else {
                        %>
                            <div class="text-center py-4">
                                <i class="bi bi-clock-history" style="font-size: 3rem; color: #6c757d;"></i>
                                <h5 class="text-muted mt-3">Belum Ada Riwayat Pengembalian</h5>
                                <p class="text-muted">Riwayat buku yang sudah dikembalikan akan muncul di sini.</p>
                            </div>
                        <%
                            }
                        %>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.min.js"></script>
</body>
</html>
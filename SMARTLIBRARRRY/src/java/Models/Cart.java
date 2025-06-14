package Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Cart extends Model<Cart> {

    private int id;
    private LocalDate checkoutDate;
    private List<Buku> bukus;
    private int createdBy;

    public Cart() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "cart";
        this.primaryKey = "id";
    }

    public Cart(int id, LocalDate checkoutDate, List<Buku> bukus, int createdBy) {
        // Constructor untuk memudahkan pembuatan objek Cart
        this.table = "cart";
        this.primaryKey = "id";
        this.id = id;
        this.checkoutDate = checkoutDate;
        this.bukus = bukus;
        this.createdBy = createdBy;
    }

    @Override
    public Cart toModel(ResultSet rs) {
        try {
            // Lakukan mapping resultSet ke objek Cart
            return new Cart(
                rs.getInt("id"),
                rs.getDate("checkout_date").toLocalDate(),
                // Anda bisa menghubungkan data buku di sini, misalnya menggunakan ID Buku
                null, // Untuk sementara, null, karena kita perlu mengimplementasikan pengambilan buku terkait
                rs.getInt("created_by")
            );
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    // Getter dan Setter untuk setiap field
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public List<Buku> getBukus() {
        return bukus;
    }

    public void setBukus(List<Buku> bukus) {
        this.bukus = bukus;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", checkoutDate=" + checkoutDate +
                ", bukus=" + bukus +
                ", createdBy=" + createdBy +
                '}';
    }
}

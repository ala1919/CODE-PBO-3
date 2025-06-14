package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Borrow extends Model<Borrow> {
    
    private int id;
    private int denda;
    private boolean returned;
    private String tanggalPengembalian;
    private String tanggalPinjam;
    private String tenggatKembali;
    private int bukuId;
    private int userId;

    public Borrow() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "borrow";
        this.primaryKey = "id";
    }

    public Borrow(int id, int denda, boolean returned, String tanggalPengembalian, 
                  String tanggalPinjam, String tenggatKembali, int bukuId, int userId) {
        // Constructor untuk memudahkan pembuatan objek Borrow
        this.table = "borrow";
        this.primaryKey = "id";
        this.id = id;
        this.denda = denda;
        this.returned = returned;
        this.tanggalPengembalian = tanggalPengembalian;
        this.tanggalPinjam = tanggalPinjam;
        this.tenggatKembali = tenggatKembali;
        this.bukuId = bukuId;
        this.userId = userId;
    }

    @Override
    public Borrow toModel(ResultSet rs) {
        try {
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

    public int getDenda() {
        return denda;
    }

    public void setDenda(int denda) {
        this.denda = denda;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public String getTanggalPengembalian() {
        return tanggalPengembalian;
    }

    public void setTanggalPengembalian(String tanggalPengembalian) {
        this.tanggalPengembalian = tanggalPengembalian;
    }

    public String getTanggalPinjam() {
        return tanggalPinjam;
    }

    public void setTanggalPinjam(String tanggalPinjam) {
        this.tanggalPinjam = tanggalPinjam;
    }

    public String getTenggatKembali() {
        return tenggatKembali;
    }

    public void setTenggatKembali(String tenggatKembali) {
        this.tenggatKembali = tenggatKembali;
    }

    public int getBukuId() {
        return bukuId;
    }

    public void setBukuId(int bukuId) {
        this.bukuId = bukuId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Borrow{" +
                "id=" + id +
                ", denda=" + denda +
                ", returned=" + returned +
                ", tanggalPengembalian='" + tanggalPengembalian + '\'' +
                ", tanggalPinjam='" + tanggalPinjam + '\'' +
                ", tenggatKembali='" + tenggatKembali + '\'' +
                ", bukuId=" + bukuId +
                ", userId=" + userId +
                '}';
    }
}

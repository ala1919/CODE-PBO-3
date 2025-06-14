package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Buku extends Model<Buku> {

    private int id;
    private boolean available;
    private String judul;
    private int maxPinjam;
    private String penerbit;
    private String pengarang;
    private int tahunTerbit;
    private int kategoriId;

    public Buku() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "books";
        this.primaryKey = "id";
    }

    public Buku(int id, boolean available, String judul, int maxPinjam, String penerbit, String pengarang, int tahunTerbit, int kategoriId) {
        // Constructor untuk memudahkan pembuatan objek Buku
        this.table = "books";
        this.primaryKey = "id";
        this.id = id;
        this.available = available;
        this.judul = judul;
        this.maxPinjam = maxPinjam;
        this.penerbit = penerbit;
        this.pengarang = pengarang;
        this.tahunTerbit = tahunTerbit;
        this.kategoriId = kategoriId;
    }

    @Override
    public Buku toModel(ResultSet rs) {
        try {
            return new Buku(
                rs.getInt("id"),
                rs.getBoolean("available"),
                rs.getString("judul"),
                rs.getInt("max_pinjam"),
                rs.getString("penerbit"),
                rs.getString("pengarang"),
                rs.getInt("tahun_terbit"),
                rs.getInt("kategori_id")
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public int getMaxPinjam() {
        return maxPinjam;
    }

    public void setMaxPinjam(int maxPinjam) {
        this.maxPinjam = maxPinjam;
    }

    public String getPenerbit() {
        return penerbit;
    }

    public void setPenerbit(String penerbit) {
        this.penerbit = penerbit;
    }

    public String getPengarang() {
        return pengarang;
    }

    public void setPengarang(String pengarang) {
        this.pengarang = pengarang;
    }

    public int getTahunTerbit() {
        return tahunTerbit;
    }

    public void setTahunTerbit(int tahunTerbit) {
        this.tahunTerbit = tahunTerbit;
    }

    public int getKategoriId() {
        return kategoriId;
    }

    public void setKategoriId(int kategoriId) {
        this.kategoriId = kategoriId;
    }

    @Override
    public String toString() {
        return "Buku{" +
                "id=" + id +
                ", available=" + available +
                ", judul='" + judul + '\'' +
                ", maxPinjam=" + maxPinjam +
                ", penerbit='" + penerbit + '\'' +
                ", pengarang='" + pengarang + '\'' +
                ", tahunTerbit=" + tahunTerbit +
                ", kategoriId=" + kategoriId +
                '}';
    }
}

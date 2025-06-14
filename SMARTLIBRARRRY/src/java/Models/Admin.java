package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin extends Model<Admin> {

    private int id;
    private String nip;

    public Admin() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "admin";
        this.primaryKey = "id";
    }

    public Admin(int id, String nip) {
        // Constructor untuk memudahkan pembuatan objek Admin
        this.table = "admin";
        this.primaryKey = "id";
        this.id = id;
        this.nip = nip;
    }

    @Override
    public Admin toModel(ResultSet rs) {
        try {
            return new Admin(
                rs.getInt("id"),
                rs.getString("nip")
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

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", nip='" + nip + '\'' +
                '}';
    }
}

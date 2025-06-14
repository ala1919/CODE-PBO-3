package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Category extends Model<Category> {

    private int id;
    private String name;

    public Category() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "categories"; // Pastikan nama tabel di database tetap 'categorie'
        this.primaryKey = "id";
    }

    public Category(int id, String name) {
        // Constructor untuk memudahkan pembuatan objek Category
        this.table = "categories"; // Pastikan nama tabel di database tetap 'categorie'
        this.primaryKey = "id";
        this.id = id;
        this.name = name;
    }

    @Override
    public Category toModel(ResultSet rs) {
        try {
            return new Category(
                rs.getInt("id"),
                rs.getString("nama")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

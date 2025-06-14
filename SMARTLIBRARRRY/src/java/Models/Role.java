package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Role extends Model<Role> {

    private int id;
    private String name;
    private String role;

    public Role() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "roles";
        this.primaryKey = "id";
    }

    public Role(int id, String name, String role) {
        // Constructor untuk memudahkan pembuatan objek Role
        this.table = "roles";
        this.primaryKey = "id";
        this.id = id;
        this.name = name;
        this.role = role;
    }

    @Override
    public Role toModel(ResultSet rs) {
        try {
            return new Role(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("role")
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

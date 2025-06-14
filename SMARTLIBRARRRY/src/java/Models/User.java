package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User extends Model<User> {

    private int id;
    private String email;
    private String password;
    private String username;
    private int roleId;

    public User() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "user";
        this.primaryKey = "id";
    }

    public User(int id, String email, String password, String username, int roleId) {
        // Constructor untuk memudahkan pembuatan objek User
        this.table = "user";
        this.primaryKey = "id";
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.roleId = roleId;
    }

    @Override
    public User toModel(ResultSet rs) {
        try {
            return new User(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("username"),
                rs.getInt("role_id")
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", roleId=" + roleId +
                '}';
    }
}

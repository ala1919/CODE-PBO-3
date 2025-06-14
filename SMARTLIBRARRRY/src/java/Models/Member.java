package Models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Member extends Model<Member> {

    private int id;
    private String noTelepon;

    public Member() {
        // Sesuaikan nilai table dan primaryKey
        this.table = "member";
        this.primaryKey = "id";
    }

    public Member(int id, String noTelepon) {
        // Constructor untuk memudahkan pembuatan objek Member
        this.table = "member";
        this.primaryKey = "id";
        this.id = id;
        this.noTelepon = noTelepon;
    }

    @Override
    public Member toModel(ResultSet rs) {
        try {
            return new Member(
                rs.getInt("id"),
                rs.getString("no_telepon")
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

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", noTelepon='" + noTelepon + '\'' +
                '}';
    }
}

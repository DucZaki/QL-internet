package database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quanly_quan_net";
    private static final String USER = "root";
    private static final String PASSWORD = "123456a@";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Lấy máy trống
    public static int getAvailableMachine() {
        String query = "SELECT id FROM may WHERE trang_thai = 'trong' LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Không có máy trống
    }

    // Cập nhật trạng thái máy khi khách thuê
    public static boolean updateMachineStatus(int machineId, int customerId) {
        String query = "UPDATE may SET trang_thai = 'dang_su_dung', id_khach = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, machineId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật máy về trạng thái trống khi hết thời gian chơi
    public static boolean resetMachine(int machineId) {
        String query = "UPDATE may SET trang_thai = 'trong', id_khach = NULL WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, machineId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

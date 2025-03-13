package ui;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

public class MayFrame extends JPanel {
    private JButton[] nutMay;
    private Timer dongHo;
    public MayFrame() {
        setLayout(new GridLayout(6, 5, 10, 10));
        nutMay = new JButton[30];
        for (int i = 0; i < 30; i++) {
            int mayId = i + 1;
            nutMay[i] = new JButton("Máy " + mayId);
            nutMay[i].setOpaque(true);
            nutMay[i].setBorderPainted(false);
            nutMay[i].setBackground(Color.GREEN);
            nutMay[i].setForeground(Color.BLACK);
            nutMay[i].setFont(new Font("Arial", Font.BOLD, 14));

            nutMay[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    xuLyClickMay(mayId);
                }
            });

            add(nutMay[i]);
        }
        taiTrangThaiMay();
        batDongHo();
    }
    private void xuLyClickMay(int mayId) {
        // Kiểm tra trạng thái hiện tại của máy
        try (Connection ketNoi = DatabaseConnection.getConnection();
             PreparedStatement stmt = ketNoi.prepareStatement("SELECT trang_thai, id_khach FROM may WHERE id = ?")) {
            stmt.setInt(1, mayId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String trangThai = rs.getString("trang_thai");

                if ("dang_su_dung".equals(trangThai)) {
                    // Máy đang được sử dụng, hiển thị tùy chọn để xóa khách
                    int idKhach = rs.getInt("id_khach");
                    String tenKhach = layTenKhach(idKhach);

                    int option = JOptionPane.showConfirmDialog(
                            this,
                            "Máy đang được sử dụng bởi: " + tenKhach + "\nBạn có muốn kết thúc phiên sử dụng?",
                            "Quản lý máy",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (option == JOptionPane.YES_OPTION) {
                        xoaKhachKhoiMay(mayId);
                    }
                } else {
                    moHopThoaiThemKhach(mayId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra trạng thái máy: " + e.getMessage());
        }
    }
    private String layTenKhach(int idKhach) {
        try (Connection ketNoi = DatabaseConnection.getConnection();
             PreparedStatement stmt = ketNoi.prepareStatement("SELECT ten FROM khach WHERE id = ?")) {
            stmt.setInt(1, idKhach);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("ten");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không xác định";
    }
    private void xoaKhachKhoiMay(int mayId) {
        try (Connection ketNoi = DatabaseConnection.getConnection();
             PreparedStatement stmt = ketNoi.prepareStatement("UPDATE may SET trang_thai = 'trong', id_khach = NULL WHERE id = ?")) {
            stmt.setInt(1, mayId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Đã kết thúc phiên sử dụng máy " + mayId);
            taiTrangThaiMay();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách khỏi máy: " + e.getMessage());
        }
    }
    private void moHopThoaiThemKhach(int mayId) {
        String tenKhach = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:");
        if (tenKhach != null && !tenKhach.trim().isEmpty()) {
            themKhachVaoMay(mayId, tenKhach);
        }
    }
    private void themKhachVaoMay(int mayId, String tenKhach) {
        try (Connection ketNoi = DatabaseConnection.getConnection()) {
            // Kiểm tra xem khách có tồn tại chưa
            int idKhach = -1;
            try (PreparedStatement kiemTra = ketNoi.prepareStatement("SELECT id FROM khach WHERE ten = ?")) {
                kiemTra.setString(1, tenKhach);
                ResultSet ketQua = kiemTra.executeQuery();
                if (ketQua.next()) {
                    idKhach = ketQua.getInt("id");
                } else {
                    try (PreparedStatement themKhach = ketNoi.prepareStatement(
                            "INSERT INTO khach (ten, so_du) VALUES (?, 12000)", Statement.RETURN_GENERATED_KEYS)) {
                        themKhach.setString(1, tenKhach);
                        themKhach.executeUpdate();
                        ResultSet khoaTuDong = themKhach.getGeneratedKeys();
                        if (khoaTuDong.next()) {
                            idKhach = khoaTuDong.getInt(1);
                        }
                    }
                }
            }
            if (idKhach != -1) {
                try (PreparedStatement kiemTraMay = ketNoi.prepareStatement(
                        "SELECT id FROM may WHERE id_khach = ?")) {
                    kiemTraMay.setInt(1, idKhach);
                    ResultSet ketQua = kiemTraMay.executeQuery();
                    if (ketQua.next()) {
                        int mayDangDung = ketQua.getInt("id");
                        JOptionPane.showMessageDialog(this,
                                "Khách hàng đang sử dụng máy " + mayDangDung + "\nMỗi khách chỉ được sử dụng một máy!");
                        return;
                    }
                }
                // Cập nhật máy với khách hàng mới
                try (PreparedStatement capNhatMay = ketNoi.prepareStatement(
                        "UPDATE may SET trang_thai = 'dang_su_dung', id_khach = ? WHERE id = ?")) {
                    capNhatMay.setInt(1, idKhach);
                    capNhatMay.setInt(2, mayId);
                    capNhatMay.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Đã thêm khách hàng " + tenKhach + " vào máy " + mayId);
                }
                taiTrangThaiMay();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }
    private void taiTrangThaiMay() {
        try (Connection ketNoi = DatabaseConnection.getConnection();
             Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery("SELECT id, trang_thai FROM may")) {
            while (ketQua.next()) {
                int id = ketQua.getInt("id") - 1;
                String trangThai = ketQua.getString("trang_thai");
                if ("dang_su_dung".equals(trangThai)) {
                    nutMay[id].setBackground(Color.RED);
                    nutMay[id].setForeground(Color.WHITE);
                } else {
                    nutMay[id].setBackground(Color.GREEN);
                    nutMay[id].setForeground(Color.BLACK);
                }
                nutMay[id].setOpaque(true);
                nutMay[id].setBorderPainted(false);
                nutMay[id].revalidate();
                nutMay[id].repaint();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void batDongHo() {
        dongHo = new Timer();
        dongHo.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Đang cập nhật trạng thái máy...");
                capNhatThoiGianSuDung();
            }
        }, 0, 60000);
    }
    private void capNhatThoiGianSuDung() {
        try (Connection ketNoi = DatabaseConnection.getConnection();
             Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery("SELECT id, id_khach FROM may WHERE trang_thai = 'dang_su_dung'")) {
            while (ketQua.next()) {
                int idMay = ketQua.getInt("id");
                int idKhach = ketQua.getInt("id_khach");

                try (PreparedStatement truTien = ketNoi.prepareStatement(
                        "UPDATE khach SET so_du = so_du - 200 WHERE id = ? AND so_du >= 200")) {
                    truTien.setInt(1, idKhach);
                    int hangAnhHuong = truTien.executeUpdate();
                    if (hangAnhHuong == 0) { // Nếu khách hết tiền
                        try (PreparedStatement datLaiMay = ketNoi.prepareStatement(
                                "UPDATE may SET trang_thai = 'trong', id_khach = NULL WHERE id = ?")) {
                            datLaiMay.setInt(1, idMay);
                            datLaiMay.executeUpdate();
                        }
                    }
                }
            }
            taiTrangThaiMay();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
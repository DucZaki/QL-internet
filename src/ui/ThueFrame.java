package ui;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class ThueFrame extends JPanel {
    private DefaultListModel<String> danhSachMay;
    private JList<String> listMay;
    private JTextField txtIdKhach;
    private JButton btnThue;
    public ThueFrame() {
        setLayout(new BorderLayout());
        JLabel lblTieuDe = new JLabel("Thuê Máy", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTieuDe, BorderLayout.NORTH);
        danhSachMay = new DefaultListModel<>();
        listMay = new JList<>(danhSachMay);
        add(new JScrollPane(listMay), BorderLayout.CENTER);
        // Thêm panel cho ID khách
        JPanel panelKhach = new JPanel(new BorderLayout());
        panelKhach.add(new JLabel("ID Khách: "), BorderLayout.WEST);
        txtIdKhach = new JTextField();
        panelKhach.add(txtIdKhach, BorderLayout.CENTER);
        // Thêm key listener cho trường ID khách
        txtIdKhach.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    thueMay();
                }
            }
        });
        btnThue = new JButton("Thuê Máy");
        btnThue.addActionListener(e -> thueMay());
        panelKhach.add(btnThue, BorderLayout.EAST);
        add(panelKhach, BorderLayout.SOUTH);
        taiDanhSachMay();
    }
    private void taiDanhSachMay() {
        danhSachMay.clear();
        try (Connection ketNoi = DatabaseConnection.getConnection();
             Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery("SELECT id, trang_thai FROM may WHERE trang_thai = 'trong'")) {
            while (ketQua.next()) {
                int id = ketQua.getInt("id");
                danhSachMay.addElement(id + " - Trống");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void thueMay() {
        String idKhach = txtIdKhach.getText().trim();
        String mayDuocChon = listMay.getSelectedValue();
        if (idKhach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID khách hàng!");
            return;
        }
        if (mayDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một máy từ danh sách!");
            return;
        }
        int idMay = Integer.parseInt(mayDuocChon.split(" - ")[0]);
        try (Connection ketNoi = DatabaseConnection.getConnection();
             PreparedStatement capNhatMay = ketNoi.prepareStatement("UPDATE may SET trang_thai = 'dang_su_dung', id_khach = ? WHERE id = ?");
             PreparedStatement kiemTraTien = ketNoi.prepareStatement("SELECT so_du FROM khach WHERE id = ?")) {
            kiemTraTien.setInt(1, Integer.parseInt(idKhach));
            ResultSet ketQua = kiemTraTien.executeQuery();
            if (ketQua.next() && ketQua.getInt("so_du") < 12000) {
                JOptionPane.showMessageDialog(this, "Khách không đủ tiền để thuê máy!");
                return;
            }
            capNhatMay.setInt(1, Integer.parseInt(idKhach));
            capNhatMay.setInt(2, idMay);
            capNhatMay.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thuê máy thành công!");
            // Xóa nội dung trường ID khách sau khi thuê thành công
            txtIdKhach.setText("");
            taiDanhSachMay();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID khách không hợp lệ, vui lòng nhập số!");
        }
    }
}
package ui;

import database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

public class ThongKeFrame extends JPanel {
    private JLabel lblTongNapTien;
    private JComboBox<String> cboThoiGian;
    public ThongKeFrame() {
        setLayout(new BorderLayout(10, 10));
        JLabel lblTitle = new JLabel("Thống kê Nạp Tiền", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);
        // Panel chứa thông tin thống kê
        JPanel panelThongTin = new JPanel(new GridLayout(1, 1, 10, 10));
        panelThongTin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblTongNapTien = new JLabel("Tổng tiền nạp: 0 VNĐ");
        lblTongNapTien.setFont(new Font("Arial", Font.PLAIN, 14));
        panelThongTin.add(lblTongNapTien);
        add(panelThongTin, BorderLayout.CENTER);
        // Panel chứa điều khiển
        JPanel panelDieuKhien = new JPanel(new BorderLayout(5, 0));
        panelDieuKhien.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        JPanel panelLoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblThoiGian = new JLabel("Thời gian: ");
        cboThoiGian = new JComboBox<>(new String[]{"Hôm nay", "Tuần này", "Tháng này", "Tất cả"});
        cboThoiGian.addActionListener(e -> loadThongKe());
        panelLoc.add(lblThoiGian);
        panelLoc.add(cboThoiGian);
        JButton btnTaiLai = new JButton("Tải lại");
        btnTaiLai.addActionListener(e -> loadThongKe());
        panelDieuKhien.add(panelLoc, BorderLayout.WEST);
        panelDieuKhien.add(btnTaiLai, BorderLayout.EAST);
        add(panelDieuKhien, BorderLayout.SOUTH);
        loadThongKe();
    }
    private void loadThongKe() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String dieuKienThoiGian = "";
            String thoiGianDuocChon = cboThoiGian.getSelectedItem().toString();
            if ("Hôm nay".equals(thoiGianDuocChon)) {
                dieuKienThoiGian = " WHERE DATE(thoi_gian) = CURRENT_DATE";
            } else if ("Tuần này".equals(thoiGianDuocChon)) {
                dieuKienThoiGian = " WHERE YEARWEEK(thoi_gian) = YEARWEEK(CURRENT_DATE)";
            } else if ("Tháng này".equals(thoiGianDuocChon)) {
                dieuKienThoiGian = " WHERE MONTH(thoi_gian) = MONTH(CURRENT_DATE) AND YEAR(thoi_gian) = YEAR(CURRENT_DATE)";
            }
            // Truy vấn tổng tiền nạp
            int tongTienNap = 0;
            String sql = "SELECT COALESCE(SUM(so_tien), 0) AS tong_tien FROM nap_tien" + dieuKienThoiGian;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    tongTienNap = rs.getInt("tong_tien");
                }
            }
            NumberFormat formatTien = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            lblTongNapTien.setText("Tổng tiền nạp: " + formatTien.format(tongTienNap) + " VNĐ");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải thống kê: " + e.getMessage());
        }
    }
}

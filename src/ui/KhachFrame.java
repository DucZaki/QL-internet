package ui;

import database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class KhachFrame extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtTen, txtSoDienThoai, txtMatKhau, txtSoTien, txtTimKiem;
    private JButton btnThem, btnNapTien, btnTimKiem;

    public KhachFrame() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID", "Tên", "Số Điện Thoại", "Máy", "Số Dư"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel panelInput = new JPanel(new GridLayout(5, 2));
        panelInput.add(new JLabel("Tên khách:"));
        txtTen = new JTextField();
        panelInput.add(txtTen);
        panelInput.add(new JLabel("Số điện thoại:"));
        txtSoDienThoai = new JTextField();
        panelInput.add(txtSoDienThoai);
        panelInput.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JTextField();
        panelInput.add(txtMatKhau);
        btnThem = new JButton("Thêm khách");
        btnNapTien = new JButton("Nạp tiền");
        panelInput.add(btnThem);
        panelInput.add(btnNapTien);
        add(panelInput, BorderLayout.SOUTH);
        JPanel panelSearch = new JPanel(new BorderLayout());
        txtTimKiem = new JTextField();
        btnTimKiem = new JButton("Tìm kiếm");
        panelSearch.add(new JLabel("Nhập ID hoặc Tên:"), BorderLayout.WEST);
        panelSearch.add(txtTimKiem, BorderLayout.CENTER);
        panelSearch.add(btnTimKiem, BorderLayout.EAST);
        add(panelSearch, BorderLayout.NORTH);
        setupKeyListeners();
        btnThem.addActionListener(e -> themKhach());
        btnNapTien.addActionListener(e -> napTien());
        btnTimKiem.addActionListener(e -> timKiemKhach());
        // Thêm sự kiện khi nhấp vào dòng trong bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    xuLyClickKhach();
                }
            }
        });
        loadKhach();
    }
    // Thiết lập key listeners cho các trường nhập liệu
    private void setupKeyListeners() {
        // Key listener cho ô tìm kiếm
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemKhach();
                }
            }
        });

        // Key listener cho ô mật khẩu (khi nhấn Enter sẽ thêm khách)
        txtMatKhau.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    themKhach();
                }
            }
        });
        txtSoDienThoai.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtMatKhau.requestFocus();
                }
            }
        });
        // Key listener cho ô tên (focus sang ô số điện thoại)
        txtTen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtSoDienThoai.requestFocus();
                }
            }
        });
    }
    // Phương thức xử lý khi click vào khách hàng
    private void xuLyClickKhach() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int idKhach = (int) model.getValueAt(selectedRow, 0);
        String tenKhach = (String) model.getValueAt(selectedRow, 1);
        String[] options = {"Chỉnh sửa khách", "Xóa khách", "Hủy"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Bạn muốn thực hiện thao tác gì với khách hàng " + tenKhach + "?",
                "Quản lý khách hàng",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        switch (choice) {
            case 0: // Chỉnh sửa khách
                chinhSuaKhach(idKhach);
                break;
            case 1: // Xóa khách
                xoaKhach(idKhach, tenKhach);
                break;
            default: // Hủy
                break;
        }
    }
    // Phương thức chỉnh sửa thông tin khách hàng
    private void chinhSuaKhach(int idKhach) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM khach WHERE id = ?")) {
            stmt.setInt(1, idKhach);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String tenHienTai = rs.getString("ten");
                String soDienThoaiHienTai = rs.getString("so_dien_thoai");
                JTextField txtSuaTen = new JTextField(tenHienTai);
                JTextField txtSuaSoDienThoai = new JTextField(soDienThoaiHienTai);
                JTextField txtSuaMatKhau = new JTextField();
                JPanel panel = new JPanel(new GridLayout(3, 2));
                panel.add(new JLabel("Tên:"));
                panel.add(txtSuaTen);
                panel.add(new JLabel("Số điện thoại:"));
                panel.add(txtSuaSoDienThoai);
                panel.add(new JLabel("Mật khẩu mới (để trống nếu không đổi):"));
                panel.add(txtSuaMatKhau);
                // Thêm key listener để có thể nhấn Enter để lưu
                KeyAdapter enterKeyListener = new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            // Mô phỏng người dùng đã nhấn OK
                            luuChinhSuaKhach(idKhach, txtSuaTen.getText().trim(),
                                    txtSuaSoDienThoai.getText().trim(),
                                    txtSuaMatKhau.getText().trim());
                        }
                    }
                };
                txtSuaTen.addKeyListener(enterKeyListener);
                txtSuaSoDienThoai.addKeyListener(enterKeyListener);
                txtSuaMatKhau.addKeyListener(enterKeyListener);
                int result = JOptionPane.showConfirmDialog(this, panel, "Chỉnh sửa thông tin khách hàng",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    luuChinhSuaKhach(idKhach, txtSuaTen.getText().trim(),
                            txtSuaSoDienThoai.getText().trim(),
                            txtSuaMatKhau.getText().trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin khách hàng: " + e.getMessage());
        }
    }
    private void luuChinhSuaKhach(int idKhach, String tenMoi, String soDienThoaiMoi, String matKhauMoi) {
        if (tenMoi.isEmpty() || soDienThoaiMoi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và số điện thoại không được để trống!");
            return;
        }
        if (!soDienThoaiMoi.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            PreparedStatement updateStmt;
            if (matKhauMoi.isEmpty()) {
                sql = "UPDATE khach SET ten = ?, so_dien_thoai = ? WHERE id = ?";
                updateStmt = conn.prepareStatement(sql);
                updateStmt.setString(1, tenMoi);
                updateStmt.setString(2, soDienThoaiMoi);
                updateStmt.setInt(3, idKhach);
            } else {
                sql = "UPDATE khach SET ten = ?, so_dien_thoai = ?, mat_khau = ? WHERE id = ?";
                updateStmt = conn.prepareStatement(sql);
                updateStmt.setString(1, tenMoi);
                updateStmt.setString(2, soDienThoaiMoi);
                updateStmt.setString(3, matKhauMoi);
                updateStmt.setInt(4, idKhach);
            }
            updateStmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công!");
            loadKhach();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin khách hàng: " + e.getMessage());
        }
    }
    // Phương thức xóa khách hàng
    private void xoaKhach(int idKhach, String tenKhach) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khách hàng " + tenKhach + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                // Kiểm tra xem khách có đang sử dụng máy không
                try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM may WHERE id_khach = ?")) {
                    checkStmt.setInt(1, idKhach);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                                "Không thể xóa khách hàng đang sử dụng máy!\nHãy kết thúc phiên sử dụng máy trước.");
                        return;
                    }
                }
                // Xóa lịch sử nạp tiền của khách
                try (PreparedStatement deleteHistoryStmt = conn.prepareStatement("DELETE FROM nap_tien WHERE id_khach = ?")) {
                    deleteHistoryStmt.setInt(1, idKhach);
                    deleteHistoryStmt.executeUpdate();
                }
                // Xóa khách
                try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM khach WHERE id = ?")) {
                    deleteStmt.setInt(1, idKhach);
                    int rowsAffected = deleteStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Đã xóa khách hàng thành công!");
                        loadKhach();
                    } else {
                        JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng!");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa khách hàng: " + e.getMessage());
            }
        }
    }
    private void themKhach() {
        String ten = txtTen.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String matKhau = txtMatKhau.getText().trim();
        if (ten.isEmpty() || soDienThoai.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        if (!soDienThoai.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM khach WHERE so_dien_thoai = ? OR ten = ?")) {
            checkStmt.setString(1, soDienThoai);
            checkStmt.setString(2, ten);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Tên hoặc số điện thoại đã tồn tại! Vui lòng nhập thông tin khác.");
                return;
            }
            String query = "INSERT INTO khach (ten, so_dien_thoai, mat_khau, so_du) VALUES (?, ?, ?, 0)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, ten);
                stmt.setString(2, soDienThoai);
                stmt.setString(3, matKhau);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Thêm khách thành công!");

                // Xóa thông tin đã nhập và đặt con trỏ về ô nhập tên
                txtTen.setText("");
                txtSoDienThoai.setText("");
                txtMatKhau.setText("");
                txtTen.requestFocus();

                loadKhach();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách: " + e.getMessage());
        }
    }
    private void napTien() {
        String tenKhach = JOptionPane.showInputDialog(this, "Nhập tên khách hàng:");
        if (tenKhach == null || tenKhach.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!");
            return;
        }
        String soTienStr = JOptionPane.showInputDialog(this, "Nhập số tiền cần nạp:");
        int soTienNap;
        try {
            soTienNap = Integer.parseInt(soTienStr.trim());
            if (soTienNap <= 0 || soTienNap > 50000000) {
                JOptionPane.showMessageDialog(this, "Số tiền nạp phải lớn hơn 0 và nhỏ hơn 50 triệu!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền hợp lệ!");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            // Kiểm tra khách có tồn tại không
            int idKhach = -1;
            int soDuHienTai = 0;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT id, so_du FROM khach WHERE ten = ?")) {
                stmt.setString(1, tenKhach);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idKhach = rs.getInt("id");
                    soDuHienTai = rs.getInt("so_du");
                } else {
                    JOptionPane.showMessageDialog(this, "Khách hàng không tồn tại!");
                    return;
                }
            }
            // Cập nhật số dư
            int soDuMoi = soDuHienTai + soTienNap;
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE khach SET so_du = ? WHERE id = ?")) {
                stmt.setInt(1, soDuMoi);
                stmt.setInt(2, idKhach);
                stmt.executeUpdate();
            }
            // Ghi vào lịch sử nạp tiền
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO nap_tien (id_khach, so_tien, thoi_gian) VALUES (?, ?, NOW())")) {
                stmt.setInt(1, idKhach);
                stmt.setInt(2, soTienNap);
                stmt.executeUpdate();
            }
            conn.commit();
            JOptionPane.showMessageDialog(this, "Nạp tiền thành công! Số dư mới: " + soDuMoi + " VNĐ");
            loadKhach(); // Cập nhật lại bảng
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi nạp tiền: " + e.getMessage());
        }
    }
    private void timKiemKhach() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            loadKhach();
            return;
        }
        model.setRowCount(0);
        String query = "SELECT k.id, k.ten, k.so_dien_thoai, COALESCE(m.id, 'Chưa gán') AS may_id, k.so_du " +
                "FROM khach k LEFT JOIN may m ON k.id = m.id_khach " +
                "WHERE k.id = ? OR k.ten LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            try {
                int id = Integer.parseInt(keyword);
                stmt.setInt(1, id);
            } catch (NumberFormatException e) {
                stmt.setInt(1, -1); // Giá trị không hợp lệ để tránh lỗi
            }
            stmt.setString(2, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("ten"), rs.getString("so_dien_thoai"), rs.getString("may_id"), rs.getInt("so_du")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadKhach() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT k.id, k.ten, k.so_dien_thoai, COALESCE(m.id, 'Chưa gán') AS may_id, k.so_du FROM khach k LEFT JOIN may m ON k.id = m.id_khach")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("ten"), rs.getString("so_dien_thoai"), rs.getString("may_id"), rs.getInt("so_du")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
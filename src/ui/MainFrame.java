package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Phần mềm Quản lý Quán Internet");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo thanh menu
        JMenuBar thanhMenu = new JMenuBar();
        JMenu menuQuanLy = new JMenu("Quản lý");
        JMenuItem menuMay = new JMenuItem("Quản lý Máy");
        JMenuItem menuKhach = new JMenuItem("Quản lý Khách");
        JMenuItem menuThue = new JMenuItem("Thuê Máy");
        JMenuItem menuThongKe = new JMenuItem("Thống kê");

        // Thêm các mục vào menu quản lý
        menuQuanLy.add(menuMay);
        menuQuanLy.add(menuKhach);
        menuQuanLy.add(menuThue);
        menuQuanLy.add(menuThongKe);
        thanhMenu.add(menuQuanLy);
        setJMenuBar(thanhMenu);

        // Tạo giao diện chính
        JPanel panelChinh = new JPanel(new BorderLayout());
        add(panelChinh, BorderLayout.CENTER);
        hienThiManHinhChao(panelChinh);
        // Thiết lập sự kiện khi nhấn vào menu
        menuMay.addActionListener(e -> hienThiPanel(panelChinh, new MayFrame()));
        menuKhach.addActionListener(e -> hienThiPanel(panelChinh, new KhachFrame()));
        menuThue.addActionListener(e -> hienThiPanel(panelChinh, new ThueFrame()));
        menuThongKe.addActionListener(e -> hienThiPanel(panelChinh, new ThongKeFrame()));

        setVisible(true);
    }
    private void hienThiManHinhChao(JPanel panel) {
        hienThiPanel(panel, new JLabel("Chào mừng đến với phần mềm Quản lý Quán Internet", SwingConstants.CENTER));
    }
    private void hienThiPanel(JPanel panel, JComponent thanhPhan) {
        panel.removeAll();
        panel.add(thanhPhan, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

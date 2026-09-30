package com.nhom.quanlyvexe.network.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * CHƯƠNG TRÌNH CLIENT TÁCH BIỆT (TIÊU CHÍ 5.3 & 5.4 - LẬP TRÌNH MẠNG)
 * Phụ trách: Trần Phan Minh Trí (MSSV: 2001240514)
 *
 * Ứng dụng Desktop Java Swing chạy độc lập trên NetBeans IDE.
 * Dùng cho nhân viên tại quầy bến xe hoặc cửa xe để tra cứu và soát vé qua Socket TCP.
 */
public class TicketInspectorClientApp extends JFrame {

    private JTextField txtHost;
    private JTextField txtPort;
    private JButton btnConnect;
    private JLabel lblStatus;

    private JTextField txtMaVe;
    private JButton btnSearch;
    private JButton btnCheckin;

    private JLabel lblResultMaVe;
    private JLabel lblResultKhach;
    private JLabel lblResultSdt;
    private JLabel lblResultTuyen;
    private JLabel lblResultGhe;
    private JLabel lblResultGia;
    private JLabel lblResultTrangThai;

    private JTextArea txtLogs;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isConnected = false;

    public TicketInspectorClientApp(String clientTitle) {
        super(clientTitle);
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 1. Panel Cấu hình kết nối
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(new TitledBorder("Cấu hình kết nối Socket Server"));
        topPanel.add(new JLabel("Host:"));
        txtHost = new JTextField("localhost", 10);
        topPanel.add(txtHost);

        topPanel.add(new JLabel("Port:"));
        txtPort = new JTextField("8888", 5);
        topPanel.add(txtPort);

        btnConnect = new JButton("Kết nối Server");
        btnConnect.setBackground(new Color(13, 110, 253));
        btnConnect.setForeground(Color.WHITE);
        btnConnect.setFocusPainted(false);
        btnConnect.addActionListener(e -> toggleConnection());
        topPanel.add(btnConnect);

        lblStatus = new JLabel("● Chưa kết nối");
        lblStatus.setForeground(Color.RED);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        topPanel.add(lblStatus);

        add(topPanel, BorderLayout.NORTH);

        // 2. Panel Trung tâm: Tìm kiếm và Chi tiết vé
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Thanh tìm kiếm mã vé
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(new TitledBorder("Tra cứu mã vé"));
        searchPanel.add(new JLabel("Nhập mã vé:"));
        txtMaVe = new JTextField("VE2026110101", 16);
        txtMaVe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(txtMaVe);

        btnSearch = new JButton("Tra Cứu Vé");
        btnSearch.setBackground(new Color(25, 135, 84));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setEnabled(false);
        btnSearch.addActionListener(e -> sendCheckCommand());
        searchPanel.add(btnSearch);

        btnCheckin = new JButton("✔ XÁC NHẬN CHO LÊN XE");
        btnCheckin.setBackground(new Color(255, 107, 53));
        btnCheckin.setForeground(Color.WHITE);
        btnCheckin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCheckin.setEnabled(false);
        btnCheckin.addActionListener(e -> sendCheckinCommand());
        searchPanel.add(btnCheckin);

        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Chi tiết thông tin vé
        JPanel detailPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        detailPanel.setBorder(new TitledBorder("Thông tin vé xe tra cứu"));
        detailPanel.setBackground(Color.WHITE);

        detailPanel.add(new JLabel(" Mã vé:"));
        lblResultMaVe = new JLabel("---");
        lblResultMaVe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailPanel.add(lblResultMaVe);

        detailPanel.add(new JLabel(" Họ tên hành khách:"));
        lblResultKhach = new JLabel("---");
        lblResultKhach.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailPanel.add(lblResultKhach);

        detailPanel.add(new JLabel(" Số điện thoại:"));
        lblResultSdt = new JLabel("---");
        detailPanel.add(lblResultSdt);

        detailPanel.add(new JLabel(" Tuyến đường:"));
        lblResultTuyen = new JLabel("---");
        lblResultTuyen.setForeground(new Color(13, 110, 253));
        detailPanel.add(lblResultTuyen);

        detailPanel.add(new JLabel(" Vị trí ghế:"));
        lblResultGhe = new JLabel("---");
        lblResultGhe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblResultGhe.setForeground(new Color(217, 119, 6));
        detailPanel.add(lblResultGhe);

        detailPanel.add(new JLabel(" Giá vé:"));
        lblResultGia = new JLabel("---");
        detailPanel.add(lblResultGia);

        detailPanel.add(new JLabel(" Trạng thái vé:"));
        lblResultTrangThai = new JLabel("---");
        lblResultTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailPanel.add(lblResultTrangThai);

        centerPanel.add(detailPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Panel Dưới cùng: Nhật ký trao đổi dữ liệu qua mạng Socket
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new TitledBorder("Nhật ký gói tin Socket Client <-> Server (Tiêu chí 5.3)"));
        txtLogs = new JTextArea(8, 50);
        txtLogs.setEditable(false);
        txtLogs.setBackground(new Color(30, 41, 59));
        txtLogs.setForeground(new Color(148, 163, 184));
        txtLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        bottomPanel.add(new JScrollPane(txtLogs), BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Xử lý đóng kết nối an toàn khi tắt app (Rubric 5.4)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnectServer();
            }
        });
    }

    private void toggleConnection() {
        if (!isConnected) {
            connectServer();
        } else {
            disconnectServer();
        }
    }

    private void connectServer() {
        try {
            String host = txtHost.getText().trim();
            int port = Integer.parseInt(txtPort.getText().trim());

            log("[CLIENT] Đang kết nối tới Socket Server " + host + ":" + port + "...");
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String welcome = reader.readLine();
            log("[SERVER PHẢN HỒI] " + welcome);

            isConnected = true;
            btnConnect.setText("Ngắt kết nối");
            btnConnect.setBackground(new Color(220, 53, 69));
            lblStatus.setText("● Đã kết nối Server");
            lblStatus.setForeground(new Color(25, 135, 84));
            btnSearch.setEnabled(true);
            btnCheckin.setEnabled(true);
        } catch (Exception ex) {
            log("[LỖI KẾT NỐI] " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến Server: " + ex.getMessage(), "Lỗi Socket", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectServer() {
        if (isConnected) {
            try {
                if (writer != null) {
                    writer.println("QUIT");
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                log("[CLIENT] Đã ngắt kết nối an toàn với Server.");
            } catch (Exception ignored) {}
            isConnected = false;
            btnConnect.setText("Kết nối Server");
            btnConnect.setBackground(new Color(13, 110, 253));
            lblStatus.setText("● Chưa kết nối");
            lblStatus.setForeground(Color.RED);
            btnSearch.setEnabled(false);
            btnCheckin.setEnabled(false);
        }
    }

    private void sendCheckCommand() {
        String maVe = txtMaVe.getText().trim();
        if (maVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã vé cần tra cứu!");
            return;
        }

        try {
            log(">>> [GỬI LỆNH] CHECK " + maVe);
            writer.println("CHECK " + maVe);

            String response = reader.readLine();
            log("<<< [NHẬN TỪ SERVER] " + response);

            if (response != null && response.startsWith("FOUND|")) {
                String[] parts = response.split("\\|");
                lblResultMaVe.setText(parts[1]);
                lblResultKhach.setText(parts[2]);
                lblResultSdt.setText(parts[3]);
                lblResultTuyen.setText(parts[4]);
                lblResultGhe.setText(parts[5]);
                lblResultTrangThai.setText(parts[6]);
                lblResultGia.setText(parts[7] + " VNĐ");

                if ("Đã lên xe".equals(parts[6])) {
                    lblResultTrangThai.setForeground(new Color(13, 110, 253));
                } else if ("Đã thanh toán".equals(parts[6])) {
                    lblResultTrangThai.setForeground(new Color(25, 135, 84));
                } else {
                    lblResultTrangThai.setForeground(Color.RED);
                }
            } else {
                clearResults();
                JOptionPane.showMessageDialog(this, "Không tìm thấy vé mã " + maVe, "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            log("[LỖI TRA CỨU] " + ex.getMessage());
        }
    }

    private void sendCheckinCommand() {
        String maVe = txtMaVe.getText().trim();
        if (maVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã vé cần soát!");
            return;
        }

        try {
            log(">>> [GỬI LỆNH] CHECKIN " + maVe);
            writer.println("CHECKIN " + maVe);

            String response = reader.readLine();
            log("<<< [NHẬN TỪ SERVER] " + response);

            if (response != null && response.startsWith("SUCCESS|")) {
                String msg = response.substring(8);
                lblResultTrangThai.setText("Đã lên xe");
                lblResultTrangThai.setForeground(new Color(13, 110, 253));
                JOptionPane.showMessageDialog(this, msg, "Soát vé thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String errMsg = response != null ? response.substring(response.indexOf('|') + 1) : "Lỗi không xác định";
                JOptionPane.showMessageDialog(this, errMsg, "Lỗi soát vé", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log("[LỖI SOÁT VÉ] " + ex.getMessage());
        }
    }

    private void clearResults() {
        lblResultMaVe.setText("---");
        lblResultKhach.setText("---");
        lblResultSdt.setText("---");
        lblResultTuyen.setText("---");
        lblResultGhe.setText("---");
        lblResultGia.setText("---");
        lblResultTrangThai.setText("---");
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            txtLogs.append(message + "\n");
            txtLogs.setCaretPosition(txtLogs.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        // Có thể chạy trực tiếp từ NetBeans (Shift + F6) để demo Client
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TicketInspectorClientApp("ỨNG DỤNG SOÁT VÉ XE KHÁCH - CLIENT LẬP TRÌNH MẠNG").setVisible(true);
        });
    }
}

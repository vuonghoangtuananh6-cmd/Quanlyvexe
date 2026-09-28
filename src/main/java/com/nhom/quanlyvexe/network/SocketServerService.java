package com.nhom.quanlyvexe.network;

import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MODULE LẬP TRÌNH MẠNG (TIÊU CHÍ 5.3 & 5.4 - RUBRIC ĐỒ ÁN)
 * Phụ trách: Trần Phan Minh Trí (MSSV: 2001240514)
 *
 * ServerSocket đa luồng (Thread Pool) phục vụ đồng thời >= 3 Client ứng dụng Desktop Swing.
 * Cho phép nhân viên quầy hoặc nhân viên cửa xe tra cứu và soát vé trực tiếp vào CSDL.
 */
@Component
public class SocketServerService implements CommandLineRunner {

    @Value("${socket.server.port:8888}")
    private int port;

    @Autowired
    private VeXeRepository veXeRepository;

    private ServerSocket serverSocket;
    private final ExecutorService clientThreadPool = Executors.newFixedThreadPool(15);
    private final AtomicInteger activeClients = new AtomicInteger(0);
    private volatile boolean isRunning = true;

    @Override
    public void run(String... args) {
        // Chạy ServerSocket trên một luồng nền để không làm nghẽn quá trình khởi động của Spring Boot
        new Thread(this::startSocketServer, "Socket-Server-Listener").start();
    }

    private void startSocketServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("==========================================================================");
            System.out.println("🚀 [SOCKET SERVER ĐA LUỒNG] Đang lắng nghe kết nối tại cổng: " + port);
            System.out.println("   Sẵn sàng phục vụ đồng thời nhiều Client Desktop cùng lúc...");
            System.out.println("==========================================================================");

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    int clientCount = activeClients.incrementAndGet();
                    String clientInfo = clientSocket.getRemoteSocketAddress().toString();

                    System.out.println("📡 [CLIENT KẾT NỐI] Client mới từ: " + clientInfo + " (Tổng client đang kết nối: " + clientCount + ")");

                    // Đẩy xử lý client vào ThreadPool để phục vụ đồng thời không nghẽn (Rubric 5.4)
                    clientThreadPool.execute(new ClientHandler(clientSocket));
                } catch (Exception e) {
                    if (!isRunning) break;
                    System.err.println("Lỗi chấp nhận client: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Không thể khởi động Socket Server: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopSocketServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            clientThreadPool.shutdown();
            System.out.println("🛑 [SOCKET SERVER] Đã đóng tài nguyên mạng thành công.");
        } catch (Exception e) {
            System.err.println("Lỗi đóng socket: " + e.getMessage());
        }
    }

    /**
     * Lớp xử lý từng Client trên một luồng riêng biệt
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clientInfo = socket.getRemoteSocketAddress().toString();
            String threadName = Thread.currentThread().getName();

            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
            ) {
                writer.println("CONNECTED|Chào mừng bạn đến với Hệ thống Soát vé Xe khách (Luồng phục vụ: " + threadName + ")");

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    System.out.println("📩 [NHẬN TỪ CLIENT " + clientInfo + "] Lệnh: " + line);

                    if (line.equalsIgnoreCase("QUIT")) {
                        writer.println("BYE|Đã đóng kết nối an toàn.");
                        break;
                    }

                    // Giao thức: CHECK <maVe>
                    if (line.startsWith("CHECK ")) {
                        String maVe = line.substring(6).trim();
                        Optional<VeXe> veOpt = veXeRepository.findByMaVe(maVe);

                        if (veOpt.isPresent()) {
                            VeXe v = veOpt.get();
                            String response = String.format("FOUND|%s|%s|%s|%s|%s|%s|%s",
                                    v.getMaVe(),
                                    v.getTenKhachHang(),
                                    v.getSoDienThoai(),
                                    v.getChuyenXe().getTuyenXe().getTenTuyen(),
                                    v.getSoGhe(),
                                    v.getTrangThaiThanhToan(),
                                    v.getGiaVe().toString());
                            writer.println(response);
                        } else {
                            writer.println("NOT_FOUND|Không tìm thấy thông tin vé mã: " + maVe);
                        }
                    }
                    // Giao thức: CHECKIN <maVe>
                    else if (line.startsWith("CHECKIN ")) {
                        String maVe = line.substring(8).trim();
                        Optional<VeXe> veOpt = veXeRepository.findByMaVe(maVe);

                        if (veOpt.isPresent()) {
                            VeXe v = veOpt.get();
                            if ("Đã hủy".equals(v.getTrangThaiThanhToan())) {
                                writer.println("ERROR|Vé này đã bị hủy, không thể cho lên xe!");
                            } else if ("Đã lên xe".equals(v.getTrangThaiThanhToan())) {
                                writer.println("ERROR|Vé này đã được soát trước đó rồi!");
                            } else {
                                v.setTrangThaiThanhToan("Đã lên xe");
                                veXeRepository.save(v);
                                writer.println("SUCCESS|Xác nhận hành khách " + v.getTenKhachHang() + " (Ghế " + v.getSoGhe() + ") LÊN XE THÀNH CÔNG!");
                                System.out.println("✅ [SOÁT VÉ XONG] Vé " + maVe + " đã đổi trạng thái sang 'Đã lên xe'");
                            }
                        } else {
                            writer.println("NOT_FOUND|Mã vé không tồn tại trong CSDL.");
                        }
                    } else {
                        writer.println("UNKNOWN_COMMAND|Lệnh không hợp lệ. Hỗ trợ: CHECK <maVe>, CHECKIN <maVe>, QUIT");
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ [CLIENT NGẮT KẾT NỐI] Client " + clientInfo + ": " + e.getMessage());
            } finally {
                int clientCount = activeClients.decrementAndGet();
                try {
                    if (!socket.isClosed()) socket.close();
                } catch (Exception ignored) {}
                System.out.println("🔌 [ĐÓNG KẾT NỐI CLIENT] " + clientInfo + " (Còn lại: " + clientCount + " client)");
            }
        }
    }
}

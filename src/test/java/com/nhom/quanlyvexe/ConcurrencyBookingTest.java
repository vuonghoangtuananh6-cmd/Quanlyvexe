package com.nhom.quanlyvexe;

import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import com.nhom.quanlyvexe.service.VeXeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * KIỂM THỬ XỬ LÝ ĐỒNG THỜI (TIÊU CHÍ 5.2 - RUBRIC ĐỒ ÁN)
 * Phụ trách: Trần Gia Phú (MSSV: 2001230672)
 *
 * Kịch bản: Giả lập 10 khách hàng (10 luồng song song) cùng bấm ĐẶT CHỖ
 * cho đúng 1 vị trí ghế (Ghế VIP A05 trên Chuyến xe ID 1) tại cùng 1 phần triệu giây.
 *
 * Kết quả mong đợi:
 * - Đúng 1 luồng đặt vé THÀNH CÔNG.
 * - 9 luồng còn lại bị TỪ CHỐI do xung đột và nhận thông báo lỗi ghế đã có người đặt.
 * - Cơ sở dữ liệu hoàn toàn nhất quán, không xảy ra hiện tượng bán trùng vé (Double Booking).
 */
@SpringBootTest
public class ConcurrencyBookingTest {

    @Autowired
    private VeXeService veXeService;

    @Autowired
    private VeXeRepository veXeRepository;

    @BeforeEach
    void setUp() {
        // Dọn dẹp dữ liệu ghế A05 từ lần test trước để bài test luôn chạy độc lập và chính xác (Idempotent)
        List<VeXe> existing = veXeRepository.findAll();
        for (VeXe v : existing) {
            if (v.getChuyenXe() != null && v.getChuyenXe().getId().equals(1L) && "A05".equals(v.getSoGhe())) {
                veXeRepository.delete(v);
            }
        }
    }

    @Test
    @DisplayName("Kiểm thử đặt vé đồng thời với 10 luồng song song")
    void testDatVeDongThoiChongTrungGhe() throws InterruptedException {
        int soLuongLuong = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(soLuongLuong);
        CountDownLatch readyLatch = new CountDownLatch(soLuongLuong);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(soLuongLuong);

        AtomicInteger soLuongThanhCong = new AtomicInteger(0);
        AtomicInteger soLuongThatBai = new AtomicInteger(0);

        Long testChuyenXeId = 1L;
        String testSoGhe = "A05";

        System.out.println("=================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ ĐỒNG THỜI: 10 LUỒNG CÙNG ĐẶT GHẾ " + testSoGhe);
        System.out.println("=================================================================");

        for (int i = 1; i <= soLuongLuong; i++) {
            final int khachId = i;
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await(); // Chờ tất cả 10 luồng sẵn sàng rồi cùng xuất phát 1 lúc

                    String threadName = Thread.currentThread().getName();
                    System.out.println("👉 [LUỒNG " + threadName + "] Khách hàng #" + khachId + " đang gửi yêu cầu đặt vé...");

                    veXeService.datVeDongThoi(
                            testChuyenXeId,
                            testSoGhe,
                            "Khách hàng test " + khachId,
                            "090000000" + khachId,
                            "test" + khachId + "@example.com",
                            null
                    );

                    soLuongThanhCong.incrementAndGet();
                    System.out.println("✅ [LUỒNG " + threadName + "] Khách #" + khachId + " ĐẶT THÀNH CÔNG!");
                } catch (Exception e) {
                    soLuongThatBai.incrementAndGet();
                    System.out.println("❌ [LUỒNG " + Thread.currentThread().getName() + "] Khách #" + khachId + " THẤT BẠI: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(); // Đợi cả 10 luồng khởi động
        startLatch.countDown(); // Bắn tín hiệu cho 10 luồng chạy cùng tích tắc
        doneLatch.await(); // Đợi cả 10 luồng kết thúc
        executorService.shutdown();

        System.out.println("=================================================================");
        System.out.println(" KẾT QUẢ KIỂM THỬ XỬ LÝ ĐỒNG THỜI:");
        System.out.println(" - Số luồng thành công : " + soLuongThanhCong.get() + " (Kỳ vọng: 1)");
        System.out.println(" - Số luồng thất bại   : " + soLuongThatBai.get() + " (Kỳ vọng: 9)");
        System.out.println("=================================================================");

        assertEquals(1, soLuongThanhCong.get(), "Chỉ được phép có duy nhất 1 luồng đặt thành công!");
        assertEquals(9, soLuongThatBai.get(), "9 luồng còn lại phải bị từ chối!");
    }
}

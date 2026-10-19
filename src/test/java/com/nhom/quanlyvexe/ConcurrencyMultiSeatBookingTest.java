package com.nhom.quanlyvexe;

import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import com.nhom.quanlyvexe.service.VeXeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * KIỂM THỬ XỬ LÝ ĐỒNG THỜI KHI ĐẶT NHIỀU GHẾ CÙNG LÚC (MULTI-SEAT CONCURRENCY TEST)
 * Kịch bản: 2 khách hàng đặt vé cùng 1 tích tắc:
 * - Khách 1: Đặt 2 ghế ["A08", "A09"]
 * - Khách 2: Đặt 2 ghế ["A09", "A10"]
 * (Xung đột tại ghế A09).
 *
 * Kết quả mong đợi:
 * - Đúng 1 khách đặt thành công 2 vé.
 * - Khách còn lại bị từ chối hoàn toàn (rollback, không đặt dở dang), không xảy ra Deadlock.
 */
@SpringBootTest
public class ConcurrencyMultiSeatBookingTest {

    @Autowired
    private VeXeService veXeService;

    @Autowired
    private VeXeRepository veXeRepository;

    @BeforeEach
    void setUp() {
        // Dọn dẹp dữ liệu ghế A08, A09, A10 từ lần test trước để bài test luôn chạy độc lập và chính xác
        List<VeXe> existing = veXeRepository.findAll();
        for (VeXe v : existing) {
            if (v.getChuyenXe() != null && v.getChuyenXe().getId().equals(1L)) {
                String sg = v.getSoGhe();
                if ("A08".equals(sg) || "A09".equals(sg) || "A10".equals(sg)) {
                    veXeRepository.delete(v);
                }
            }
        }
    }

    @Test
    @DisplayName("Kiểm thử đặt nhiều vé đồng thời tranh chấp ghế chéo nhau")
    void testDatNhieuGheDongThoiTranhChap() throws InterruptedException {
        int soLuongKhach = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(soLuongKhach);
        CountDownLatch readyLatch = new CountDownLatch(soLuongKhach);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(soLuongKhach);

        AtomicInteger soLuongThanhCong = new AtomicInteger(0);
        AtomicInteger soLuongThatBai = new AtomicInteger(0);

        Long testChuyenXeId = 1L;

        // Khách 1 muốn đặt [A08, A09]
        List<String> seatsKhach1 = Arrays.asList("A08", "A09");
        // Khách 2 muốn đặt [A09, A10]
        List<String> seatsKhach2 = Arrays.asList("A09", "A10");

        System.out.println("=================================================================");
        System.out.println(" BẮT ĐẦU KIỂM THỬ ĐỒNG THỜI NHIỀU GHẾ: KHÁCH 1 VÀ KHÁCH 2 TRANH GHẾ A09");
        System.out.println("=================================================================");

        // Luồng khách 1
        executorService.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                List<VeXe> ve = veXeService.datVeTheoDanhSachGhe(
                        testChuyenXeId, seatsKhach1, "Khách Hàng 1", "0911111111", "khach1@test.com", "Đón bến xe", null
                );
                soLuongThanhCong.incrementAndGet();
                System.out.println("✅ Khách 1 ĐẶT THÀNH CÔNG " + ve.size() + " vé: " + seatsKhach1);
            } catch (Exception e) {
                soLuongThatBai.incrementAndGet();
                System.out.println("❌ Khách 1 THẤT BẠI: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        });

        // Luồng khách 2
        executorService.submit(() -> {
            readyLatch.countDown();
            try {
                startLatch.await();
                List<VeXe> ve = veXeService.datVeTheoDanhSachGhe(
                        testChuyenXeId, seatsKhach2, "Khách Hàng 2", "0922222222", "khach2@test.com", "Đón ngã tư", null
                );
                soLuongThanhCong.incrementAndGet();
                System.out.println("✅ Khách 2 ĐẶT THÀNH CÔNG " + ve.size() + " vé: " + seatsKhach2);
            } catch (Exception e) {
                soLuongThatBai.incrementAndGet();
                System.out.println("❌ Khách 2 THẤT BẠI: " + e.getMessage());
            } finally {
                doneLatch.countDown();
            }
        });

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        System.out.println("=================================================================");
        System.out.println(" KẾT QUẢ KIỂM THỬ NHIỀU GHẾ ĐỒNG THỜI:");
        System.out.println(" - Khách thành công : " + soLuongThanhCong.get() + " (Kỳ vọng: 1)");
        System.out.println(" - Khách thất bại   : " + soLuongThatBai.get() + " (Kỳ vọng: 1)");
        System.out.println("=================================================================");

        assertEquals(1, soLuongThanhCong.get(), "Chỉ đúng 1 khách được đặt thành công!");
        assertEquals(1, soLuongThatBai.get(), "Khách còn lại phải bị từ chối do tranh chấp ghế!");
    }
}

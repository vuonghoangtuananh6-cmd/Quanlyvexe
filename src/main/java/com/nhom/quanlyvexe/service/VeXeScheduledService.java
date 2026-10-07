package com.nhom.quanlyvexe.service;

import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeXeScheduledService {

    @Autowired
    private VeXeRepository veXeRepository;

    /**
     * Tác vụ định kỳ (Scheduled Task - Rubric 5.1 & Tiêu chí Concurrency)
     * Chạy mỗi 60 giây một lần để quét các vé quá hạn giữ chỗ (10 phút) chưa thanh toán
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scanAndReleaseExpiredBookings() {
        String threadName = Thread.currentThread().getName();
        LocalDateTime thoiDiemHetHan = LocalDateTime.now().minusMinutes(10);

        List<VeXe> veQuaHan = veXeRepository.findByTrangThaiThanhToanAndThoiGianDatBefore("Chờ thanh toán", thoiDiemHetHan);

        if (!veQuaHan.isEmpty()) {
            System.out.println("⏰ [SCHEDULED BACKGROUND TASK - Luồng: " + threadName + "] Phát hiện " + veQuaHan.size() + " vé chờ thanh toán quá hạn 10 phút.");
            for (VeXe v : veQuaHan) {
                v.setTrangThaiThanhToan("Đã hủy");
                v.setGhiChu("Hệ thống tự động hủy do quá hạn thanh toán.");
                veXeRepository.save(v);
                System.out.println("   └─> Đã hủy vé: " + v.getMaVe() + ", Ghế: " + v.getSoGhe() + ", Chuyến: " + v.getChuyenXe().getMaChuyen());
            }
        }
    }
}

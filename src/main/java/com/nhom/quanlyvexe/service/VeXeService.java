package com.nhom.quanlyvexe.service;

import com.nhom.quanlyvexe.entity.ChuyenXe;
import com.nhom.quanlyvexe.entity.NguoiDung;
import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.repository.ChuyenXeRepository;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VeXeService {

    @Autowired
    private VeXeRepository veXeRepository;

    @Autowired
    private ChuyenXeRepository chuyenXeRepository;

    // Quản lý khóa tài nguyên theo từng cặp [Chuyến xe + Số ghế] để đồng bộ đa luồng (Rubric 5.2)
    private static final ConcurrentHashMap<String, Object> seatLocks = new ConcurrentHashMap<>();

    /**
     * Nghiệp vụ đặt vé có xử lý đồng thời (Concurrency) theo danh sách ghế
     * Hỗ trợ đặt 1 hoặc nhiều vé cùng lúc với khóa đa ghế theo thứ tự bảng chữ cái chống Deadlock
     */
    @Transactional
    public List<VeXe> datVeTheoDanhSachGhe(Long chuyenXeId, List<String> danhSachGhe, String tenKhach, String sdt, String email, String ghiChu, NguoiDung user) {
        if (danhSachGhe == null || danhSachGhe.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 ghế!");
        }

        ChuyenXe chuyenXe = chuyenXeRepository.findById(chuyenXeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến xe có ID: " + chuyenXeId));

        // Chuẩn hóa và sắp xếp danh sách ghế theo thứ tự bảng chữ cái để phòng chống Deadlock
        List<String> sortedGhe = new ArrayList<>();
        for (String g : danhSachGhe) {
            String cleanGhe = g.trim().toUpperCase();
            if (!sortedGhe.contains(cleanGhe)) {
                sortedGhe.add(cleanGhe);
            }
        }
        Collections.sort(sortedGhe);

        // Chuẩn bị danh sách đối tượng khóa
        List<Object> locks = new ArrayList<>();
        for (String soGhe : sortedGhe) {
            String lockKey = chuyenXeId + "_" + soGhe;
            locks.add(seatLocks.computeIfAbsent(lockKey, k -> new Object()));
        }

        // Đệ quy khóa lần lượt từng ghế theo thứ tự
        return acquireLocksRecursively(chuyenXe, sortedGhe, locks, 0, tenKhach, sdt, email, ghiChu, user);
    }

    private List<VeXe> acquireLocksRecursively(
            ChuyenXe chuyenXe, List<String> sortedGhe, List<Object> locks, int index,
            String tenKhach, String sdt, String email, String ghiChu, NguoiDung user) {

        if (index == locks.size()) {
            // Đã giữ toàn bộ khóa cho tất cả các ghế cần đặt
            // 1. Kiểm tra toàn diện xem có ghế nào trong danh sách đã bị đặt trước không
            for (String soGhe : sortedGhe) {
                if (veXeRepository.isGheDaDat(chuyenXe.getId(), soGhe)) {
                    throw new IllegalStateException("Ghế " + soGhe + " đã có người đặt trước trong tích tắc! Vui lòng chọn ghế khác.");
                }
            }

            // 2. Tạo mã đơn hàng chung cho các vé đặt cùng đợt
            String maDonHang = "DH" + System.currentTimeMillis();
            List<VeXe> listVe = new ArrayList<>();

            for (int i = 0; i < sortedGhe.size(); i++) {
                String soGhe = sortedGhe.get(i);
                String maVe = "VE" + System.currentTimeMillis() + (i + 1) + (int)(Math.random() * 100);

                VeXe veMoi = new VeXe();
                veMoi.setMaVe(maVe);
                veMoi.setMaDonHang(maDonHang);
                veMoi.setChuyenXe(chuyenXe);
                veMoi.setNguoiDung(user);
                veMoi.setSoGhe(soGhe);
                veMoi.setTenKhachHang(tenKhach);
                veMoi.setSoDienThoai(sdt);
                veMoi.setEmail(email);
                veMoi.setGiaVe(chuyenXe.getGiaVe());
                veMoi.setThoiGianDat(LocalDateTime.now());
                veMoi.setTrangThaiThanhToan("Chờ thanh toán");
                veMoi.setGhiChu(ghiChu);

                listVe.add(veXeRepository.save(veMoi));
            }

            System.out.println("<<< [MULTI-BOOKING SUCCESS] Đặt thành công " + listVe.size() + " vé cho đơn: " + maDonHang);
            return listVe;
        }

        // Khóa ghế tại vị trí index và gọi tiếp index + 1
        synchronized (locks.get(index)) {
            return acquireLocksRecursively(chuyenXe, sortedGhe, locks, index + 1, tenKhach, sdt, email, ghiChu, user);
        }
    }

    /**
     * Phương thức đặt vé đơn lẻ tương thích ngược 100% với ConcurrencyBookingTest
     */
    @Transactional
    public VeXe datVeDongThoi(Long chuyenXeId, String soGhe, String tenKhach, String sdt, String email, NguoiDung user) {
        List<VeXe> list = datVeTheoDanhSachGhe(chuyenXeId, Collections.singletonList(soGhe), tenKhach, sdt, email, null, user);
        return list.get(0);
    }

    @Transactional
    public void huyVe(Long veId) {
        VeXe ve = veXeRepository.findById(veId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vé xe ID: " + veId));
        ve.setTrangThaiThanhToan("Đã hủy");
        veXeRepository.save(ve);
        System.out.println(">>> [HUY VE] Đã hủy vé mã " + ve.getMaVe() + " và giải phóng ghế " + ve.getSoGhe());
    }

    @Transactional
    public void xacNhanThanhToan(Long veId) {
        VeXe ve = veXeRepository.findById(veId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vé xe ID: " + veId));
        ve.setTrangThaiThanhToan("Đã thanh toán");
        veXeRepository.save(ve);
    }

    @Transactional
    public List<VeXe> datVeDongThoi(com.nhom.quanlyvexe.dto.DatVeDto dto, NguoiDung user) {
        List<String> listGhe = dto.getDanhSachGhe();
        if (listGhe.isEmpty() && dto.getSoGhe() != null && !dto.getSoGhe().trim().isEmpty()) {
            listGhe.add(dto.getSoGhe().trim().toUpperCase());
        }
        return datVeTheoDanhSachGhe(dto.getChuyenXeId(), listGhe, dto.getTenKhachHang(), dto.getSoDienThoai(), dto.getEmail(), dto.getGhiChu(), user);
    }

    @Transactional(readOnly = true)
    public Page<VeXe> findPaginated(String keyword, int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ?
                org.springframework.data.domain.Sort.by(sortField).ascending() :
                org.springframework.data.domain.Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return veXeRepository.searchVeXe(keyword.trim(), pageable);
        }
        return veXeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<VeXe> findAllPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return veXeRepository.findAllByOrderByThoiGianDatDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<VeXe> findById(Long id) {
        return veXeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<VeXe> findByMaVe(String maVe) {
        return veXeRepository.findByMaVe(maVe);
    }
}

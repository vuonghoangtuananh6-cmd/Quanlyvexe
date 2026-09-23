package com.nhom.quanlyvexe.service;

import com.nhom.quanlyvexe.entity.Ghe;
import com.nhom.quanlyvexe.entity.Xe;
import com.nhom.quanlyvexe.repository.ChuyenXeRepository;
import com.nhom.quanlyvexe.repository.GheRepository;
import com.nhom.quanlyvexe.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class XeService {

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private GheRepository gheRepository;

    @Autowired
    private ChuyenXeRepository chuyenXeRepository;

    @Transactional(readOnly = true)
    public List<Xe> findAll() {
        return xeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Xe> findPaginated(String keyword, int pageNo, int pageSize) {
        return findPaginated(keyword, pageNo, pageSize, "id", "desc");
    }

    @Transactional(readOnly = true)
    public Page<Xe> findPaginated(String keyword, int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ?
                org.springframework.data.domain.Sort.by(sortField).ascending() :
                org.springframework.data.domain.Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return xeRepository.findByBienSoContainingIgnoreCaseOrTenXeContainingIgnoreCase(keyword.trim(), keyword.trim(), pageable);
        }
        return xeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Xe> findById(Long id) {
        return xeRepository.findById(id);
    }

    @Transactional
    public Xe save(Xe xe) {
        boolean isNew = (xe.getId() == null);
        if (isNew && xeRepository.existsByBienSo(xe.getBienSo())) {
            throw new IllegalArgumentException("Biển số xe '" + xe.getBienSo() + "' đã tồn tại!");
        }

        Xe savedXe = xeRepository.save(xe);

        // Tự động sinh danh sách ghế tương ứng với tổng số ghế của xe khi tạo mới (dùng saveAll tối ưu CSDL)
        if (isNew) {
            int total = savedXe.getTongSoGhe();
            java.util.List<Ghe> danhSachGhe = new java.util.ArrayList<>();
            boolean isGheNgoi = "Ghế ngồi".equalsIgnoreCase(savedXe.getLoaiXe());

            if (isGheNgoi) {
                // Xe ghế ngồi sinh 1 tầng
                for (int i = 1; i <= total; i++) {
                    danhSachGhe.add(new Ghe(savedXe, String.format("%02d", i), 1));
                }
            } else {
                // Xe giường nằm / Limousine sinh 2 tầng
                int perFloor = total / 2;
                for (int i = 1; i <= perFloor; i++) {
                    danhSachGhe.add(new Ghe(savedXe, String.format("A%02d", i), 1));
                }
                for (int i = 1; i <= (total - perFloor); i++) {
                    danhSachGhe.add(new Ghe(savedXe, String.format("B%02d", i), 2));
                }
            }
            gheRepository.saveAll(danhSachGhe);
        }
        return savedXe;
    }

    @Transactional
    public void deleteById(Long id) {
        Xe xe = xeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xe có ID: " + id));

        // Kiểm tra ràng buộc khóa ngoại (Tiêu chí 4.5: Tối ưu với existsByXeId, không tải toàn bộ RAM)
        if (chuyenXeRepository.existsByXeId(id)) {
            throw new IllegalStateException("Không thể xóa xe này vì đang có chuyến xe được lên lịch bằng xe này!");
        }

        xeRepository.delete(xe);
    }

    @Transactional
    public List<Ghe> getGheByXeId(Long xeId) {
        List<Ghe> list = gheRepository.findByXeIdOrderByTangAscSoGheAsc(xeId);
        Optional<Xe> xeOpt = xeRepository.findById(xeId);
        if (xeOpt.isEmpty()) {
            return list != null ? list : Collections.emptyList();
        }

        Xe xe = xeOpt.get();
        int expectedTotal = (xe.getTongSoGhe() != null && xe.getTongSoGhe() > 0) ? xe.getTongSoGhe() : 34;

        // Nếu xe chưa có ghế hoặc số ghế chưa đủ theo tổng số chỗ của xe trong CSDL
        if (list == null || list.isEmpty() || list.size() < expectedTotal) {
            Set<String> existingSeatNames = new HashSet<>();
            if (list != null) {
                for (Ghe g : list) {
                    if (g.getSoGhe() != null) {
                        existingSeatNames.add(g.getSoGhe().trim());
                    }
                }
            }

            boolean isGheNgoi = "Ghế ngồi".equalsIgnoreCase(xe.getLoaiXe()) || "Ghe ngoi".equalsIgnoreCase(xe.getLoaiXe());
            List<Ghe> missingSeats = new ArrayList<>();

            if (isGheNgoi) {
                for (int i = 1; i <= expectedTotal; i++) {
                    String seatName = String.format("%02d", i);
                    if (!existingSeatNames.contains(seatName)) {
                        missingSeats.add(new Ghe(xe, seatName, 1));
                    }
                }
            } else {
                int perFloor = expectedTotal / 2;
                for (int i = 1; i <= perFloor; i++) {
                    String seatName = String.format("A%02d", i);
                    if (!existingSeatNames.contains(seatName)) {
                        missingSeats.add(new Ghe(xe, seatName, 1));
                    }
                }
                for (int i = 1; i <= (expectedTotal - perFloor); i++) {
                    String seatName = String.format("B%02d", i);
                    if (!existingSeatNames.contains(seatName)) {
                        missingSeats.add(new Ghe(xe, seatName, 2));
                    }
                }
            }

            if (!missingSeats.isEmpty()) {
                try {
                    gheRepository.saveAll(missingSeats);
                    list = gheRepository.findByXeIdOrderByTangAscSoGheAsc(xeId);
                    System.out.println(">>> [AUTO SEED SEATS] Đã bổ sung " + missingSeats.size() + " ghế cho xe ID: " + xeId + ", tổng ghế hiện tại: " + list.size());
                } catch (Exception e) {
                    System.err.println("Không thể lưu tự động ghế bổ sung vào DB: " + e.getMessage());
                }
            }
        }
        return list;
    }
}

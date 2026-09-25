package com.nhom.quanlyvexe.service;

import com.nhom.quanlyvexe.entity.ChuyenXe;
import com.nhom.quanlyvexe.repository.ChuyenXeRepository;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ChuyenXeService {

    @Autowired
    private ChuyenXeRepository chuyenXeRepository;

    @Autowired
    private VeXeRepository veXeRepository;

    @Transactional(readOnly = true)
    public List<ChuyenXe> findAll() {
        return chuyenXeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<ChuyenXe> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return chuyenXeRepository.findAllByOrderByNgayKhoiHanhDescGioKhoiHanhDesc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ChuyenXe> findPaginated(String keyword, int pageNo, int pageSize, String sortField, String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("asc") ?
                org.springframework.data.domain.Sort.by(sortField).ascending() :
                org.springframework.data.domain.Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return chuyenXeRepository.searchChuyenXe(keyword.trim(), pageable);
        }
        return chuyenXeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<ChuyenXe> findById(Long id) {
        Optional<ChuyenXe> opt = chuyenXeRepository.findById(id);
        opt.ifPresent(this::tinhSoGheTrong);
        return opt;
    }

    @Transactional
    public ChuyenXe save(ChuyenXe chuyenXe) {
        boolean isNew = (chuyenXe.getId() == null);
        if (isNew && chuyenXeRepository.existsByMaChuyen(chuyenXe.getMaChuyen())) {
            throw new IllegalArgumentException("Mã chuyến xe '" + chuyenXe.getMaChuyen() + "' đã tồn tại!");
        }

        // Kiểm tra logic trùng xe trong cùng một ngày xuất bến
        if (isNew && chuyenXeRepository.existsByXeIdAndNgayKhoiHanhAndTrangThaiNot(
                chuyenXe.getXe().getId(), chuyenXe.getNgayKhoiHanh(), "Đã hủy")) {
            throw new IllegalArgumentException("Xe này đã được xếp lịch chạy trong ngày " + chuyenXe.getNgayKhoiHanh() + "!");
        }

        return chuyenXeRepository.save(chuyenXe);
    }

    @Transactional
    public void deleteById(Long id) {
        ChuyenXe chuyenXe = chuyenXeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyến xe có ID: " + id));

        // Kiểm tra ràng buộc khóa ngoại (Tiêu chí 4.5: Tối ưu với existsByChuyenXeId, không load toàn bộ RAM)
        if (veXeRepository.existsByChuyenXeId(id)) {
            throw new IllegalStateException("Không thể xóa chuyến xe này vì đã có vé xe được phát hành!");
        }

        chuyenXeRepository.delete(chuyenXe);
    }

    @Transactional(readOnly = true)
    public List<ChuyenXe> timKiemChuyenXe(String diemDi, String diemDen, LocalDate ngay) {
        List<ChuyenXe> list = chuyenXeRepository.timKiemChuyenXe(diemDi, diemDen, ngay);
        for (ChuyenXe c : list) {
            tinhSoGheTrong(c);
        }
        return list;
    }

    private void tinhSoGheTrong(ChuyenXe c) {
        if (c != null && c.getXe() != null) {
            long daDat = veXeRepository.countGheDaDatByChuyenXeId(c.getId());
            int tong = (c.getXe().getTongSoGhe() != null) ? c.getXe().getTongSoGhe() : 0;
            c.setSoGheTrong(Math.max(0, (int)(tong - daDat)));
        }
    }
}

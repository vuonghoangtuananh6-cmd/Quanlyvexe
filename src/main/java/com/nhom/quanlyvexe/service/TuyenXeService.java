package com.nhom.quanlyvexe.service;

import com.nhom.quanlyvexe.entity.TuyenXe;
import com.nhom.quanlyvexe.repository.ChuyenXeRepository;
import com.nhom.quanlyvexe.repository.TuyenXeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TuyenXeService {

    @Autowired
    private TuyenXeRepository tuyenXeRepository;

    @Autowired
    private ChuyenXeRepository chuyenXeRepository;

    @Transactional(readOnly = true)
    public List<TuyenXe> findAll() {
        return tuyenXeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<TuyenXe> findPaginated(String keyword, int pageNo, int pageSize, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return tuyenXeRepository.searchTuyenXe(keyword.trim(), pageable);
        }
        return tuyenXeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<TuyenXe> findById(Long id) {
        return tuyenXeRepository.findById(id);
    }

    @Transactional
    public TuyenXe save(TuyenXe tuyenXe) {
        if (tuyenXe.getId() == null && tuyenXeRepository.existsByMaTuyen(tuyenXe.getMaTuyen())) {
            throw new IllegalArgumentException("Mã tuyến xe '" + tuyenXe.getMaTuyen() + "' đã tồn tại!");
        }
        return tuyenXeRepository.save(tuyenXe);
    }

    @Transactional
    public void deleteById(Long id) {
        TuyenXe tuyenXe = tuyenXeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tuyến xe có ID: " + id));

        // Kiểm tra ràng buộc khóa ngoại (Tiêu chí 4.5: Tối ưu với existsByTuyenXeId, không tải toàn bộ RAM)
        if (chuyenXeRepository.existsByTuyenXeId(id)) {
            throw new IllegalStateException("Không thể xóa tuyến xe này vì đã có các chuyến xe đang tham chiếu đến!");
        }

        tuyenXeRepository.delete(tuyenXe);
    }
}

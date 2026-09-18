package com.nhom.quanlyvexe.service;

import com.nhom.quanlyvexe.entity.NguoiDung;
import com.nhom.quanlyvexe.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NguoiDungService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Transactional(readOnly = true)
    public Optional<NguoiDung> dangNhap(String taiKhoan, String matKhau) {
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByTaiKhoan(taiKhoan);
        if (userOpt.isPresent() && userOpt.get().getMatKhau().equals(matKhau)) {
            return userOpt;
        }
        return Optional.empty();
    }

    @Transactional
    public NguoiDung dangKy(NguoiDung nguoiDung) {
        if (nguoiDungRepository.existsByTaiKhoan(nguoiDung.getTaiKhoan())) {
            throw new IllegalArgumentException("Tài khoản đã tồn tại trên hệ thống!");
        }
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            throw new IllegalArgumentException("Email đã được sử dụng!");
        }
        nguoiDung.setVaiTro("ROLE_CUSTOMER");
        return nguoiDungRepository.save(nguoiDung);
    }

    @Transactional(readOnly = true)
    public Optional<NguoiDung> findById(Long id) {
        return nguoiDungRepository.findById(id);
    }
}

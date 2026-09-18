package com.nhom.quanlyvexe.repository;

import com.nhom.quanlyvexe.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    Optional<NguoiDung> findByTaiKhoan(String taiKhoan);
    Optional<NguoiDung> findByEmail(String email);
    boolean existsByTaiKhoan(String taiKhoan);
    boolean existsByEmail(String email);
}

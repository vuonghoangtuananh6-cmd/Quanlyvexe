package com.nhom.quanlyvexe.repository;

import com.nhom.quanlyvexe.entity.Xe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface XeRepository extends JpaRepository<Xe, Long> {
    Optional<Xe> findByBienSo(String bienSo);
    boolean existsByBienSo(String bienSo);
    List<Xe> findByLoaiXe(String loaiXe);
    Page<Xe> findByBienSoContainingIgnoreCaseOrTenXeContainingIgnoreCase(String bienSo, String tenXe, Pageable pageable);
}

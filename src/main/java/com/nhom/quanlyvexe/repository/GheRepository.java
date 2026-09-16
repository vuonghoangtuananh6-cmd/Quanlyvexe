package com.nhom.quanlyvexe.repository;

import com.nhom.quanlyvexe.entity.Ghe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GheRepository extends JpaRepository<Ghe, Long> {
    List<Ghe> findByXeIdOrderByTangAscSoGheAsc(Long xeId);
    Optional<Ghe> findByXeIdAndSoGhe(Long xeId, String soGhe);
    void deleteByXeId(Long xeId);
}

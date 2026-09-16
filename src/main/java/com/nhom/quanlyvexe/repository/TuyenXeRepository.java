package com.nhom.quanlyvexe.repository;

import com.nhom.quanlyvexe.entity.TuyenXe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuyenXeRepository extends JpaRepository<TuyenXe, Long> {
    Optional<TuyenXe> findByMaTuyen(String maTuyen);
    boolean existsByMaTuyen(String maTuyen);

    // Truy vấn tùy chỉnh theo quy ước tên phương thức
    List<TuyenXe> findByDiemDiContainingIgnoreCaseAndDiemDenContainingIgnoreCase(String diemDi, String diemDen);

    // Truy vấn tùy chỉnh sử dụng @Query với phân trang và tìm kiếm từ khóa
    @Query("SELECT t FROM TuyenXe t WHERE LOWER(t.tenTuyen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.diemDi) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.diemDen) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<TuyenXe> searchTuyenXe(@Param("keyword") String keyword, Pageable pageable);
}

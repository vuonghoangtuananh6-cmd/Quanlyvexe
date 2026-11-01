package com.nhom.quanlyvexe.repository;

import com.nhom.quanlyvexe.entity.ChuyenXe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChuyenXeRepository extends JpaRepository<ChuyenXe, Long> {
    Optional<ChuyenXe> findByMaChuyen(String maChuyen);
    boolean existsByMaChuyen(String maChuyen);

    // Truy vấn tìm chuyến xe theo Điểm đi, Điểm đến và Ngày khởi hành
    @Query("SELECT c FROM ChuyenXe c WHERE " +
           "(:diemDi IS NULL OR LOWER(c.tuyenXe.diemDi) LIKE LOWER(CONCAT('%', :diemDi, '%'))) AND " +
           "(:diemDen IS NULL OR LOWER(c.tuyenXe.diemDen) LIKE LOWER(CONCAT('%', :diemDen, '%'))) AND " +
           "(:ngay IS NULL OR c.ngayKhoiHanh = :ngay) AND " +
           "c.trangThai != 'Đã hủy' " +
           "ORDER BY c.gioKhoiHanh ASC")
    List<ChuyenXe> timKiemChuyenXe(
            @Param("diemDi") String diemDi,
            @Param("diemDen") String diemDen,
            @Param("ngay") LocalDate ngay
    );

    // Kiểm tra ràng buộc khóa ngoại (tối ưu hiệu năng CSDL theo Tiêu chí 4.5)
    boolean existsByXeId(Long xeId);
    boolean existsByTuyenXeId(Long tuyenXeId);

    // Kiểm tra xe có bị trùng lịch trong ngày không (hỗ trợ cả tạo mới và chỉnh sửa)
    boolean existsByXeIdAndNgayKhoiHanhAndTrangThaiNot(Long xeId, LocalDate ngayKhoiHanh, String trangThai);

    @Query("SELECT COUNT(c) > 0 FROM ChuyenXe c WHERE c.xe.id = :xeId AND c.ngayKhoiHanh = :ngay AND c.trangThai != 'Đã hủy' AND (:id IS NULL OR c.id != :id)")
    boolean existsConflictXe(@Param("xeId") Long xeId, @Param("ngay") LocalDate ngay, @Param("id") Long id);

    // Tìm kiếm chuyến xe cho Admin có phân trang và sắp xếp (Tiêu chí 3.4)
    @Query("SELECT c FROM ChuyenXe c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(c.maChuyen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.tuyenXe.tenTuyen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.xe.bienSo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ChuyenXe> searchChuyenXe(@Param("keyword") String keyword, Pageable pageable);

    Page<ChuyenXe> findAllByOrderByNgayKhoiHanhDescGioKhoiHanhDesc(Pageable pageable);
}

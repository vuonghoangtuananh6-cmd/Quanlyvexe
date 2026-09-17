package com.nhom.quanlyvexe.repository;

import com.nhom.quanlyvexe.entity.VeXe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VeXeRepository extends JpaRepository<VeXe, Long> {
    Optional<VeXe> findByMaVe(String maVe);
    boolean existsByMaVe(String maVe);

    // Kiểm tra xem ghế trên chuyến xe đó đã được đặt chưa (không tính vé Đã hủy)
    @Query("SELECT COUNT(v) > 0 FROM VeXe v WHERE v.chuyenXe.id = :chuyenXeId AND v.soGhe = :soGhe AND v.trangThaiThanhToan != 'Đã hủy'")
    boolean isGheDaDat(@Param("chuyenXeId") Long chuyenXeId, @Param("soGhe") String soGhe);

    // Lấy danh sách số ghế đã đặt của một chuyến xe
    @Query("SELECT v.soGhe FROM VeXe v WHERE v.chuyenXe.id = :chuyenXeId AND v.trangThaiThanhToan != 'Đã hủy'")
    List<String> findDanhSachGheDaDatByChuyenXeId(@Param("chuyenXeId") Long chuyenXeId);

    // Đếm số ghế đã đặt của một chuyến xe (trừ vé đã hủy)
    @Query("SELECT COUNT(v) FROM VeXe v WHERE v.chuyenXe.id = :chuyenXeId AND v.trangThaiThanhToan != 'Đã hủy'")
    long countGheDaDatByChuyenXeId(@Param("chuyenXeId") Long chuyenXeId);

    // Tìm danh sách vé theo mã đơn hàng
    List<VeXe> findByMaDonHang(String maDonHang);

    // Tìm các vé chờ thanh toán quá hạn (dành cho @Scheduled Concurrency)
    List<VeXe> findByTrangThaiThanhToanAndThoiGianDatBefore(String trangThai, LocalDateTime thoiDiem);

    // Danh sách vé theo khách hàng
    List<VeXe> findByNguoiDungIdOrderByThoiGianDatDesc(Long nguoiDungId);

    // Thống kê doanh thu theo trạng thái
    @Query("SELECT COALESCE(SUM(v.giaVe), 0) FROM VeXe v WHERE v.trangThaiThanhToan IN ('Đã thanh toán', 'Đã lên xe')")
    BigDecimal tinhTongDoanhThu();

    // Thống kê số lượng vé theo trạng thái
    long countByTrangThaiThanhToan(String trangThai);

    // Kiểm tra có vé nào thuộc chuyến xe này không (tối ưu hiệu năng CSDL)
    boolean existsByChuyenXeId(Long chuyenXeId);

    // Tìm kiếm vé xe có phân trang và sắp xếp (Tiêu chí 3.4)
    @Query("SELECT v FROM VeXe v WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(v.maVe) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.tenKhachHang) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<VeXe> searchVeXe(@Param("keyword") String keyword, Pageable pageable);

    Page<VeXe> findAllByOrderByThoiGianDatDesc(Pageable pageable);
}

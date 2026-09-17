package com.nhom.quanlyvexe.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ve_xe")
public class VeXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_ve", nullable = false, unique = true, length = 30)
    private String maVe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chuyen_xe_id", nullable = false)
    private ChuyenXe chuyenXe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "so_ghe", nullable = false, length = 10)
    private String soGhe;

    @Column(name = "ten_khach_hang", nullable = false, length = 100)
    private String tenKhachHang;

    @Column(name = "so_dien_thoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "gia_ve", nullable = false)
    private BigDecimal giaVe;

    @Column(name = "thoi_gian_dat")
    private LocalDateTime thoiGianDat;

    @Column(name = "trang_thai_thanh_toan", length = 50)
    private String trangThaiThanhToan = "Chờ thanh toán"; // Chờ thanh toán, Đã thanh toán, Đã lên xe, Đã hủy

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "ma_don_hang", length = 50)
    private String maDonHang;

    public VeXe() {
        this.thoiGianDat = LocalDateTime.now();
    }

    public VeXe(String maVe, ChuyenXe chuyenXe, NguoiDung nguoiDung, String soGhe, String tenKhachHang, String soDienThoai, String email, BigDecimal giaVe) {
        this.maVe = maVe;
        this.chuyenXe = chuyenXe;
        this.nguoiDung = nguoiDung;
        this.soGhe = soGhe;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.giaVe = giaVe;
        this.thoiGianDat = LocalDateTime.now();
        this.trangThaiThanhToan = "Chờ thanh toán";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaVe() { return maVe; }
    public void setMaVe(String maVe) { this.maVe = maVe; }

    public ChuyenXe getChuyenXe() { return chuyenXe; }
    public void setChuyenXe(ChuyenXe chuyenXe) { this.chuyenXe = chuyenXe; }

    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }

    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { this.soGhe = soGhe; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }

    public LocalDateTime getThoiGianDat() { return thoiGianDat; }
    public void setThoiGianDat(LocalDateTime thoiGianDat) { this.thoiGianDat = thoiGianDat; }

    public String getTrangThaiThanhToan() { return trangThaiThanhToan; }
    public void setTrangThaiThanhToan(String trangThaiThanhToan) { this.trangThaiThanhToan = trangThaiThanhToan; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
}

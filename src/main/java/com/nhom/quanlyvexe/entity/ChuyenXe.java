package com.nhom.quanlyvexe.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "chuyen_xe")
public class ChuyenXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_chuyen", nullable = false, unique = true, length = 30)
    private String maChuyen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tuyen_xe_id", nullable = false)
    private TuyenXe tuyenXe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "xe_id", nullable = false)
    private Xe xe;

    @Column(name = "ngay_khoi_hanh", nullable = false)
    private LocalDate ngayKhoiHanh;

    @Column(name = "gio_khoi_hanh", nullable = false)
    private LocalTime gioKhoiHanh;

    @Column(name = "gio_den_du_kien", nullable = false)
    private LocalTime gioDenDuKien;

    @Column(name = "gia_ve", nullable = false)
    private BigDecimal giaVe;

    @Column(name = "trang_thai", length = 50)
    private String trangThai = "Sắp khởi hành"; // Sắp khởi hành, Đang chạy, Đã hoàn thành, Đã hủy

    @Column(name = "hinh_anh", length = 500)
    private String hinhAnh;

    @Transient
    private Integer soGheTrong;

    public ChuyenXe() {}

    public ChuyenXe(String maChuyen, TuyenXe tuyenXe, Xe xe, LocalDate ngayKhoiHanh, LocalTime gioKhoiHanh, LocalTime gioDenDuKien, BigDecimal giaVe) {
        this.maChuyen = maChuyen;
        this.tuyenXe = tuyenXe;
        this.xe = xe;
        this.ngayKhoiHanh = ngayKhoiHanh;
        this.gioKhoiHanh = gioKhoiHanh;
        this.gioDenDuKien = gioDenDuKien;
        this.giaVe = giaVe;
        this.trangThai = "Sắp khởi hành";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaChuyen() { return maChuyen; }
    public void setMaChuyen(String maChuyen) { this.maChuyen = maChuyen; }

    public TuyenXe getTuyenXe() { return tuyenXe; }
    public void setTuyenXe(TuyenXe tuyenXe) { this.tuyenXe = tuyenXe; }

    public Xe getXe() { return xe; }
    public void setXe(Xe xe) { this.xe = xe; }

    public LocalDate getNgayKhoiHanh() { return ngayKhoiHanh; }
    public void setNgayKhoiHanh(LocalDate ngayKhoiHanh) { this.ngayKhoiHanh = ngayKhoiHanh; }

    public LocalTime getGioKhoiHanh() { return gioKhoiHanh; }
    public void setGioKhoiHanh(LocalTime gioKhoiHanh) { this.gioKhoiHanh = gioKhoiHanh; }

    public LocalTime getGioDenDuKien() { return gioDenDuKien; }
    public void setGioDenDuKien(LocalTime gioDenDuKien) { this.gioDenDuKien = gioDenDuKien; }

    public BigDecimal getGiaVe() { return giaVe; }
    public void setGiaVe(BigDecimal giaVe) { this.giaVe = giaVe; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public Integer getSoGheTrong() { return soGheTrong; }
    public void setSoGheTrong(Integer soGheTrong) { this.soGheTrong = soGheTrong; }

    public String getHinhAnhHienThi() {
        if (this.hinhAnh != null && !this.hinhAnh.trim().isEmpty()) {
            return this.hinhAnh;
        }
        if (this.tuyenXe != null) {
            return this.tuyenXe.getHinhAnhHienThi();
        }
        return "/images/destinations/default.jpg";
    }
}

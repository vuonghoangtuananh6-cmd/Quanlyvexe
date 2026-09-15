package com.nhom.quanlyvexe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tuyen_xe")
public class TuyenXe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_tuyen", nullable = false, unique = true, length = 30)
    private String maTuyen;

    @Column(name = "ten_tuyen", nullable = false, length = 150)
    private String tenTuyen;

    @Column(name = "diem_di", nullable = false, length = 100)
    private String diemDi;

    @Column(name = "diem_den", nullable = false, length = 100)
    private String diemDen;

    @Column(name = "quang_duong_km", nullable = false)
    private Double quangDuongKm;

    @Column(name = "thoi_gian_du_kien", nullable = false, length = 50)
    private String thoiGianDuKien;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "hinh_anh", length = 500)
    private String hinhAnh;

    public TuyenXe() {}

    public TuyenXe(String maTuyen, String tenTuyen, String diemDi, String diemDen, Double quangDuongKm, String thoiGianDuKien) {
        this.maTuyen = maTuyen;
        this.tenTuyen = tenTuyen;
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.quangDuongKm = quangDuongKm;
        this.thoiGianDuKien = thoiGianDuKien;
        this.trangThai = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaTuyen() { return maTuyen; }
    public void setMaTuyen(String maTuyen) { this.maTuyen = maTuyen; }

    public String getTenTuyen() { return tenTuyen; }
    public void setTenTuyen(String tenTuyen) { this.tenTuyen = tenTuyen; }

    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { this.diemDi = diemDi; }

    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { this.diemDen = diemDen; }

    public Double getQuangDuongKm() { return quangDuongKm; }
    public void setQuangDuongKm(Double quangDuongKm) { this.quangDuongKm = quangDuongKm; }

    public String getThoiGianDuKien() { return thoiGianDuKien; }
    public void setThoiGianDuKien(String thoiGianDuKien) { this.thoiGianDuKien = thoiGianDuKien; }

    public Boolean getTrangThai() { return trangThai; }
    public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public String getHinhAnhHienThi() {
        if (this.hinhAnh != null && !this.hinhAnh.trim().isEmpty()) {
            return this.hinhAnh;
        }
        return com.nhom.quanlyvexe.util.DestinationImageUtils.getImageByDestination(this.diemDen);
    }
}

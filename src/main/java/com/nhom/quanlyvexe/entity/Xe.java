package com.nhom.quanlyvexe.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "xe")
public class Xe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bien_so", nullable = false, unique = true, length = 20)
    private String bienSo;

    @Column(name = "ten_xe", nullable = false, length = 100)
    private String tenXe;

    @Column(name = "loai_xe", nullable = false, length = 50)
    private String loaiXe; // Giuong nam, Limousine, Ghe ngoi

    @Column(name = "tong_so_ghe", nullable = false)
    private Integer tongSoGhe;

    @Column(name = "trang_thai", length = 30)
    private String trangThai = "Hoạt động";

    @OneToMany(mappedBy = "xe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ghe> danhSachGhe = new ArrayList<>();

    public Xe() {}

    public Xe(String bienSo, String tenXe, String loaiXe, Integer tongSoGhe) {
        this.bienSo = bienSo;
        this.tenXe = tenXe;
        this.loaiXe = loaiXe;
        this.tongSoGhe = tongSoGhe;
        this.trangThai = "Hoạt động";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBienSo() { return bienSo; }
    public void setBienSo(String bienSo) { this.bienSo = bienSo; }

    public String getTenXe() { return tenXe; }
    public void setTenXe(String tenXe) { this.tenXe = tenXe; }

    public String getLoaiXe() { return loaiXe; }
    public void setLoaiXe(String loaiXe) { this.loaiXe = loaiXe; }

    public Integer getTongSoGhe() { return tongSoGhe; }
    public void setTongSoGhe(Integer tongSoGhe) { this.tongSoGhe = tongSoGhe; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public List<Ghe> getDanhSachGhe() { return danhSachGhe; }
    public void setDanhSachGhe(List<Ghe> danhSachGhe) { this.danhSachGhe = danhSachGhe; }
}

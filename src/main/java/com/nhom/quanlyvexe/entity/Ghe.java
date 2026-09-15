package com.nhom.quanlyvexe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ghe", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"xe_id", "so_ghe"})
})
public class Ghe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "xe_id", nullable = false)
    private Xe xe;

    @Column(name = "so_ghe", nullable = false, length = 10)
    private String soGhe;

    @Column(name = "tang")
    private Integer tang = 1;

    public Ghe() {}

    public Ghe(Xe xe, String soGhe, Integer tang) {
        this.xe = xe;
        this.soGhe = soGhe;
        this.tang = tang;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Xe getXe() { return xe; }
    public void setXe(Xe xe) { this.xe = xe; }

    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { this.soGhe = soGhe; }

    public Integer getTang() { return tang; }
    public void setTang(Integer tang) { this.tang = tang; }
}

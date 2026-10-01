package com.nhom.quanlyvexe.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class ChuyenXeSearchDto {

    private String diemDi;
    private String diemDen;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayDi;

    public ChuyenXeSearchDto() {}

    public ChuyenXeSearchDto(String diemDi, String diemDen, LocalDate ngayDi) {
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.ngayDi = ngayDi;
    }

    public String getDiemDi() { return diemDi; }
    public void setDiemDi(String diemDi) { this.diemDi = diemDi; }

    public String getDiemDen() { return diemDen; }
    public void setDiemDen(String diemDen) { this.diemDen = diemDen; }

    public LocalDate getNgayDi() { return ngayDi; }
    public void setNgayDi(LocalDate ngayDi) { this.ngayDi = ngayDi; }
}

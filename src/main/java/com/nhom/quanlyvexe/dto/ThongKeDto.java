package com.nhom.quanlyvexe.dto;

import java.math.BigDecimal;

public class ThongKeDto {

    private BigDecimal tongDoanhThu;
    private long tongSoVe;
    private long soVeDaThanhToan;
    private long soVeChoThanhToan;
    private long soVeDaLenXe;
    private long soVeDaHuy;

    private long tongSoTuyen;
    private long tongSoXe;
    private long tongSoChuyen;

    public ThongKeDto() {
        this.tongDoanhThu = BigDecimal.ZERO;
    }

    public BigDecimal getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public long getTongSoVe() { return tongSoVe; }
    public void setTongSoVe(long tongSoVe) { this.tongSoVe = tongSoVe; }

    public long getSoVeDaThanhToan() { return soVeDaThanhToan; }
    public void setSoVeDaThanhToan(long soVeDaThanhToan) { this.soVeDaThanhToan = soVeDaThanhToan; }

    public long getSoVeChoThanhToan() { return soVeChoThanhToan; }
    public void setSoVeChoThanhToan(long soVeChoThanhToan) { this.soVeChoThanhToan = soVeChoThanhToan; }

    public long getSoVeDaLenXe() { return soVeDaLenXe; }
    public void setSoVeDaLenXe(long soVeDaLenXe) { this.soVeDaLenXe = soVeDaLenXe; }

    public long getSoVeDaHuy() { return soVeDaHuy; }
    public void setSoVeDaHuy(long soVeDaHuy) { this.soVeDaHuy = soVeDaHuy; }

    public long getTongSoTuyen() { return tongSoTuyen; }
    public void setTongSoTuyen(long tongSoTuyen) { this.tongSoTuyen = tongSoTuyen; }

    public long getTongSoXe() { return tongSoXe; }
    public void setTongSoXe(long tongSoXe) { this.tongSoXe = tongSoXe; }

    public long getTongSoChuyen() { return tongSoChuyen; }
    public void setTongSoChuyen(long tongSoChuyen) { this.tongSoChuyen = tongSoChuyen; }
}

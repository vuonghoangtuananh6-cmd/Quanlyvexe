package com.nhom.quanlyvexe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public class DatVeDto {

    @NotNull(message = "Chuyến xe không được để trống")
    private Long chuyenXeId;

    @NotBlank(message = "Vui lòng chọn vị trí ghế ngồi")
    private String soGhe;

    private Integer soLuongVe = 1;

    @NotBlank(message = "Họ và tên hành khách không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String tenKhachHang;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không đúng định dạng Việt Nam")
    private String soDienThoai;

    @Email(message = "Địa chỉ email không đúng định dạng")
    private String email;

    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String ghiChu;

    public DatVeDto() {}

    public Long getChuyenXeId() { return chuyenXeId; }
    public void setChuyenXeId(Long chuyenXeId) { this.chuyenXeId = chuyenXeId; }

    public String getSoGhe() { return soGhe; }
    public void setSoGhe(String soGhe) { this.soGhe = soGhe; }

    public Integer getSoLuongVe() {
        if (soLuongVe == null || soLuongVe < 1) {
            List<String> list = getDanhSachGhe();
            return list.isEmpty() ? 1 : list.size();
        }
        return soLuongVe;
    }
    public void setSoLuongVe(Integer soLuongVe) { this.soLuongVe = soLuongVe; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    /**
     * Tách chuỗi ghế (ví dụ "A01, A02, A03") thành danh sách các ghế riêng biệt
     */
    public java.util.List<String> getDanhSachGhe() {
        java.util.List<String> list = new java.util.ArrayList<>();
        if (this.soGhe != null && !this.soGhe.trim().isEmpty()) {
            String[] parts = this.soGhe.split("[,;\\s]+");
            for (String p : parts) {
                if (!p.trim().isEmpty()) {
                    list.add(p.trim().toUpperCase());
                }
            }
        }
        return list;
    }
}

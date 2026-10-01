package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.entity.ChuyenXe;
import com.nhom.quanlyvexe.entity.Ghe;
import com.nhom.quanlyvexe.entity.NguoiDung;
import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import com.nhom.quanlyvexe.service.ChuyenXeService;
import com.nhom.quanlyvexe.service.XeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class ChuyenXePublicController {

    @Autowired
    private ChuyenXeService chuyenXeService;

    @Autowired
    private XeService xeService;

    @Autowired
    private VeXeRepository veXeRepository;

    @GetMapping("/chuyen-xe/tim-kiem")
    public String timKiemChuyenXe(
            @RequestParam(name = "diemDi", required = false) String diemDi,
            @RequestParam(name = "diemDen", required = false) String diemDen,
            @RequestParam(name = "ngay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
            Model model) {

        List<ChuyenXe> ketQua = chuyenXeService.timKiemChuyenXe(diemDi, diemDen, ngay);
        model.addAttribute("danhSachChuyen", ketQua);
        model.addAttribute("diemDi", diemDi);
        model.addAttribute("diemDen", diemDen);
        model.addAttribute("ngay", ngay);

        return "chuyen-xe/tim-kiem";
    }

    @GetMapping("/chuyen-xe/chi-tiet/{id}")
    public String chiTietChuyenXe(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ChuyenXe> chuyenXeOpt = chuyenXeService.findById(id);
        if (chuyenXeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin chuyến xe!");
            return "redirect:/chuyen-xe/tim-kiem";
        }

        ChuyenXe chuyenXe = chuyenXeOpt.get();
        String diemDen = (chuyenXe.getTuyenXe() != null) ? chuyenXe.getTuyenXe().getDiemDen() : "";
        model.addAttribute("chuyenXe", chuyenXe);
        model.addAttribute("galleryImages", com.nhom.quanlyvexe.util.DestinationImageUtils.getGalleryImages(diemDen));
        model.addAttribute("highlightText", com.nhom.quanlyvexe.util.DestinationImageUtils.getHighlight(diemDen));
        return "chuyen-xe/chi-tiet";
    }

    @GetMapping("/chuyen-xe/chon-ghe/{id}")
    public String chonGhe(@PathVariable("id") Long id, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        NguoiDung currentUser = (NguoiDung) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập tài khoản để thực hiện đặt vé!");
            return "redirect:/dang-nhap?redirect=/chuyen-xe/chon-ghe/" + id;
        }

        Optional<ChuyenXe> chuyenXeOpt = chuyenXeService.findById(id);
        if (chuyenXeOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến xe!");
            return "redirect:/chuyen-xe/tim-kiem";
        }

        ChuyenXe chuyenXe = chuyenXeOpt.get();
        List<Ghe> danhSachGhe = xeService.getGheByXeId(chuyenXe.getXe().getId());
        List<String> danhSachGheDaDat = veXeRepository.findDanhSachGheDaDatByChuyenXeId(chuyenXe.getId());
        if (danhSachGheDaDat == null) {
            danhSachGheDaDat = new java.util.ArrayList<>();
        }

        boolean isGheNgoi = chuyenXe.getXe() != null && chuyenXe.getXe().getLoaiXe() != null &&
                (chuyenXe.getXe().getLoaiXe().equalsIgnoreCase("Ghế ngồi") || chuyenXe.getXe().getLoaiXe().equalsIgnoreCase("Ghe ngoi"));
        boolean coTang2 = !isGheNgoi;
        if (danhSachGhe != null && !danhSachGhe.isEmpty()) {
            boolean hasFloor2 = false;
            for (Ghe g : danhSachGhe) {
                if (g.getTang() != null && g.getTang() == 2) {
                    hasFloor2 = true;
                    break;
                }
            }
            if (hasFloor2) {
                coTang2 = true;
            }
        }

        model.addAttribute("chuyenXe", chuyenXe);
        model.addAttribute("danhSachGhe", danhSachGhe);
        model.addAttribute("danhSachGheDaDat", danhSachGheDaDat);
        model.addAttribute("coTang2", coTang2);

        // Chuẩn bị sẵn thông tin khách hàng từ tài khoản đã đăng nhập
        VeXe veXeMoi = new VeXe();
        com.nhom.quanlyvexe.dto.DatVeDto datVeDto = new com.nhom.quanlyvexe.dto.DatVeDto();
        datVeDto.setChuyenXeId(chuyenXe.getId());
        if (currentUser != null) {
            veXeMoi.setTenKhachHang(currentUser.getHoTen());
            veXeMoi.setSoDienThoai(currentUser.getSoDienThoai());
            veXeMoi.setEmail(currentUser.getEmail());
            datVeDto.setTenKhachHang(currentUser.getHoTen());
            datVeDto.setSoDienThoai(currentUser.getSoDienThoai());
            datVeDto.setEmail(currentUser.getEmail());
        }
        model.addAttribute("veXe", veXeMoi);
        model.addAttribute("datVeDto", datVeDto);

        return "chuyen-xe/chon-ghe";
    }

    @GetMapping("/ve-cua-toi")
    public String veCuaToi(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        NguoiDung currentUser = (NguoiDung) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để xem lịch sử vé!");
            return "redirect:/dang-nhap";
        }

        List<VeXe> danhSachVe = veXeRepository.findByNguoiDungIdOrderByThoiGianDatDesc(currentUser.getId());
        model.addAttribute("danhSachVe", danhSachVe);
        return "ve/ve-cua-toi";
    }
}

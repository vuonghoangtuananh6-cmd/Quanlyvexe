package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.repository.ChuyenXeRepository;
import com.nhom.quanlyvexe.repository.TuyenXeRepository;
import com.nhom.quanlyvexe.repository.VeXeRepository;
import com.nhom.quanlyvexe.repository.XeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/thong-ke")
public class ThongKeController {

    @Autowired
    private VeXeRepository veXeRepository;

    @Autowired
    private TuyenXeRepository tuyenXeRepository;

    @Autowired
    private XeRepository xeRepository;

    @Autowired
    private ChuyenXeRepository chuyenXeRepository;

    @GetMapping
    public String viewDashboard(Model model) {
        BigDecimal tongDoanhThu = veXeRepository.tinhTongDoanhThu();
        long soVeDaThanhToan = veXeRepository.countByTrangThaiThanhToan("Đã thanh toán");
        long soVeChoThanhToan = veXeRepository.countByTrangThaiThanhToan("Chờ thanh toán");
        long soVeDaLenXe = veXeRepository.countByTrangThaiThanhToan("Đã lên xe");
        long soVeDaHuy = veXeRepository.countByTrangThaiThanhToan("Đã hủy");

        long tongSoTuyen = tuyenXeRepository.count();
        long tongSoXe = xeRepository.count();
        long tongSoChuyen = chuyenXeRepository.count();

        com.nhom.quanlyvexe.dto.ThongKeDto dto = new com.nhom.quanlyvexe.dto.ThongKeDto();
        dto.setTongDoanhThu(tongDoanhThu != null ? tongDoanhThu : BigDecimal.ZERO);
        dto.setSoVeDaThanhToan(soVeDaThanhToan);
        dto.setSoVeChoThanhToan(soVeChoThanhToan);
        dto.setSoVeDaLenXe(soVeDaLenXe);
        dto.setSoVeDaHuy(soVeDaHuy);
        dto.setTongSoVe(soVeDaThanhToan + soVeChoThanhToan + soVeDaLenXe + soVeDaHuy);
        dto.setTongSoTuyen(tongSoTuyen);
        dto.setTongSoXe(tongSoXe);
        dto.setTongSoChuyen(tongSoChuyen);

        model.addAttribute("thongKeDto", dto);
        model.addAttribute("tongDoanhThu", dto.getTongDoanhThu());
        model.addAttribute("soVeDaThanhToan", dto.getSoVeDaThanhToan());
        model.addAttribute("soVeChoThanhToan", dto.getSoVeChoThanhToan());
        model.addAttribute("soVeDaLenXe", dto.getSoVeDaLenXe());
        model.addAttribute("soVeDaHuy", dto.getSoVeDaHuy());
        model.addAttribute("tongSoVe", dto.getTongSoVe());

        model.addAttribute("tongSoTuyen", dto.getTongSoTuyen());
        model.addAttribute("tongSoXe", dto.getTongSoXe());
        model.addAttribute("tongSoChuyen", dto.getTongSoChuyen());

        return "admin/thong-ke";
    }
}

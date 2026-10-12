package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.entity.ChuyenXe;
import com.nhom.quanlyvexe.service.ChuyenXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ChuyenXeService chuyenXeService;

    @GetMapping("/")
    public String index(Model model) {
        List<ChuyenXe> danhSachChuyen = chuyenXeService.timKiemChuyenXe(null, null, null);
        if (danhSachChuyen != null && danhSachChuyen.size() > 8) {
            danhSachChuyen = danhSachChuyen.subList(0, 8);
        }
        model.addAttribute("danhSachChuyenTieuBieu", danhSachChuyen);
        return "index";
    }
}

package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.entity.TuyenXe;
import com.nhom.quanlyvexe.service.TuyenXeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tuyen-xe")
public class TuyenXeController {

    @Autowired
    private TuyenXeService tuyenXeService;

    @GetMapping
    public String listTuyenXe(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        Page<TuyenXe> pageResult = tuyenXeService.findPaginated(keyword, page, size, sortField, sortDir);

        model.addAttribute("listTuyenXe", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/tuyen-xe/list";
    }

    @GetMapping("/them-moi")
    public String showAddForm(Model model) {
        model.addAttribute("tuyenXe", new TuyenXe());
        model.addAttribute("pageTitle", "Thêm Tuyến Xe Mới");
        return "admin/tuyen-xe/form";
    }

    @PostMapping("/luu")
    public String saveTuyenXe(@ModelAttribute("tuyenXe") TuyenXe tuyenXe, RedirectAttributes redirectAttributes) {
        try {
            tuyenXeService.save(tuyenXe);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin tuyến xe thành công!");
            return "redirect:/admin/tuyen-xe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/tuyen-xe/them-moi";
        }
    }

    @GetMapping("/chinh-sua/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return tuyenXeService.findById(id).map(tuyenXe -> {
            model.addAttribute("tuyenXe", tuyenXe);
            model.addAttribute("pageTitle", "Chỉnh Sửa Tuyến Xe");
            return "admin/tuyen-xe/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tuyến xe!");
            return "redirect:/admin/tuyen-xe";
        });
    }

    @GetMapping("/xoa/{id}")
    public String deleteTuyenXe(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            tuyenXeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa tuyến xe thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/tuyen-xe";
    }
}

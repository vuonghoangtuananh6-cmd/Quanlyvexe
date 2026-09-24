package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.entity.Xe;
import com.nhom.quanlyvexe.service.XeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/xe")
public class XeController {

    @Autowired
    private XeService xeService;

    @GetMapping
    public String listXe(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        Page<Xe> pageResult = xeService.findPaginated(keyword, page, size, sortField, sortDir);
        model.addAttribute("listXe", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/xe/list";
    }

    @GetMapping("/them-moi")
    public String showAddForm(Model model) {
        model.addAttribute("xe", new Xe());
        model.addAttribute("pageTitle", "Thêm Xe Khách Mới");
        return "admin/xe/form";
    }

    @PostMapping("/luu")
    public String saveXe(@ModelAttribute("xe") Xe xe, RedirectAttributes redirectAttributes) {
        try {
            xeService.save(xe);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin xe khách thành công!");
            return "redirect:/admin/xe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/xe/them-moi";
        }
    }

    @GetMapping("/chinh-sua/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return xeService.findById(id).map(xe -> {
            model.addAttribute("xe", xe);
            model.addAttribute("pageTitle", "Chỉnh Sửa Xe Khách");
            return "admin/xe/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin xe!");
            return "redirect:/admin/xe";
        });
    }

    @GetMapping("/xoa/{id}")
    public String deleteXe(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            xeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa xe khách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/xe";
    }

    @GetMapping("/ghe/{id}")
    public String viewGhe(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return xeService.findById(id).map(xe -> {
            model.addAttribute("xe", xe);
            model.addAttribute("danhSachGhe", xeService.getGheByXeId(id));
            return "admin/xe/ghe";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin xe!");
            return "redirect:/admin/xe";
        });
    }
}

package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.entity.ChuyenXe;
import com.nhom.quanlyvexe.service.ChuyenXeService;
import com.nhom.quanlyvexe.service.TuyenXeService;
import com.nhom.quanlyvexe.service.XeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/chuyen-xe")
public class ChuyenXeAdminController {

    @Autowired
    private ChuyenXeService chuyenXeService;

    @Autowired
    private TuyenXeService tuyenXeService;

    @Autowired
    private XeService xeService;

    @GetMapping
    public String listChuyenXe(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        Page<ChuyenXe> pageResult = chuyenXeService.findPaginated(keyword, page, size, sortField, sortDir);
        model.addAttribute("listChuyenXe", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/chuyen-xe/list";
    }

    @GetMapping("/them-moi")
    public String showAddForm(Model model) {
        model.addAttribute("chuyenXe", new ChuyenXe());
        model.addAttribute("listTuyenXe", tuyenXeService.findAll());
        model.addAttribute("listXe", xeService.findAll());
        model.addAttribute("pageTitle", "Lên Lịch Chuyến Xe Mới");
        return "admin/chuyen-xe/form";
    }

    @PostMapping("/luu")
    public String saveChuyenXe(@ModelAttribute("chuyenXe") ChuyenXe chuyenXe, RedirectAttributes redirectAttributes) {
        try {
            chuyenXeService.save(chuyenXe);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu lịch trình chuyến xe thành công!");
            return "redirect:/admin/chuyen-xe";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            if (chuyenXe.getId() != null) {
                return "redirect:/admin/chuyen-xe/chinh-sua/" + chuyenXe.getId();
            }
            return "redirect:/admin/chuyen-xe/them-moi";
        }
    }

    @GetMapping("/chinh-sua/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return chuyenXeService.findById(id).map(chuyenXe -> {
            model.addAttribute("chuyenXe", chuyenXe);
            model.addAttribute("listTuyenXe", tuyenXeService.findAll());
            model.addAttribute("listXe", xeService.findAll());
            model.addAttribute("pageTitle", "Chỉnh Sửa Chuyến Xe");
            return "admin/chuyen-xe/form";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến xe!");
            return "redirect:/admin/chuyen-xe";
        });
    }

    @GetMapping("/xoa/{id}")
    public String deleteChuyenXe(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            chuyenXeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chuyến xe thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/chuyen-xe";
    }
}

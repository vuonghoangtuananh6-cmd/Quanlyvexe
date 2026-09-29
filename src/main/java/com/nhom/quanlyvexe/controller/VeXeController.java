package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.dto.DatVeDto;
import com.nhom.quanlyvexe.entity.NguoiDung;
import com.nhom.quanlyvexe.entity.VeXe;
import com.nhom.quanlyvexe.service.VeXeService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class VeXeController {

    @Autowired
    private VeXeService veXeService;

    @PostMapping("/ve/dat-ve")
    public String datVe(
            @Valid @ModelAttribute("datVeDto") DatVeDto datVeDto,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        NguoiDung currentUser = (NguoiDung) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập tài khoản để đặt vé!");
            String redirectUrl = (datVeDto != null && datVeDto.getChuyenXeId() != null) ? "?redirect=/chuyen-xe/chon-ghe/" + datVeDto.getChuyenXeId() : "";
            return "redirect:/dang-nhap" + redirectUrl;
        }

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
            return "redirect:/chuyen-xe/chon-ghe/" + (datVeDto.getChuyenXeId() != null ? datVeDto.getChuyenXeId() : "");
        }

        try {
            java.util.List<VeXe> listVe = veXeService.datVeDongThoi(datVeDto, currentUser);

            StringBuilder msg = new StringBuilder("Đặt chỗ thành công " + listVe.size() + " vé! (Ghế: ");
            for (int i = 0; i < listVe.size(); i++) {
                msg.append(listVe.get(i).getSoGhe());
                if (i < listVe.size() - 1) msg.append(", ");
            }
            msg.append("). Mã đơn: ").append(listVe.get(0).getMaDonHang()).append(". Vui lòng thanh toán trong vòng 10 phút.");

            redirectAttributes.addFlashAttribute("successMessage", msg.toString());
            return "redirect:/ve-cua-toi";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/chuyen-xe/chon-ghe/" + datVeDto.getChuyenXeId();
        }
    }

    @GetMapping("/ve/huy/{id}")
    public String huyVe(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            NguoiDung currentUser = (NguoiDung) session.getAttribute("user");
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng đăng nhập để thực hiện thao tác này!");
                return "redirect:/dang-nhap";
            }

            Optional<VeXe> veOpt = veXeService.findById(id);
            if (veOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy vé xe!");
                return "redirect:/ve-cua-toi";
            }

            VeXe ve = veOpt.get();
            // Ràng buộc bảo mật: Chỉ chủ nhân của vé hoặc Admin/Nhân viên mới được hủy vé
            boolean laChuVe = ve.getNguoiDung() != null && ve.getNguoiDung().getId().equals(currentUser.getId());
            boolean laQuanTri = "ROLE_ADMIN".equals(currentUser.getVaiTro()) || "ROLE_STAFF".equals(currentUser.getVaiTro());

            if (!laChuVe && !laQuanTri) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền hủy vé của người khác!");
                return "redirect:/ve-cua-toi";
            }

            veXeService.huyVe(id);
            redirectAttributes.addFlashAttribute("successMessage", "Hủy vé thành công! Ghế đã được nhả lại.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ve-cua-toi";
    }

    // Quản lý vé dành cho Admin/Nhân viên (Tiêu chí 3.4: Tìm kiếm, phân trang và sắp xếp)
    @GetMapping("/admin/ve-xe")
    public String listVeXeAdmin(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "8") int size,
            @RequestParam(name = "sortField", defaultValue = "thoiGianDat") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            Model model) {

        Page<VeXe> pageResult = veXeService.findPaginated(keyword, page, size, sortField, sortDir);
        model.addAttribute("listVeXe", pageResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalItems", pageResult.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "admin/ve-xe/list";
    }

    @GetMapping("/admin/ve-xe/thanh-toan/{id}")
    public String xacNhanThanhToan(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            veXeService.xacNhanThanhToan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận thanh toán cho vé thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/ve-xe";
    }
}

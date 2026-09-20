package com.nhom.quanlyvexe.controller;

import com.nhom.quanlyvexe.entity.NguoiDung;
import com.nhom.quanlyvexe.service.NguoiDungService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping("/dang-nhap")
    public String showLoginForm(
            @RequestParam(value = "redirect", required = false) String redirect,
            Model model,
            HttpSession session) {
        if (redirect != null && !redirect.trim().isEmpty()) {
            session.setAttribute("redirectUrl", redirect.trim());
            model.addAttribute("redirect", redirect.trim());
        } else {
            String savedRedirect = (String) session.getAttribute("redirectUrl");
            if (savedRedirect != null && !savedRedirect.trim().isEmpty()) {
                model.addAttribute("redirect", savedRedirect.trim());
            }
        }
        return "auth/login";
    }

    @PostMapping("/dang-nhap")
    public String handleLogin(
            @RequestParam("taiKhoan") String taiKhoan,
            @RequestParam("matKhau") String matKhau,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Optional<NguoiDung> userOpt = nguoiDungService.dangNhap(taiKhoan, matKhau);
        if (userOpt.isPresent()) {
            NguoiDung user = userOpt.get();
            session.setAttribute("user", user);
            redirectAttributes.addFlashAttribute("successMessage", "Xin chào " + user.getHoTen() + "! Đăng nhập thành công.");

            if ("ROLE_ADMIN".equals(user.getVaiTro()) || "ROLE_STAFF".equals(user.getVaiTro())) {
                session.removeAttribute("redirectUrl");
                return "redirect:/admin/tuyen-xe";
            }

            String targetRedirect = (redirect != null && !redirect.trim().isEmpty())
                    ? redirect.trim()
                    : (String) session.getAttribute("redirectUrl");
            session.removeAttribute("redirectUrl");

            if (targetRedirect != null && !targetRedirect.trim().isEmpty() && targetRedirect.startsWith("/")) {
                return "redirect:" + targetRedirect;
            }
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Sai tài khoản hoặc mật khẩu!");
            if (redirect != null && !redirect.trim().isEmpty()) {
                return "redirect:/dang-nhap?redirect=" + redirect.trim();
            }
            return "redirect:/dang-nhap";
        }
    }

    @GetMapping("/dang-ky")
    public String showRegisterForm(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "auth/register";
    }

    @PostMapping("/dang-ky")
    public String handleRegister(@ModelAttribute("nguoiDung") NguoiDung nguoiDung,
                                 RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.dangKy(nguoiDung);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/dang-nhap";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/dang-ky";
        }
    }

    @GetMapping("/dang-xuat")
    public String handleLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("user");
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Đã đăng xuất khỏi hệ thống thành công.");
        return "redirect:/";
    }
}

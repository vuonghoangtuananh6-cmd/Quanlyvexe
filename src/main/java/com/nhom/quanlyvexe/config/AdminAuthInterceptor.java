package com.nhom.quanlyvexe.config;

import com.nhom.quanlyvexe.entity.NguoiDung;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        // Chưa đăng nhập -> Chuyển hướng đến trang Đăng nhập
        if (user == null) {
            session.setAttribute("errorMessage", "Vui lòng đăng nhập để truy cập trang quản trị!");
            response.sendRedirect(request.getContextPath() + "/dang-nhap");
            return false;
        }

        String uri = request.getRequestURI();
        String role = user.getVaiTro();

        // Nhân viên (ROLE_STAFF) chỉ có quyền quản lý vé xe (/admin/ve-xe)
        if ("ROLE_STAFF".equals(role)) {
            if (uri.startsWith("/admin/ve-xe")) {
                return true;
            }
            // Nếu truy cập các trang quản trị khác như /admin/tuyen-xe, /admin/xe, /admin/chuyen-xe, /admin/thong-ke -> Chặn
            session.setAttribute("errorMessage", "Tài khoản Nhân viên không có quyền truy cập khu vực này!");
            response.sendRedirect(request.getContextPath() + "/admin/ve-xe");
            return false;
        }

        // Quản trị viên (ROLE_ADMIN) có toàn quyền
        if ("ROLE_ADMIN".equals(role)) {
            return true;
        }

        // Khách hàng (ROLE_CUSTOMER) không được vào bất kỳ trang /admin nào
        session.setAttribute("errorMessage", "Bạn không có quyền truy cập trang quản trị!");
        response.sendRedirect(request.getContextPath() + "/");
        return false;
    }
}

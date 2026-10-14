package com.nhom.quanlyvexe.util;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * TIỆN ÍCH ÁNH XẠ VÀ QUẢN LÝ HÌNH ẢNH ĐIỂM ĐẾN DU LỊCH
 * Tự động nhận diện điểm đến từ tên lộ trình / tuyến xe và trả về ảnh tương ứng
 */
public class DestinationImageUtils {

    private static final String DEFAULT_IMAGE = "/images/destinations/default.jpg";

    private static final Map<String, String> DESTINATION_MAP = new LinkedHashMap<>();
    private static final Map<String, List<String>> GALLERY_MAP = new LinkedHashMap<>();
    private static final Map<String, String> HIGHLIGHT_MAP = new LinkedHashMap<>();

    static {
        // Ánh xạ ảnh chính
        DESTINATION_MAP.put("da lat", "/images/destinations/da-lat.jpg");
        DESTINATION_MAP.put("lam dong", "/images/destinations/da-lat.jpg");
        DESTINATION_MAP.put("da nang", "/images/destinations/da-nang.jpg");
        DESTINATION_MAP.put("nha trang", "/images/destinations/nha-trang.jpg");
        DESTINATION_MAP.put("khanh hoa", "/images/destinations/nha-trang.jpg");
        DESTINATION_MAP.put("phu quoc", "/images/destinations/phu-quoc.jpg");
        DESTINATION_MAP.put("ha noi", "/images/destinations/ha-noi.jpg");
        DESTINATION_MAP.put("hue", "/images/destinations/hue.jpg");
        DESTINATION_MAP.put("sai gon", "/images/destinations/sai-gon.jpg");
        DESTINATION_MAP.put("ho chi minh", "/images/destinations/sai-gon.jpg");
        DESTINATION_MAP.put("sapa", "/images/destinations/sapa.jpg");
        DESTINATION_MAP.put("lao cai", "/images/destinations/sapa.jpg");
        DESTINATION_MAP.put("can tho", "/images/destinations/can-tho.jpg");
        DESTINATION_MAP.put("vung tau", "/images/destinations/vung-tau.jpg");
        DESTINATION_MAP.put("phan thiet", "/images/destinations/phan-thiet.jpg");
        DESTINATION_MAP.put("binh thuan", "/images/destinations/phan-thiet.jpg");
        DESTINATION_MAP.put("quy nhon", "/images/destinations/quy-nhon.jpg");
        DESTINATION_MAP.put("binh dinh", "/images/destinations/quy-nhon.jpg");
        DESTINATION_MAP.put("buon ma thuot", "/images/destinations/buon-ma-thuot.jpg");
        DESTINATION_MAP.put("dak lak", "/images/destinations/buon-ma-thuot.jpg");
        DESTINATION_MAP.put("ha long", "/images/destinations/ha-long.jpg");
        DESTINATION_MAP.put("quang ninh", "/images/destinations/ha-long.jpg");
        DESTINATION_MAP.put("ninh binh", "/images/destinations/ninh-binh.jpg");
        DESTINATION_MAP.put("hai phong", "/images/destinations/hai-phong.jpg");
        DESTINATION_MAP.put("vinh", "/images/destinations/vinh.jpg");
        DESTINATION_MAP.put("nghe an", "/images/destinations/vinh.jpg");
        DESTINATION_MAP.put("rach gia", "/images/destinations/rach-gia.jpg");
        DESTINATION_MAP.put("kien giang", "/images/destinations/rach-gia.jpg");
        DESTINATION_MAP.put("ca mau", "/images/destinations/ca-mau.jpg");

        // Địa danh nổi bật & giới thiệu
        HIGHLIGHT_MAP.put("da lat", "Thành phố ngàn hoa, Quảng trường Lâm Viên, Hồ Xuân Hương, Thung Lũng Tình Yêu.");
        HIGHLIGHT_MAP.put("da nang", "Cầu Rồng phun lửa, Quần thể Bà Nà Hills, Bãi biển Mỹ Khê top đầu hành tinh.");
        HIGHLIGHT_MAP.put("nha trang", "Vịnh biển Nha Trang ngọc bích, Tháp Bà Ponagar, VinWonders Hòn Tre.");
        HIGHLIGHT_MAP.put("phu quoc", "Đảo Ngọc Phú Quốc, Bãi Sao cát trắng mịn, ngắm hoàng hôn Grand World.");
        HIGHLIGHT_MAP.put("ha noi", "Thủ đô ngàn năm văn hiến, Tháp Rùa Hồ Gươm, 36 Phố phường cổ kính.");
        HIGHLIGHT_MAP.put("hue", "Kinh thành Đại Nội Huế di sản thế giới, Chùa Thiên Mụ, Sông Hương êm đềm.");
        HIGHLIGHT_MAP.put("sai gon", "Hòn ngọc Viễn Đông, Landmark 81, Nhà thờ Đức Bà, Bưu điện Trung tâm.");
        HIGHLIGHT_MAP.put("sapa", "Nóc nhà Đông Dương Fansipan, Ruộng bậc thang Mường Hoa kỳ vĩ.");
        HIGHLIGHT_MAP.put("can tho", "Thủ phủ miền Tây sông nước, Chợ nổi Cái Răng, Bến Ninh Kiều thơ mộng.");
        HIGHLIGHT_MAP.put("vung tau", "Bãi Sau biển xanh nắng vàng, Tượng Chúa Kito Vua, Ngọn Hải Đăng cổ.");
        HIGHLIGHT_MAP.put("phan thiet", "Đồi cát bay Mũi Né, Suối Tiên, Làng chài Mũi Né bình minh rực rỡ.");
        HIGHLIGHT_MAP.put("quy nhon", "Tuyệt tác thiên nhiên Eo Gió, Bãi biển Kỳ Co trong vắt màu ngọc lục bảo.");
        HIGHLIGHT_MAP.put("buon ma thuot", "Thủ phủ Cà phê Tây Nguyên, Hùng vĩ Thác Dray Nur, Bản Đôn.");
        HIGHLIGHT_MAP.put("ha long", "Kỳ quan thiên nhiên thế giới Vịnh Hạ Long, Hòn Trống Mái, Đảo Ti Tốp.");
        HIGHLIGHT_MAP.put("ninh binh", "Quần thể danh thắng Tràng An di sản kép, Tam Cốc Bích Động non nước hữu tình.");
        HIGHLIGHT_MAP.put("hai phong", "Thành phố Hoa Phượng Đỏ, Cầu Hoàng Văn Thụ, Đảo ngọc Cát Bà.");
        HIGHLIGHT_MAP.put("vinh", "Quê hương Chủ tịch Hồ Chí Minh, Bãi biển Cửa Lò sóng vỗ dạt dào.");
        HIGHLIGHT_MAP.put("rach gia", "Cổng Tam Quan biểu tượng, biển Tây Nam Kiên Giang trù phú.");
        HIGHLIGHT_MAP.put("ca mau", "Cực Nam Tổ quốc, Cột mốc tọa độ Mũi Cà Mau, Rừng ngập mặn U Minh Hạ.");
    }

    /**
     * Lấy URL ảnh đại diện theo tên điểm đến hoặc tên tuyến
     */
    public static String getImageByDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            return DEFAULT_IMAGE;
        }

        String normalized = removeDiacritics(destination.toLowerCase());
        for (Map.Entry<String, String> entry : DESTINATION_MAP.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return DEFAULT_IMAGE;
    }

    /**
     * Lấy danh sách ảnh thư viện liên quan đến điểm đến
     */
    public static List<String> getGalleryImages(String destination) {
        String mainImg = getImageByDestination(destination);
        List<String> list = new ArrayList<>();
        list.add(mainImg);
        // Thêm ảnh phụ tương ứng nếu có
        list.add(mainImg); // Gallery slot
        list.add(DEFAULT_IMAGE);
        return list;
    }

    /**
     * Lấy điểm nhấn du lịch đặc trưng
     */
    public static String getHighlight(String destination) {
        if (destination == null) return "Hành trình an toàn, dịch vụ chuyên nghiệp.";
        String normalized = removeDiacritics(destination.toLowerCase());
        for (Map.Entry<String, String> entry : HIGHLIGHT_MAP.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "Hành trình xe khách tiện nghi cao cấp, tài xế kinh nghiệm phục vụ tận tâm.";
    }

    /**
     * Chuyển chuỗi tiếng Việt có dấu thành không dấu để so khớp linh hoạt
     */
    public static String removeDiacritics(String text) {
        if (text == null) return "";
        String nfdNormalizedString = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString)
                .replaceAll("")
                .replace("đ", "d")
                .replace("Đ", "d");
    }
}

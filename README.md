# BÁO CÁO ĐỒ ÁN MÔN CÔNG NGHỆ JAVA
# ĐỀ TÀI: HỆ THỐNG QUẢN LÝ VÉ XE KHÁCH

---

## 1. THÀNH VIÊN NHÓM VÀ BẢNG PHÂN CÔNG 10 USE CASE
*Đồ án chia đều 10 Use Case cho 5 thành viên (mỗi bạn 2 Use Case hoàn chỉnh từ CSDL, Nghiệp vụ, Giao diện đến Kiểm thử).*

| STT | Họ và Tên | MSSV | Vai trò | Use Case & Chuyên đề phụ trách | Email Git Commit |
| :---: | :--- | :---: | :---: | :--- | :--- |
| **1** | **Vương Hoàng Tuấn Anh** | **2001230012** | **Nhóm trưởng** | • **UC01:** Đăng nhập, Xác thực & Phân quyền (`ROLE_ADMIN`, `ROLE_STAFF`, `ROLE_CUSTOMER`)<br>• **UC02:** Quản lý Tuyến xe (CRUD, Phân trang, Tìm kiếm, Ràng buộc xóa)<br>• *Kiến trúc:* Khởi tạo khung Spring Boot 3, ExceptionHandler `@ControllerAdvice`. | `vuonghoangtuananh6@gmail.com` |
| **2** | **Văn Công Trường** | **2001240528** | Thành viên | • **UC03:** Quản lý Xe & Sinh sơ đồ ghế tự động theo tầng<br>• **UC04:** Quản lý Lịch trình Chuyến xe (Kiểm tra xung đột lịch xe)<br>• *CSDL:* Thiết kế SQL Server (6 bảng, 20+ bản ghi mẫu/bảng), JPA Entity & Custom `@Query`. | `fromtruongvanwithlove@gmail.com` |
| **3** | **Trần Gia Phú** | **2001230672** | Thành viên | • **UC05:** Đặt vé & Khóa ghế đồng thời (**Tiêu chí 5.1 & 5.2 - Concurrency Lock**)<br>• **UC06:** Tác vụ nền tự động hủy vé quá hạn giữ chỗ (`@Scheduled` Background Task)<br>• *Kiểm thử:* Viết Unit Test 10 luồng song song chứng minh tính đúng đắn. | `phu07122005@gmail.com` |
| **4** | **Trần Nguyễn Khánh Ngọc** | **2001240300** | Thành viên | • **UC07:** Tra cứu & Tìm kiếm Chuyến xe khách (Bộ lọc theo ngày/điểm đi/điểm đến)<br>• **UC08:** Sơ đồ ghế trực quan & Quản lý vé của tôi (Thao tác hủy vé có Modal xác nhận)<br>• *UI/UX:* Layout Thymeleaf fragments, Bootstrap 5 responsive trên Mobile/Desktop. | `khanhngoc30062006@gmail.com` |
| **5** | **Trần Phan Minh Trí** | **2001240514** | Thành viên | • **UC09:** Desktop App Soát vé qua Socket TCP (**Tiêu chí 5.3 & 5.4 - Network Client**)<br>• **UC10:** Dashboard Báo cáo & Thống kê Doanh thu, số lượng vé theo trạng thái<br>• *Mạng:* Socket Server đa luồng (Thread Pool) phục vụ $\ge 3$ client đồng thời. | `tri.0398826045@gmail.com` |

---

## 2. YÊU CẦU MÔI TRƯỜNG & CÔNG NGHỆ
- **Ngôn ngữ & Framework:** Java 17 / 21 / 25, Spring Boot 3.3.4, Spring Data JPA, Hibernate.
- **Giao diện Web:** Thymeleaf Layout Dialect + Bootstrap 5.3 + Bootstrap Icons.
- **Lập trình mạng & Desktop:** Java Socket / ServerSocket đa luồng + Java Swing GUI.
- **Cơ sở dữ liệu:** Microsoft SQL Server (MSSQLSERVER).
- **Công cụ IDE:** Apache NetBeans IDE (mở trực tiếp file `pom.xml` của Maven).

---

## 3. TÀI KHOẢN TRẢI NGHIỆM HỆ THỐNG
Tất cả tài khoản mẫu đã được nạp sẵn trong file script CSDL với mật khẩu mặc định là `123456`:
- **Quản trị viên (Admin):** `admin` / `123456` (Toàn quyền quản lý tuyến, xe, chuyến, thống kê).
- **Nhân viên (Staff):** `staff_truong`, `staff_phu`, `staff_ngoc`, `staff_tri` / `123456` (Duyệt vé xe, soát vé).
- **Khách hàng (Customer):** `khach01`, `khach02`, ... đến `khach15` / `123456` (Tìm chuyến, đặt vé, xem vé của tôi).

---

## 4. HƯỚNG DẪN CÀI ĐẶT & CHẠY TRÊN NETBEANS IDE

### Bước 1: Khởi tạo Cơ sở dữ liệu SQL Server
1. Mở **SQL Server Management Studio (SSMS)**.
2. Kết nối tới SQL Server của bạn.
3. Mở file [schema_and_data.sql](file:///c:/A/java/doan/src/main/resources/database/schema_and_data.sql) nằm trong thư mục `src/main/resources/database/`.
4. Nhấn **Execute (F5)** để tự động tạo Database `QuanLyVeXe` cùng 6 bảng và hơn 120 bản ghi dữ liệu mẫu.

### Bước 2: Cấu hình thông số trong `application.properties`
Mở file [application.properties](file:///c:/A/java/doan/src/main/resources/application.properties) và chỉnh lại `username` / `password` nếu máy bạn dùng tài khoản khác:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=QuanLyVeXe;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=123456
```

### Bước 3: Mở và Chạy Web trên NetBeans IDE
1. Khởi động **Apache NetBeans IDE**.
2. Chọn menu **File -> Open Project...**.
3. Chọn thư mục `C:\A\java\doan` (NetBeans sẽ tự nhận diện dự án Maven với biểu tượng quả táo nhỏ hoặc chữ M).
4. Nhấp chuột phải vào dự án `quanlyvexe` -> Chọn **Run** (hoặc mở file [QuanlyvexeApplication.java](file:///c:/A/java/doan/src/main/java/com/nhom/quanlyvexe/QuanlyvexeApplication.java) -> nhấn `Shift + F6`).
5. Khi màn hình console hiện:
   ```
   Tomcat started on port 8080 (http)
   [SOCKET SERVER ĐA LUỒNG] Đang lắng nghe kết nối tại cổng: 8888
   ```
6. Truy cập ứng dụng tại trình duyệt: **http://localhost:8080**

### Bước 4: Chạy Demo Client Soát Vé Mạng (Tiêu chí 5.3 & 5.4)
1. Trong NetBeans, mở cây thư mục: `Source Packages -> com.nhom.quanlyvexe.network.client`.
2. Mở file [TicketInspectorClientApp.java](file:///c:/A/java/doan/src/main/java/com/nhom/quanlyvexe/network/client/TicketInspectorClientApp.java).
3. Nhấn tổ hợp phím **Shift + F6** để chạy ứng dụng Swing.
4. *Để chứng minh khả năng phục vụ $\ge 3$ client đồng thời theo Rubric:* Bạn có thể lặp lại thao tác **Shift + F6** 3 lần để mở 3 cửa sổ Client cùng lúc kết nối vào cổng `8888`.

### Bước 5: Chạy Kiểm Thử Đa Luồng Concurrency (Tiêu chí 5.2)
1. Trong NetBeans, mở thư mục `Test Packages -> com.nhom.quanlyvexe`.
2. Chuột phải vào file [ConcurrencyBookingTest.java](file:///c:/A/java/doan/src/test/java/com/nhom/quanlyvexe/ConcurrencyBookingTest.java) -> Chọn **Test File** (hoặc `Ctrl + F6`).
3. Xem kết quả trên màn hình: 10 luồng cùng tranh chấp 1 vị trí ghế, đúng 1 luồng thành công và 9 luồng bị chặn, đảm bảo an toàn giao dịch tuyệt đối.
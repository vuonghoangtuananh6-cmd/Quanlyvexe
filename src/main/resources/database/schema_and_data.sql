-- =====================================================================
-- KICH BAN TAO CO SO DU LIEU VA DU LIEU MAU: QUAN LY VE XE KHACH
-- DU LIEU TIENG VIET KHONG DAU (CHONG LOI FONT CHU TUYET DOI)
-- =====================================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'QuanLyVeXe')
BEGIN
    CREATE DATABASE QuanLyVeXe;
END
GO

USE QuanLyVeXe;
GO

-- 1. XOA BANG CU NEU DA TON TAI
IF OBJECT_ID('ve_xe', 'U') IS NOT NULL DROP TABLE ve_xe;
IF OBJECT_ID('chuyen_xe', 'U') IS NOT NULL DROP TABLE chuyen_xe;
IF OBJECT_ID('ghe', 'U') IS NOT NULL DROP TABLE ghe;
IF OBJECT_ID('xe', 'U') IS NOT NULL DROP TABLE xe;
IF OBJECT_ID('tuyen_xe', 'U') IS NOT NULL DROP TABLE tuyen_xe;
IF OBJECT_ID('nguoi_dung', 'U') IS NOT NULL DROP TABLE nguoi_dung;
GO

-- 2. BANG NGUOI DUNG
CREATE TABLE nguoi_dung (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    tai_khoan NVARCHAR(50) NOT NULL UNIQUE,
    mat_khau NVARCHAR(100) NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    email NVARCHAR(100) NOT NULL UNIQUE,
    so_dien_thoai NVARCHAR(15) NOT NULL,
    vai_tro NVARCHAR(20) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    ngay_tao DATETIME DEFAULT GETDATE()
);
GO

-- 3. BANG TUYEN XE
CREATE TABLE tuyen_xe (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_tuyen NVARCHAR(30) NOT NULL UNIQUE,
    ten_tuyen NVARCHAR(150) NOT NULL,
    diem_di NVARCHAR(100) NOT NULL,
    diem_den NVARCHAR(100) NOT NULL,
    quang_duong_km FLOAT NOT NULL,
    thoi_gian_du_kien NVARCHAR(50) NOT NULL,
    trang_thai BIT DEFAULT 1,
    hinh_anh NVARCHAR(500) NULL
);
GO

-- 4. BANG XE KHACH
CREATE TABLE xe (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    bien_so NVARCHAR(20) NOT NULL UNIQUE,
    ten_xe NVARCHAR(100) NOT NULL,
    loai_xe NVARCHAR(50) NOT NULL, -- Giuong nam, Limousine, Ghe ngoi
    tong_so_ghe INT NOT NULL,
    trang_thai NVARCHAR(30) DEFAULT 'Hoat dong'
);
GO

-- 5. BANG CHUYEN XE (LICH TRINH)
CREATE TABLE chuyen_xe (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_chuyen NVARCHAR(30) NOT NULL UNIQUE,
    tuyen_xe_id BIGINT NOT NULL,
    xe_id BIGINT NOT NULL,
    ngay_khoi_hanh DATE NOT NULL,
    gio_khoi_hanh TIME NOT NULL,
    gio_den_du_kien TIME NOT NULL,
    gia_ve DECIMAL(18, 0) NOT NULL,
    trang_thai NVARCHAR(50) DEFAULT 'Sap khoi hanh', -- Sap khoi hanh, Dang chay, Da hoan thanh, Da huy
    hinh_anh NVARCHAR(500) NULL,
    CONSTRAINT FK_ChuyenXe_TuyenXe FOREIGN KEY (tuyen_xe_id) REFERENCES tuyen_xe(id),
    CONSTRAINT FK_ChuyenXe_Xe FOREIGN KEY (xe_id) REFERENCES xe(id)
);
GO

-- 6. BANG GHE
CREATE TABLE ghe (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    xe_id BIGINT NOT NULL,
    so_ghe NVARCHAR(10) NOT NULL,
    tang INT DEFAULT 1,
    CONSTRAINT FK_Ghe_Xe FOREIGN KEY (xe_id) REFERENCES xe(id) ON DELETE CASCADE,
    CONSTRAINT UQ_Xe_SoGhe UNIQUE (xe_id, so_ghe)
);
GO

-- 7. BANG VE XE
CREATE TABLE ve_xe (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ma_ve NVARCHAR(30) NOT NULL UNIQUE,
    chuyen_xe_id BIGINT NOT NULL,
    nguoi_dung_id BIGINT NULL,
    so_ghe NVARCHAR(10) NOT NULL,
    ten_khach_hang NVARCHAR(100) NOT NULL,
    so_dien_thoai NVARCHAR(15) NOT NULL,
    email NVARCHAR(100) NULL,
    gia_ve DECIMAL(18, 0) NOT NULL,
    thoi_gian_dat DATETIME DEFAULT GETDATE(),
    trang_thai_thanh_toan NVARCHAR(50) DEFAULT 'Cho thanh toan', -- Cho thanh toan, Da thanh toan, Da len xe, Da huy
    ghi_chu NVARCHAR(255) NULL,
    ma_don_hang NVARCHAR(50) NULL,
    CONSTRAINT FK_VeXe_ChuyenXe FOREIGN KEY (chuyen_xe_id) REFERENCES chuyen_xe(id),
    CONSTRAINT FK_VeXe_NguoiDung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);
GO

-- =====================================================================
-- DU LIEU MAU CHUAN TIENG VIET KHONG DAU (TOI THIEU 20 BAN GHI / BANG)
-- =====================================================================

-- 1. BANG NGUOI_DUNG (20 ban ghi)
INSERT INTO nguoi_dung (tai_khoan, mat_khau, ho_ten, email, so_dien_thoai, vai_tro) VALUES
('admin', '123456', 'Vuong Hoang Tuan Anh', 'vuonghoangtuananh6@gmail.com', '0901234567', 'ROLE_ADMIN'),
('staff_truong', '123456', 'Van Cong Truong', 'fromtruongvanwithlove@gmail.com', '0912345678', 'ROLE_STAFF'),
('staff_phu', '123456', 'Tran Gia Phu', 'phu07122005@gmail.com', '0923456789', 'ROLE_STAFF'),
('staff_ngoc', '123456', 'Tran Nguyen Khanh Ngoc', 'khanhngoc30062006@gmail.com', '0934567890', 'ROLE_STAFF'),
('staff_tri', '123456', 'Tran Phan Minh Tri', 'tri.0398826045@gmail.com', '0945678901', 'ROLE_STAFF'),
('khach01', '123456', 'Nguyen Van An', 'nguyenvanan@gmail.com', '0981112233', 'ROLE_CUSTOMER'),
('khach02', '123456', 'Le Thi Binh', 'lethibinh@gmail.com', '0982223344', 'ROLE_CUSTOMER'),
('khach03', '123456', 'Tran Minh Cuong', 'tranminhcuong@gmail.com', '0983334455', 'ROLE_CUSTOMER'),
('khach04', '123456', 'Pham Thu Dung', 'phamthudung@gmail.com', '0984445566', 'ROLE_CUSTOMER'),
('khach05', '123456', 'Hoang Quoc Dung', 'hoangquocdung@gmail.com', '0985556677', 'ROLE_CUSTOMER'),
('khach06', '123456', 'Vu Thi Hong', 'vuthihong@gmail.com', '0986667788', 'ROLE_CUSTOMER'),
('khach07', '123456', 'Dang Thanh Hai', 'dangthanhhai@gmail.com', '0987778899', 'ROLE_CUSTOMER'),
('khach08', '123456', 'Bui Quang Huy', 'buiquanghuy@gmail.com', '0988889900', 'ROLE_CUSTOMER'),
('khach09', '123456', 'Ngo Thi Kieu', 'ngothikieu@gmail.com', '0989990011', 'ROLE_CUSTOMER'),
('khach10', '123456', 'Duong Van Long', 'duongvanlong@gmail.com', '0971112233', 'ROLE_CUSTOMER'),
('khach11', '123456', 'Ly Thi Mai', 'lythimai@gmail.com', '0972223344', 'ROLE_CUSTOMER'),
('khach12', '123456', 'Do Minh Nam', 'dominhnam@gmail.com', '0973334455', 'ROLE_CUSTOMER'),
('khach13', '123456', 'Ho Xuan Oanh', 'hoxuanoanh@gmail.com', '0974445566', 'ROLE_CUSTOMER'),
('khach14', '123456', 'Truong Cong Phuc', 'truongcongphuc@gmail.com', '0975556677', 'ROLE_CUSTOMER'),
('khach15', '123456', 'Cao Thi Quynh', 'caothiquynh@gmail.com', '0976667788', 'ROLE_CUSTOMER');
GO

-- 2. BANG TUYEN_XE (20 ban ghi)
INSERT INTO tuyen_xe (ma_tuyen, ten_tuyen, diem_di, diem_den, quang_duong_km, thoi_gian_du_kien) VALUES
('TX01', 'Sai Gon - Da Lat', 'TP. Ho Chi Minh', 'Da Lat (Lam Dong)', 305, '7 gio'),
('TX02', 'Sai Gon - Nha Trang', 'TP. Ho Chi Minh', 'Nha Trang (Khanh Hoa)', 435, '8 gio'),
('TX03', 'Sai Gon - Can Tho', 'TP. Ho Chi Minh', 'Can Tho', 165, '3.5 gio'),
('TX04', 'Sai Gon - Vung Tau', 'TP. Ho Chi Minh', 'Vung Tau', 100, '2.5 gio'),
('TX05', 'Sai Gon - Phan Thiet', 'TP. Ho Chi Minh', 'Phan Thiet (Binh Thuan)', 210, '4.5 gio'),
('TX06', 'Sai Gon - Buon Ma Thuot', 'TP. Ho Chi Minh', 'Buon Ma Thuot (Dak Lak)', 350, '8 gio'),
('TX07', 'Sai Gon - Quy Nhon', 'TP. Ho Chi Minh', 'Quy Nhon (Binh Dinh)', 650, '12 gio'),
('TX08', 'Sai Gon - Rach Gia', 'TP. Ho Chi Minh', 'Rach Gia (Kien Giang)', 250, '5.5 gio'),
('TX09', 'Ha Noi - Sapa', 'Ha Noi', 'Sapa (Lao Cai)', 315, '6 gio'),
('TX10', 'Ha Noi - Hai Phong', 'Ha Noi', 'Hai Phong', 120, '1.5 gio'),
('TX11', 'Ha Noi - Quang Ninh', 'Ha Noi', 'Ha Long (Quang Ninh)', 160, '2.5 gio'),
('TX12', 'Ha Noi - Da Nang', 'Ha Noi', 'Da Nang', 760, '14 gio'),
('TX13', 'Ha Noi - Vinh', 'Ha Noi', 'Vinh (Nghe An)', 300, '5.5 gio'),
('TX14', 'Ha Noi - Ninh Binh', 'Ha Noi', 'Ninh Binh', 95, '1.5 gio'),
('TX15', 'Da Nang - Hue', 'Da Nang', 'Hue', 100, '2 gio'),
('TX16', 'Da Nang - Quy Nhon', 'Da Nang', 'Quy Nhon', 320, '6 gio'),
('TX17', 'Can Tho - Rach Gia', 'Can Tho', 'Rach Gia', 110, '2.5 gio'),
('TX18', 'Can Tho - Ca Mau', 'Can Tho', 'Ca Mau', 180, '3.5 gio'),
('TX19', 'Nha Trang - Da Lat', 'Nha Trang', 'Da Lat', 135, '3 gio'),
('TX20', 'Da Lat - Buon Ma Thuot', 'Da Lat', 'Buon Ma Thuot', 210, '5 gio');
GO

-- 3. BANG XE (20 ban ghi)
INSERT INTO xe (bien_so, ten_xe, loai_xe, tong_so_ghe, trang_thai) VALUES
('51B-123.45', 'Thaco Mobihome 01', 'Giuong nam', 34, 'Hoat dong'),
('51B-123.46', 'Thaco Mobihome 02', 'Giuong nam', 34, 'Hoat dong'),
('51B-234.56', 'Hyundai Universe 01', 'Ghe ngoi', 29, 'Hoat dong'),
('51B-234.57', 'Hyundai Universe 02', 'Ghe ngoi', 29, 'Hoat dong'),
('51B-345.67', 'Limousine VIP 01', 'Limousine', 22, 'Hoat dong'),
('51B-345.68', 'Limousine VIP 02', 'Limousine', 22, 'Hoat dong'),
('29B-456.78', 'Thaco King Long 01', 'Giuong nam', 34, 'Hoat dong'),
('29B-456.79', 'Thaco King Long 02', 'Giuong nam', 34, 'Hoat dong'),
('29B-567.89', 'Hyundai Tracomeco 01', 'Ghe ngoi', 29, 'Hoat dong'),
('29B-567.90', 'Hyundai Tracomeco 02', 'Ghe ngoi', 29, 'Hoat dong'),
('43B-678.90', 'Thaco Mobihome Mien Trung', 'Giuong nam', 34, 'Hoat dong'),
('43B-678.91', 'DCar Limousine Da Nang', 'Limousine', 22, 'Hoat dong'),
('65B-789.01', 'Thaco Can Tho Express', 'Giuong nam', 34, 'Hoat dong'),
('65B-789.02', 'Samco Can Tho 01', 'Ghe ngoi', 29, 'Hoat dong'),
('49B-890.12', 'Thaco Da Lat Vip', 'Limousine', 22, 'Hoat dong'),
('49B-890.13', 'Universe Suong Mu', 'Giuong nam', 34, 'Hoat dong'),
('79B-901.23', 'Thaco Bien Xanh Nha Trang', 'Giuong nam', 34, 'Hoat dong'),
('79B-901.24', 'Limousine Nha Trang', 'Limousine', 22, 'Hoat dong'),
('77B-012.34', 'Thaco Xu Nau Quy Nhon', 'Giuong nam', 34, 'Hoat dong'),
('14B-112.23', 'Limousine Dat Mo Ha Long', 'Limousine', 22, 'Hoat dong');
GO

-- 4. BANG CHUYEN_XE (20 ban ghi)
INSERT INTO chuyen_xe (ma_chuyen, tuyen_xe_id, xe_id, ngay_khoi_hanh, gio_khoi_hanh, gio_den_du_kien, gia_ve, trang_thai) VALUES
('CX01', 1, 1, '2026-11-01', '08:00', '15:00', 280000, 'Sap khoi hanh'),
('CX02', 1, 2, '2026-11-01', '22:00', '05:00', 300000, 'Sap khoi hanh'),
('CX03', 2, 5, '2026-11-01', '09:00', '17:00', 350000, 'Sap khoi hanh'),
('CX04', 3, 3, '2026-11-01', '07:30', '11:00', 160000, 'Sap khoi hanh'),
('CX05', 4, 4, '2026-11-01', '06:00', '08:30', 140000, 'Sap khoi hanh'),
('CX06', 5, 6, '2026-11-02', '13:00', '17:30', 220000, 'Sap khoi hanh'),
('CX07', 6, 1, '2026-11-02', '20:00', '04:00', 320000, 'Sap khoi hanh'),
('CX08', 7, 2, '2026-11-02', '18:00', '06:00', 400000, 'Sap khoi hanh'),
('CX09', 9, 7, '2026-11-01', '07:00', '13:00', 320000, 'Sap khoi hanh'),
('CX10', 9, 8, '2026-11-01', '22:30', '04:30', 350000, 'Sap khoi hanh'),
('CX11', 10, 9, '2026-11-01', '08:30', '10:00', 120000, 'Sap khoi hanh'),
('CX12', 11, 10, '2026-11-01', '14:00', '16:30', 180000, 'Sap khoi hanh'),
('CX13', 12, 7, '2026-11-02', '17:00', '07:00', 500000, 'Sap khoi hanh'),
('CX14', 13, 8, '2026-11-02', '08:00', '13:30', 230000, 'Sap khoi hanh'),
('CX15', 14, 9, '2026-11-02', '09:00', '10:30', 100000, 'Sap khoi hanh'),
('CX16', 15, 11, '2026-11-01', '08:00', '10:00', 110000, 'Sap khoi hanh'),
('CX17', 16, 12, '2026-11-01', '13:30', '19:30', 250000, 'Sap khoi hanh'),
('CX18', 17, 13, '2026-11-01', '07:00', '09:30', 130000, 'Sap khoi hanh'),
('CX19', 19, 17, '2026-11-01', '08:00', '11:00', 170000, 'Sap khoi hanh'),
('CX20', 20, 15, '2026-11-01', '12:00', '17:00', 200000, 'Sap khoi hanh');
GO

-- 5. BANG GHE (Tu dong sinh day du tat ca cac ghe cho 20 xe theo so cho va tang)
DECLARE @xeId BIGINT, @loaiXe NVARCHAR(50), @tongSoGhe INT;
DECLARE @i INT, @perFloor INT;

DECLARE xe_cursor CURSOR FOR
    SELECT id, loai_xe, tong_so_ghe FROM xe;

OPEN xe_cursor;
FETCH NEXT FROM xe_cursor INTO @xeId, @loaiXe, @tongSoGhe;

WHILE @@FETCH_STATUS = 0
BEGIN
    IF @loaiXe LIKE N'%Ghe ngoi%' OR @loaiXe LIKE N'%ngồi%'
    BEGIN
        SET @i = 1;
        WHILE @i <= @tongSoGhe
        BEGIN
            INSERT INTO ghe (xe_id, so_ghe, tang)
            VALUES (@xeId, RIGHT('00' + CAST(@i AS VARCHAR(10)), 2), 1);
            SET @i = @i + 1;
        END
    END
    ELSE
    BEGIN
        SET @perFloor = @tongSoGhe / 2;
        SET @i = 1;
        WHILE @i <= @perFloor
        BEGIN
            INSERT INTO ghe (xe_id, so_ghe, tang)
            VALUES (@xeId, 'A' + RIGHT('00' + CAST(@i AS VARCHAR(10)), 2), 1);
            SET @i = @i + 1;
        END

        SET @i = 1;
        WHILE @i <= (@tongSoGhe - @perFloor)
        BEGIN
            INSERT INTO ghe (xe_id, so_ghe, tang)
            VALUES (@xeId, 'B' + RIGHT('00' + CAST(@i AS VARCHAR(10)), 2), 2);
            SET @i = @i + 1;
        END
    END

    FETCH NEXT FROM xe_cursor INTO @xeId, @loaiXe, @tongSoGhe;
END

CLOSE xe_cursor;
DEALLOCATE xe_cursor;
GO

-- 6. BANG VE_XE (20 ban ghi)
INSERT INTO ve_xe (ma_ve, chuyen_xe_id, nguoi_dung_id, so_ghe, ten_khach_hang, so_dien_thoai, email, gia_ve, thoi_gian_dat, trang_thai_thanh_toan) VALUES
('VE2026110101', 1, 6, 'A01', 'Nguyen Van An', '0981112233', 'nguyenvanan@gmail.com', 280000, '2026-10-15 08:30:00', 'Da thanh toan'),
('VE2026110102', 1, 7, 'A02', 'Le Thi Binh', '0982223344', 'lethibinh@gmail.com', 280000, '2026-10-15 09:15:00', 'Da thanh toan'),
('VE2026110103', 1, 8, 'A03', 'Tran Minh Cuong', '0983334455', 'tranminhcuong@gmail.com', 280000, '2026-10-16 10:00:00', 'Cho thanh toan'),
('VE2026110104', 1, 9, 'B01', 'Pham Thu Dung', '0984445566', 'phamthudung@gmail.com', 280000, '2026-10-16 14:20:00', 'Da len xe'),
('VE2026110105', 2, 10, 'A01', 'Hoang Quoc Dung', '0985556677', 'hoangquocdung@gmail.com', 300000, '2026-10-17 08:00:00', 'Da thanh toan'),
('VE2026110106', 2, 11, 'A02', 'Vu Thi Hong', '0986667788', 'vuthihong@gmail.com', 300000, '2026-10-17 11:45:00', 'Da thanh toan'),
('VE2026110107', 3, 12, 'A01', 'Dang Thanh Hai', '0987778899', 'dangthanhhai@gmail.com', 350000, '2026-10-18 07:30:00', 'Da thanh toan'),
('VE2026110108', 3, 13, 'A02', 'Bui Quang Huy', '0988889900', 'buiquanghuy@gmail.com', 350000, '2026-10-18 09:10:00', 'Cho thanh toan'),
('VE2026110109', 4, 14, '01', 'Ngo Thi Kieu', '0989990011', 'ngothikieu@gmail.com', 160000, '2026-10-18 15:00:00', 'Da len xe'),
('VE2026110110', 4, 15, '02', 'Duong Van Long', '0971112233', 'duongvanlong@gmail.com', 160000, '2026-10-18 16:30:00', 'Da thanh toan'),
('VE2026110111', 5, 16, '01', 'Ly Thi Mai', '0972223344', 'lythimai@gmail.com', 140000, '2026-10-19 08:20:00', 'Da thanh toan'),
('VE2026110112', 6, 17, 'A01', 'Do Minh Nam', '0973334455', 'dominhnam@gmail.com', 220000, '2026-10-19 10:15:00', 'Da thanh toan'),
('VE2026110113', 7, 18, 'A01', 'Ho Xuan Oanh', '0974445566', 'hoxuanoanh@gmail.com', 320000, '2026-10-19 13:40:00', 'Cho thanh toan'),
('VE2026110114', 8, 19, 'A01', 'Truong Cong Phuc', '0975556677', 'truongcongphuc@gmail.com', 400000, '2026-10-19 17:00:00', 'Da thanh toan'),
('VE2026110115', 9, 20, 'A01', 'Cao Thi Quynh', '0976667788', 'caothiquynh@gmail.com', 320000, '2026-10-20 08:00:00', 'Da thanh toan'),
('VE2026110116', 10, 6, 'A01', 'Nguyen Van An', '0981112233', 'nguyenvanan@gmail.com', 350000, '2026-10-20 09:20:00', 'Da len xe'),
('VE2026110117', 11, 7, '01', 'Le Thi Binh', '0982223344', 'lethibinh@gmail.com', 120000, '2026-10-20 10:10:00', 'Da thanh toan'),
('VE2026110118', 12, 8, '01', 'Tran Minh Cuong', '0983334455', 'tranminhcuong@gmail.com', 180000, '2026-10-20 11:30:00', 'Da thanh toan'),
('VE2026110119', 13, 9, 'A01', 'Pham Thu Dung', '0984445566', 'phamthudung@gmail.com', 500000, '2026-10-20 13:00:00', 'Cho thanh toan'),
('VE2026110120', 14, 10, 'A01', 'Hoang Quoc Dung', '0985556677', 'hoangquocdung@gmail.com', 230000, '2026-10-20 14:15:00', 'Da thanh toan');
GO

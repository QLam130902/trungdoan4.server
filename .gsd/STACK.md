# Danh mục Công nghệ (Technology Stack)

> Tự động tạo bởi Antigravity thông qua quy trình `/map` vào ngày 20/05/2026

## 1. Nền tảng Chạy (Runtime Environment)

| Công nghệ | Phiên bản | Vai trò |
|-----------|-----------|---------|
| **Java** | `17` | Ngôn ngữ lập trình chính và máy ảo thực thi |
| **Spring Boot** | `3.4.3` | Khung ứng dụng chính (Framework) phục vụ viết APIs và cấu hình |
| **MySQL** | `8` | Hệ quản trị cơ sở dữ liệu quan hệ lưu trữ dữ liệu hệ thống |

---

## 2. Công nghệ Cốt lõi (Core Technologies)

### Phía Backend (Dự án hiện tại)
| Tính năng | Thư viện / Nơi cấu hình | Vai trò |
|-----------|-------------------------|---------|
| **REST APIs** | Spring MVC (`spring-boot-starter-web`) | Định nghĩa các bộ định tuyến APIs và nhận tham số HTTP |
| **Data Access (JPA)** | Spring Data JPA + Hibernate | Tự động sinh bảng và thực thi các câu lệnh SQL |
| **Security & Auth** | Spring Security (`spring-boot-starter-security`) | Lọc bảo mật, mã hóa mật khẩu BCrypt |
| **Token JWT** | Auth0 Java JWT (`com.auth0:java-jwt`) | Khởi tạo và xác thực mã chữ ký số JWT |

### Phía Frontend (Dự án kết hợp)
Dự án frontend nằm tại thư mục [c:\Users\PC\projects\trungdoan4.online](file:///c:/Users/PC/projects/trungdoan4.online) (tên cũ là `frontend` ở `c:\Users\PC\projects\DanVanKheoTrungDoan4\frontend`):
*   **Framework**: React 18
*   **Build Tool**: Vite 5
*   **Charts**: Chart.js và React Chartjs 2 (vẽ biểu đồ Dashboard quản trị)
*   **Excel**: ExcelJS, File Saver, XLSX (phục vụ kết xuất danh sách góp ý ra file Excel)

---

## 3. Các thư viện phụ thuộc (Dependencies)

Các phụ thuộc được khai báo trong tệp cấu hình Maven [pom.xml](file:///c:/Users/PC/projects/trungdoan4.server/pom.xml):

### Phụ thuộc Sản xuất (Production Dependencies)
| Nhóm / ID Thư viện | Phiên bản | Vai trò |
|-------------------|-----------|---------|
| `org.springframework.boot:spring-boot-starter-web` | *Theo parent* | Phục vụ xây dựng các API RESTful |
| `org.springframework.boot:spring-boot-starter-data-jpa` | *Theo parent* | Kết nối cơ sở dữ liệu qua Hibernate và JPA |
| `org.springframework.boot:spring-boot-starter-security` | *Theo parent* | Phân quyền truy cập ứng dụng |
| `com.mysql:mysql-connector-j` | `9.2.0` | Driver kết nối MySQL Database |
| `com.auth0:java-jwt` | `4.4.0` | Xử lý token JWT |

### Phụ thuộc Phát triển (Development Dependencies)
| Nhóm / ID Thư viện | Phiên bản | Vai trò |
|-------------------|-----------|---------|
| `org.springframework.boot:spring-boot-devtools` | *Optional* | Tự động reload ứng dụng khi thay đổi mã nguồn trên local |
| `org.springframework.boot:spring-boot-starter-test` | *Test* | Hỗ trợ viết kiểm thử đơn vị và tích hợp |

---

## 4. Cơ sở hạ tầng (Infrastructure)

| Dịch vụ | Nhà cung cấp | Vai trò |
|---------|--------------|---------|
| **Server VPS** | 160.187.229.25 | Máy chủ ảo chạy hệ điều hành Ubuntu 22.04 LTS chứa Backend và DB |
| **Reverse Proxy** | Nginx | Nhận request HTTPS từ client ở cổng 443 và chuyển tiếp đến Spring Boot ở cổng 8080 |
| **SSL Certificate** | Let's Encrypt | Cấp chứng chỉ bảo mật HTTPS cho domain `api.sudoan5.io.vn` |
| **Hosting Frontend**| GitHub Pages | Lưu trữ và phục vụ giao diện tĩnh tại `sudoan5.io.vn` |
| **Service Manager** | Systemd (todoapp) | Đảm bảo ứng dụng chạy ngầm liên tục trên VPS và tự động khởi động lại |

*   **Repository Backend**: `https://github.com/QLam130902/trungdoan4.server`
*   **Repository Frontend**: `https://github.com/QLam130902/trungdoan4.online`

---

## 5. Các biến cấu hình (Configuration)

Các tham số cấu hình hệ thống được lưu trữ trong các file tài nguyên:
*   [application.properties](file:///c:/Users/PC/projects/trungdoan4.server/src/main/resources/application.properties): Cấu hình profile hoạt động mặc định (`spring.profiles.active=dev`).
*   [application-dev.properties](file:///c:/Users/PC/projects/trungdoan4.server/src/main/resources/application-dev.properties): Cấu hình chạy local kết nối tới MySQL `localhost:3306/db_homthugopy` (User: `root`, Pass: `123456`).
*   [application-prod.properties](file:///c:/Users/PC/projects/trungdoan4.server/src/main/resources/application-prod.properties): Cấu hình chạy VPS kết nối tới MySQL `127.0.0.1:3306/db_homthugopy` (User: `admin`, Pass: `aATrungdoan4aA@`).

---

## 6. Thống kê Quy mô Mã nguồn (File Size Inventory)

| Danh mục | Số lượng tệp | Số dòng code (ước tính) | Vai trò |
|----------|--------------|-------------------------|---------|
| **Java Sources (Main)** | 22 | ~1200 | Mã nguồn xử lý nghiệp vụ chính |
| **Java Sources (Test)** | 3 | ~100 | Kiểm thử đơn vị |
| **Configuration (XML/Prop)**| 4 | ~120 | Cấu hình dự án và cơ sở dữ liệu |
| **Documentation (MD)** | 8 | ~1200 | Tài liệu hướng dẫn deploy, rules, quy chuẩn |
| **Tổng cộng** | **37** | **~2620** | |

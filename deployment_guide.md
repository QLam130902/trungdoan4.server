# 📋 TÀI LIỆU HƯỚNG DẪN DEPLOY
## Hệ thống Hòm thư góp ý số — Sư đoàn 5, Quân khu 7
*Phiên bản: 1.2 | Cập nhật: 05/05/2026 | Tài liệu MẬT — Lưu trữ cẩn thận*

---

## I. KIẾN TRÚC TỔNG THỂ

```
NGƯỜI DÙNG (Điện thoại / Máy tính)
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│         DNS: sudoan5.io.vn                                  │
│  A record (@)   → GitHub Pages (tự động)                   │
│  A record (api) → 160.187.229.25   (VPS)                   │
└──────────────┬──────────────────────────┬───────────────────┘
               │                          │
               ▼                          ▼
  ┌────────────────────┐     ┌────────────────────────────┐
  │  FRONTEND          │     │  BACKEND (VPS Ubuntu 22.04)│
  │  GitHub Pages      │     │  IP: 160.187.229.25        │
  │  sudoan5.io.vn     │────▶│  api.sudoan5.io.vn         │
  │  (React + Vite)    │     │  Nginx → cổng 8080         │
  └────────────────────┘     │  Spring Boot 3.4 (Java)    │
                             │  MySQL 8 (db_homthugopy)   │
                             └────────────────────────────┘
```

**Luồng dữ liệu:**
1. Người dùng truy cập `https://sudoan5.io.vn` → GitHub Pages phục vụ giao diện React
2. Giao diện gọi API tới `https://api.sudoan5.io.vn/suggestions/...`
3. DNS trỏ `api.` về VPS → Nginx nhận và chuyển tiếp (proxy) về Spring Boot cổng 8080
4. Spring Boot xử lý, truy vấn MySQL, trả kết quả về

---

## II. CẤU TRÚC KHO MÃ NGUỒN (GIT)

### Frontend: `QLam130902/trungdoan4.online`
| Nhánh | Mục đích |
|---|---|
| `local` | Nhánh code chính hàng ngày |
| `main` | **Nhánh deploy** — push vào đây kích hoạt GitHub Actions |
| `deploy/*` | Nhánh trigger CI/CD (ví dụ: `deploy/sudoan5`) |
| `gh-pages` | Nhánh kết quả build (tự động, không sửa thủ công) |

### Backend: `QLam130902/trungdoan4.server`
| Nhánh | Mục đích |
|---|---|
| `local` | Nhánh code chính hàng ngày |
| `main` | Nhánh ổn định |

---

## III. QUY TRÌNH DEPLOY FRONTEND (Từng bước)

### Điều kiện trước khi deploy
- [ ] Code chạy đúng trên local (`npm run dev`)
- [ ] File `.env.production`: `VITE_API_BASE_URL=https://api.sudoan5.io.vn`
- [ ] File `public/CNAME` có nội dung: `sudoan5.io.vn`
- [ ] `vite.config.js` có `base: '/'`

### Các bước thực hiện

**Bước 1: Commit code**
```bash
# Mở Terminal tại: C:\Users\PC\projects\trungdoan4.online
git add .
git commit -m "mô tả thay đổi"
```

**Bước 2: Push để kích hoạt deploy**
```bash
git push origin local:deploy/ten-nhanh
# Ví dụ: git push origin local:deploy/update-ui
```

**Bước 3: Theo dõi deploy tự động**
- Truy cập: `https://github.com/QLam130902/trungdoan4.online/actions`
- Chờ dấu ✅ xanh (thường 1-3 phút)

**Bước 4: Xác nhận thành công**
- Mở `https://sudoan5.io.vn`, bấm **Ctrl+F5** để xóa cache

---

## IV. QUY TRÌNH DEPLOY BACKEND (Từng bước)

### Điều kiện trước khi deploy
- [ ] Code chạy đúng trên local (`.\mvnw spring-boot:run`)
- [ ] Đã commit code

### Các bước thực hiện

**Bước 1: Commit code**
```bash
# Mở Terminal tại: C:\Users\PC\projects\trungdoan4.server
git add .
git commit -m "mô tả thay đổi"
git push origin local
```

**Bước 2: Build file JAR**
```bash
.\mvnw clean package -DskipTests
# Kết quả: target\todo-0.0.1-SNAPSHOT.jar (~54MB, khoảng 30-60 giây)
```

**Bước 3: Copy file JAR lên VPS**
```bash
# Chạy tại CMD Windows MỚI (KHÔNG phải trong VPS)
scp C:\Users\PC\projects\trungdoan4.server\target\todo-0.0.1-SNAPSHOT.jar root@160.187.229.25:/root/
```
> Nếu SCP lỗi: Dùng **WinSCP** (winscp.net) — Host `160.187.229.25`, User `root`, kéo thả file JAR vào `/root/`

**Bước 4: Khởi động lại service trên VPS**
```bash
# SSH vào VPS từ CMD Windows
ssh root@160.187.229.25

# Sau khi vào VPS:
systemctl restart todoapp
systemctl status todoapp
# Chờ 30-40 giây → kết quả: Active: active (running)
```

**Bước 5: Kiểm tra hoạt động**
```bash
curl -s https://api.sudoan5.io.vn/suggestions/lookup/test
```

---

## V. CẤU HÌNH VPS — CHI TIẾT

### 5.1 Thông tin máy chủ
| Thông số | Giá trị |
|---|---|
| Hệ điều hành | Ubuntu 22.04 LTS |
| IP Public | `160.187.229.25` |
| Vị trí file JAR | `/root/todo-0.0.1-SNAPSHOT.jar` |
| Java | `/usr/bin/java` |
| Database | MySQL 8, port 3306, DB: `db_homthugopy` |

### 5.2 Cấu hình Systemd Service
File: `/etc/systemd/system/todoapp.service`

> ⚠️ **QUAN TRỌNG:** `--spring.profiles.active=prod` là **BẮT BUỘC**. Nếu thiếu, Spring Boot dùng profile `dev` → không kết nối database → crash.

```ini
[Unit]
Description=TrungDoan4 Backend API Service
After=network.target

[Service]
User=root
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /root/todo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**Lệnh quản lý service:**
```bash
systemctl start todoapp
systemctl stop todoapp
systemctl restart todoapp
systemctl status todoapp
systemctl daemon-reload          # Sau khi sửa file .service
journalctl -u todoapp -f         # Log realtime
journalctl -u todoapp -n 80 --no-pager  # 80 dòng log gần nhất
```

### 5.3 Cấu hình Nginx hiện tại
File: `/etc/nginx/sites-available/sudoan5.io.vn`

```nginx
server {
    listen 80;
    server_name api.sudoan5.io.vn;
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    # Certbot tự thêm phần HTTPS bên dưới
    listen 443 ssl;
    ssl_certificate /etc/letsencrypt/live/api.sudoan5.io.vn/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.sudoan5.io.vn/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;
}
server {
    if ($host = api.sudoan5.io.vn) { return 301 https://$host$request_uri; }
    listen 80;
    server_name api.sudoan5.io.vn;
    return 404;
}
```

**Lệnh quản lý Nginx:**
```bash
nginx -t                                    # LUÔN kiểm tra trước khi reload
systemctl reload nginx
systemctl restart nginx
systemctl status nginx
```

### 5.4 Chứng chỉ SSL
- Domain: `api.sudoan5.io.vn` | Hết hạn: **03/08/2026** (tự gia hạn)

```bash
certbot --nginx -d <domain>       # Cài SSL cho domain mới
certbot renew --dry-run           # Kiểm tra tự động gia hạn
certbot delete --cert-name <domain>  # Xóa chứng chỉ cũ
```
> ⚠️ Sau khi xóa chứng chỉ → phải xóa symlink Nginx tương ứng, nếu không Nginx không reload được

---

## VI. HƯỚNG DẪN ĐỔI TÊN MIỀN (Chi tiết)

> Phần này ghi lại đầy đủ quy trình thực hiện khi cần chuyển hệ thống sang tên miền mới (ví dụ đã thực hiện: `trungdoan4.io.vn` → `sudoan5.io.vn`).

### Bước 1 — Cấu hình DNS (Tại nhà cung cấp tên miền)

Tạo các DNS records cho tên miền mới:

| Loại | Tên | Giá trị | Mục đích |
|---|---|---|---|
| `A` | `@` (hoặc tên miền gốc) | `160.187.229.25` | Trỏ về VPS (cho API) |
| `A` | `api` | `160.187.229.25` | Subdomain API |
| `CNAME` | `www` | `tenmien.io.vn` | Alias www |

Kiểm tra DNS đã thông:
```bash
nslookup sudoan5.io.vn
nslookup api.sudoan5.io.vn
# Kết quả phải trả về: Address: 160.187.229.25
```
> DNS thường thông trong 5–30 phút, tối đa 24 giờ.

### Bước 2 — Cập nhật code trên Local

**Frontend — file `.env.production`:**
```env
VITE_API_BASE_URL=https://api.tenmien-moi.io.vn
```

**Frontend — file `public/CNAME`** *(tạo mới nếu chưa có)*:
```
tenmien-moi.io.vn
```

**Backend — file `SecurityConfig.java`** (thêm domain mới vào CORS):
```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://tenmien-moi.io.vn",     // Domain mới
    "https://qlam130902.github.io",  // GitHub Pages gốc (dự phòng)
    "http://localhost:5173"
));
```

### Bước 3 — Commit & Push

```bash
# Frontend
git add .
git commit -m "Deploy: Chuyen sang ten mien moi"
git push origin local:deploy/ten-mien-moi

# Backend
git add .
git commit -m "Update CORS for new domain"
git push origin local
```

### Bước 4 — Cài Custom Domain trên GitHub Pages

1. Vào `GitHub → Repository trungdoan4.online → Settings → Pages`
2. Phần **Custom domain** nhập: `tenmien-moi.io.vn`
3. Bấm **Save**
4. Chờ GitHub tự cấp SSL (10–30 phút) → Tick vào **Enforce HTTPS**

### Bước 5 — Cấu hình Nginx trên VPS

SSH vào VPS: `ssh root@160.187.229.25`

Tạo file cấu hình Nginx mới:
```bash
nano /etc/nginx/sites-available/tenmien-moi.io.vn
```

Nội dung:
```nginx
server {
    listen 80;
    server_name api.tenmien-moi.io.vn;
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Kích hoạt và kiểm tra:
```bash
ln -s /etc/nginx/sites-available/tenmien-moi.io.vn /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx
```

### Bước 6 — Cấp SSL cho tên miền mới

```bash
certbot --nginx -d api.tenmien-moi.io.vn
```
Certbot sẽ tự cấu hình HTTPS và gia hạn tự động.

### Bước 7 — Build và Upload Backend mới lên VPS

```bash
# Trên máy local:
.\mvnw clean package -DskipTests

# Upload (terminal Windows mới, KHÔNG phải SSH):
scp C:\Users\PC\projects\trungdoan4.server\target\todo-0.0.1-SNAPSHOT.jar root@160.187.229.25:/root/

# Trên VPS:
systemctl restart todoapp
```

### Bước 8 — Kiểm tra toàn diện

```bash
# Kiểm tra API trả về (lỗi 401 là đúng vì cần đăng nhập):
curl -s https://api.tenmien-moi.io.vn/suggestions

# Kiểm tra Frontend:
# Mở trình duyệt: https://tenmien-moi.io.vn
```

---

## VII. CẤU HÌNH DNS — THAM KHẢO

| Loại | Host | Giá trị | Mục đích |
|---|---|---|---|
| **A** | `api` | `160.187.229.25` | Backend API → VPS |
| **A** | `@` | `160.187.229.25` | Frontend (nếu không dùng GitHub Pages redirect) |
| **CNAME** | `www` | `tenmien.io.vn` | Alias www |

> DNS thay đổi cần 15 phút đến vài tiếng để có hiệu lực toàn cầu.

---

## VIII. CÁC LỖI ĐÃ GẶP VÀ CÁCH KHẮC PHỤC

### ❌ Lỗi 1: Màn hình trắng sau khi deploy
**Nguyên nhân:** `vite.config.js` có `base: '/trungdoan4.online/'`  
**Khắc phục:** Sửa thành `base: '/'`, commit và push lại

### ❌ Lỗi 2: GitHub Actions không tự chạy
**Nguyên nhân:** File `.github/workflows/deploy.yml` chưa có trên nhánh `main`  
**Khắc phục:** Kích hoạt thủ công: Actions → Run workflow → Chọn nhánh `main`

### ❌ Lỗi 3: API trả về 502 Bad Gateway
**Kiểm tra:**
```bash
systemctl status todoapp
nginx -t
curl -s http://localhost:8080/suggestions/lookup/test
journalctl -u todoapp -n 50 --no-pager
```
**Nếu Nginx lỗi SSL cũ:**
```bash
grep -r "domain-cu" /etc/nginx/
rm /etc/nginx/sites-enabled/domain-cu
nginx -t && systemctl reload nginx
```

### ❌ Lỗi 4: Spring Boot crash — "Unable to determine Dialect"
**Nguyên nhân:** Thiếu `--spring.profiles.active=prod` trong file service  
**Khắc phục:**
```bash
nano /etc/systemd/system/todoapp.service  # Thêm --spring.profiles.active=prod
systemctl daemon-reload && systemctl restart todoapp
```

### ❌ Lỗi 5: Nginx không reload — SSL certificate not found
**Nguyên nhân:** File cấu hình Nginx cũ vẫn còn trỏ đến chứng chỉ của tên miền đã xóa  
**Khắc phục:**
```bash
# Xóa symlink cũ
rm /etc/nginx/sites-enabled/ten-mien-cu
nginx -t && systemctl reload nginx
```

### ❌ Lỗi 6: SCP — "No such file or directory"
**Nguyên nhân:** Chạy lệnh SCP từ bên trong VPS  
**Khắc phục:** Mở CMD mới trên Windows (không phải cửa sổ SSH):
```bash
scp C:\Users\PC\projects\trungdoan4.server\target\todo-0.0.1-SNAPSHOT.jar root@160.187.229.25:/root/
```

### ❌ Lỗi 7: GitHub Pages HTTPS "Not yet available"
**Nguyên nhân:** GitHub đang cấp chứng chỉ SSL cho tên miền mới, cần thời gian  
**Khắc phục:** Chờ 10–30 phút rồi reload trang GitHub Settings → Pages

### ❌ Lỗi 8: API bị chặn do CORS
**Nguyên nhân:** Domain mới chưa được thêm vào `SecurityConfig.java`  
**Khắc phục:** Thêm domain vào `allowedOrigins`, build lại JAR và upload lên VPS

---

## IX. CHECKLIST KIỂM TRA SAU DEPLOY

### Frontend
- [ ] `https://sudoan5.io.vn` hiện lên bình thường, có ổ khóa xanh (HTTPS)
- [ ] Gửi 1 góp ý → Nhận mã tra cứu GY-XXXXXX
- [ ] Tra cứu bằng mã → Hiện đúng nội dung

### Backend & Admin
- [ ] Đăng nhập `https://sudoan5.io.vn/#/admin/login` thành công
- [ ] Dashboard hiển thị biểu đồ
- [ ] Danh sách góp ý, phân trang hoạt động
- [ ] Phản hồi góp ý → Lưu thành công
- [ ] Kiểm tra trên Mobile (mạng 4G, không phải Wifi)

### Trên VPS
```bash
systemctl status todoapp nginx
ss -tlnp | grep -E "80|443|8080"
curl -s https://api.sudoan5.io.vn/suggestions | head -c 100
```

---

## X. CÁCH TRUY CẬP VPS

**Phương pháp 1: SSH từ CMD Windows**
```bash
ssh root@160.187.229.25
```

**Phương pháp 2: WinSCP** — Host `160.187.229.25`, User `root`, Protocol SFTP  
*(Dùng để kéo thả file JAR lên VPS khi SCP gặp lỗi)*

**Phương pháp 3: Console VPS** — Đăng nhập trang quản lý nhà cung cấp → mục Console / VNC

---

## XI. THÔNG TIN TÀI KHOẢN VÀ MẬT KHẨU
### ⚠️ LƯU TRỮ CẨN THẬN — KHÔNG CHIA SẺ

### Địa chỉ truy cập
| Mục | Địa chỉ |
|---|---|
| Trang chủ | `https://sudoan5.io.vn` |
| Trang quản trị | `https://sudoan5.io.vn/#/admin/login` |
| API Backend | `https://api.sudoan5.io.vn` |
| Repo Frontend | `https://github.com/QLam130902/trungdoan4.online` |
| Repo Backend | `https://github.com/QLam130902/trungdoan4.server` |

### Database MySQL (trên VPS)
| Thông số | Giá trị |
|---|---|
| Host | `127.0.0.1:3306` |
| Database | `db_homthugopy` |
| Username | `admin` |
| Password | `aATrungdoan4aA@` |

### Tài khoản hệ thống
| Tài khoản | Mật khẩu | Vai trò |
|---|---|---|
| `admin` | `aATrungdoan4aA@` | Quản trị viên |
| `tuannvt` | `Trungdoan4@2026` | Cán bộ xử lý (Trung tá Nguyễn Văn Tuấn) |

### Liên hệ kỹ thuật
| Kênh | Thông tin |
|---|---|
| Hotline | `0989496685` |
| Zalo | `zalo.me/0989496685` |
| Messenger | `messenger.com/t/100006863434895` |

---

## XII. LỊCH SỬ DEPLOY

| Ngày | Phiên bản | Nội dung |
|---|---|---|
| 28/04/2026 | v1.0 | Deploy lần đầu — hệ thống cơ bản |
| 03/05/2026 | v1.1 | Dashboard, phân trang, xuất Excel, bảo mật JWT, tên miền `trungdoan4.io.vn` |
| 05/05/2026 | v1.2 | Nâng cấp lên Sư đoàn 5, Contact Menu, chuyển tên miền sang `sudoan5.io.vn`, HTTPS đầy đủ |

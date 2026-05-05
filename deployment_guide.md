# 📋 TÀI LIỆU HƯỚNG DẪN DEPLOY
## Hệ thống Hòm thư góp ý số — Trung đoàn 4, Sư đoàn 5
*Phiên bản: 1.1 | Cập nhật: 03/05/2026 | Tài liệu MẬT — Lưu trữ cẩn thận*

---

## I. KIẾN TRÚC TỔNG THỂ

```
NGƯỜI DÙNG (Điện thoại / Máy tính)
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│         DNS: trungdoan4.io.vn                               │
│  A record (@)   → 185.199.108.153  (GitHub Pages)          │
│  A record (api) → 160.187.229.25   (VPS)                   │
└──────────────┬──────────────────────────┬───────────────────┘
               │                          │
               ▼                          ▼
  ┌────────────────────┐     ┌────────────────────────────┐
  │  FRONTEND          │     │  BACKEND (VPS Ubuntu 22.04)│
  │  GitHub Pages      │     │  IP: 160.187.229.25        │
  │  trungdoan4.io.vn  │────▶│  api.trungdoan4.io.vn      │
  │  (React + Vite)    │     │  Nginx → cổng 8080         │
  └────────────────────┘     │  Spring Boot 3.4 (Java)    │
                             │  MySQL 8 (db_homthugopy)   │
                             └────────────────────────────┘
```

**Luồng dữ liệu:**
1. Người dùng truy cập `https://trungdoan4.io.vn` → GitHub Pages phục vụ giao diện React
2. Giao diện gọi API tới `https://api.trungdoan4.io.vn/suggestions/...`
3. DNS trỏ `api.` về VPS → Nginx nhận và chuyển tiếp (proxy) về Spring Boot cổng 8080
4. Spring Boot xử lý, truy vấn MySQL, trả kết quả về

---

## II. CẤU TRÚC KHO MÃ NGUỒN (GIT)

### Frontend: `QLam130902/trungdoan4.online`
| Nhánh | Mục đích |
|---|---|
| `local` | Nhánh code chính hàng ngày |
| `main` | **Nhánh deploy** — push vào đây kích hoạt GitHub Actions |
| `feature/*` | Nhánh phát triển tính năng mới |
| `gh-pages` | Nhánh kết quả build (tự động, không sửa thủ công) |

### Backend: `QLam130902/trungdoan4.server`
| Nhánh | Mục đích |
|---|---|
| `local` | Nhánh code chính hàng ngày |
| `main` | Nhánh ổn định |
| `feature/*` | Nhánh phát triển tính năng mới |

---

## III. QUY TRÌNH DEPLOY FRONTEND (Từng bước)

### Điều kiện trước khi deploy
- [ ] Code chạy đúng trên local (`npm run dev`)
- [ ] File `.env.production`: `VITE_API_BASE_URL=https://api.trungdoan4.io.vn`
- [ ] File `public/CNAME` có nội dung: `trungdoan4.io.vn`
- [ ] `vite.config.js` có `base: '/'`

### Các bước thực hiện

**Bước 1: Commit code trên nhánh `local`**
```bash
# Mở Terminal tại: C:\Users\PC\projects\trungdoan4.online
git add .
git commit -m "mô tả thay đổi"
```

**Bước 2: Merge vào `main` và push lên GitHub**
```bash
git checkout main
git merge local
git push origin main
git checkout local    # Quay về local để tiếp tục code
```

**Bước 3: Theo dõi deploy tự động**
- Truy cập: `https://github.com/QLam130902/trungdoan4.online/actions`
- Chờ dấu ✅ xanh (thường 1-3 phút)

**Bước 4: Nếu Actions không tự trigger**
- Vào tab Actions → Chọn workflow → Bấm **"Run workflow"** → Chọn nhánh `main` → Run
- Hoặc kiểm tra file deploy có trên nhánh main chưa:
```bash
git show main:.github/workflows/deploy.yml
```

**Bước 5: Xác nhận thành công**
- Mở `https://trungdoan4.io.vn`, bấm **Ctrl+F5** để xóa cache

---

## IV. QUY TRÌNH DEPLOY BACKEND (Từng bước)

### Điều kiện trước khi deploy
- [ ] Code chạy đúng trên local (`.\mvnw spring-boot:run`)
- [ ] Đã commit code vào nhánh `local`

### Các bước thực hiện

**Bước 1: Commit code**
```bash
# Mở Terminal tại: C:\Users\PC\projects\trungdoan4.server
git add .
git commit -m "mô tả thay đổi"
```

**Bước 2: Build file JAR**
```bash
.\mvnw clean package -DskipTests
# Kết quả: target\todo-0.0.1-SNAPSHOT.jar (~54MB, khoảng 30-60 giây)
```

**Bước 3: Copy file JAR lên VPS**
```bash
# Chạy tại CMD Windows (KHÔNG phải PowerShell, KHÔNG phải trong VPS)
scp C:\Users\PC\projects\trungdoan4.server\target\todo-0.0.1-SNAPSHOT.jar root@160.187.229.25:/root/
```
> Nếu SCP lỗi: Dùng **WinSCP** (winscp.net) — Host `160.187.229.25`, User `root`, kéo thả file JAR vào `/root/`

**Bước 4: Khởi động lại service trên VPS**
```bash
# SSH vào VPS từ CMD Windows
ssh root@160.187.229.25

# Sau khi vào VPS:
sudo systemctl restart todoapp
sudo systemctl status todoapp
# Chờ 30-40 giây → kết quả: Active: active (running)
```

**Bước 5: Kiểm tra hoạt động**
```bash
sudo ss -tlnp | grep 8080
curl -s http://localhost:8080/suggestions/lookup/test  # Trả về JSON là OK
```

---

## V. CẤU HÌNH VPS — CHI TIẾT

### 5.1 Thông tin máy chủ
| Thông số | Giá trị |
|---|---|
| Hệ điều hành | Ubuntu 22.04 LTS |
| IP Public | `160.187.229.25` |
| Hostname | `trungdoan4.online` |
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
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /root/todo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

[Install]
WantedBy=multi-user.target
```

**Lệnh quản lý service:**
```bash
sudo systemctl start todoapp
sudo systemctl stop todoapp
sudo systemctl restart todoapp
sudo systemctl status todoapp
sudo systemctl daemon-reload          # Sau khi sửa file .service
sudo journalctl -u todoapp -f         # Log realtime
sudo journalctl -u todoapp -n 80 --no-pager  # 80 dòng log gần nhất
```

### 5.3 Cấu hình Nginx
File: `/etc/nginx/sites-available/api`

```nginx
server {
    server_name api.trungdoan4.io.vn;
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    listen 443 ssl;
    ssl_certificate /etc/letsencrypt/live/api.trungdoan4.io.vn/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.trungdoan4.io.vn/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;
}
server {
    if ($host = api.trungdoan4.io.vn) { return 301 https://$host$request_uri; }
    listen 80;
    server_name api.trungdoan4.io.vn;
    return 404;
}
```

**Lệnh quản lý Nginx:**
```bash
sudo nginx -t                          # LUÔN kiểm tra trước khi reload
sudo systemctl reload nginx
sudo systemctl restart nginx
sudo systemctl status nginx
sudo ln -s /etc/nginx/sites-available/api /etc/nginx/sites-enabled/api  # Kích hoạt site
sudo rm /etc/nginx/sites-enabled/api   # Vô hiệu hóa site
```

### 5.4 Chứng chỉ SSL
- Domain: `api.trungdoan4.io.vn` | Hết hạn: **01/08/2026** (tự gia hạn)
- Vị trí: `/etc/letsencrypt/live/api.trungdoan4.io.vn/`

```bash
sudo certbot --nginx -d <domain>       # Cài SSL cho domain mới
sudo certbot renew --dry-run           # Kiểm tra tự động gia hạn
sudo certbot delete --cert-name <domain>  # Xóa chứng chỉ cũ
```
> ⚠️ Sau khi xóa chứng chỉ → phải xóa symlink Nginx tương ứng, nếu không Nginx không reload được

---

## VI. CẤU HÌNH DNS

| Loại | Host | Giá trị | Mục đích |
|---|---|---|---|
| **A** | `@` | `185.199.108.153` | Frontend → GitHub Pages |
| **A** | `api` | `160.187.229.25` | Backend API → VPS |
| **CNAME** | `www` | `qlam130902.github.io` | Alias www |

> DNS thay đổi cần 15 phút đến vài tiếng để có hiệu lực toàn cầu.

---

## VII. CÁC LỖI ĐÃ GẶP VÀ CÁCH KHẮC PHỤC

### ❌ Lỗi 1: Màn hình trắng sau khi deploy
**Nguyên nhân:** `vite.config.js` có `base: '/trungdoan4.online/'`  
**Khắc phục:** Sửa thành `base: '/'`, commit và push lại

### ❌ Lỗi 2: GitHub Actions không tự chạy
**Nguyên nhân:** File `.github/workflows/deploy.yml` chưa có trên nhánh `main`  
**Khắc phục:**
```bash
git checkout main && git merge chore/setup-github-pages && git push origin main
```
Hoặc kích hoạt thủ công: Actions → Run workflow → Chọn nhánh `main`

### ❌ Lỗi 3: API trả về 502 Bad Gateway
**Kiểm tra:**
```bash
sudo systemctl status todoapp
sudo nginx -t
curl -s http://localhost:8080/suggestions/lookup/test
sudo journalctl -u todoapp -n 50 --no-pager
```
**Nếu Nginx lỗi SSL cũ:**
```bash
grep -r "domain-cũ" /etc/nginx/
sudo rm /etc/nginx/sites-enabled/domain-cũ
sudo nginx -t && sudo systemctl reload nginx
```

### ❌ Lỗi 4: Spring Boot crash — "Unable to determine Dialect"
**Nguyên nhân:** Thiếu `--spring.profiles.active=prod` trong file service  
**Khắc phục:**
```bash
sudo nano /etc/systemd/system/todoapp.service  # Thêm --spring.profiles.active=prod
sudo systemctl daemon-reload && sudo systemctl restart todoapp
```

### ❌ Lỗi 5: SSH — Permission denied
**Khắc phục:** Dùng Console trực tiếp trên trang quản lý VPS, hoặc dùng WinSCP

### ❌ Lỗi 6: SCP — "No such file or directory"
**Nguyên nhân:** Chạy lệnh SCP từ bên trong VPS  
**Khắc phục:** Mở CMD mới trên Windows (không phải cửa sổ SSH):
```bash
scp C:\Users\PC\projects\trungdoan4.server\target\todo-0.0.1-SNAPSHOT.jar root@160.187.229.25:/root/
```

### ❌ Lỗi 7: `authFetch is not defined`
**Khắc phục:** Thêm vào các file admin:
```javascript
import { useAuth } from '../../contexts/AuthContext';
const { authFetch } = useAuth();
```

### ❌ Lỗi 8: Modal hết phiên dẫn về trang sai
**Nguyên nhân:** Thiếu `/#/` trong đường dẫn (dự án dùng HashRouter)  
**Khắc phục:** Trong `AuthContext.jsx`: `window.location.href = '/#/admin/login';`

---

## VIII. CHECKLIST KIỂM TRA SAU DEPLOY

### Frontend
- [ ] `https://trungdoan4.io.vn` hiện lên bình thường, có ổ khóa xanh
- [ ] Gửi 1 góp ý → Nhận mã tra cứu
- [ ] Tra cứu bằng mã → Hiện đúng nội dung

### Backend & Admin
- [ ] Đăng nhập `https://trungdoan4.io.vn/#/admin/login` thành công
- [ ] Dashboard hiển thị biểu đồ
- [ ] Danh sách góp ý, phân trang hoạt động
- [ ] Phản hồi góp ý → Lưu thành công
- [ ] Xuất Excel → File tải về đúng định dạng

### Trên VPS
```bash
sudo systemctl status todoapp nginx
sudo ss -tlnp | grep -E "80|443|8080"
```

---

## IX. CÁCH TRUY CẬP VPS

**Phương pháp 1: SSH từ CMD Windows**
```bash
ssh root@160.187.229.25
```

**Phương pháp 2: WinSCP** — Host `160.187.229.25`, User `root`, Protocol SFTP

**Phương pháp 3: Console VPS** — Đăng nhập trang quản lý nhà cung cấp → mục Console / VNC

---

## X. THÔNG TIN TÀI KHOẢN VÀ MẬT KHẨU
### ⚠️ LƯU TRỮ CẨN THẬN — KHÔNG CHIA SẺ

### Hạ tầng
| Dịch vụ | Thông tin |
|---|---|
| VPS SSH | `root@160.187.229.25` |
| GitHub | `QLam130902` |

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

### Địa chỉ truy cập
| Mục | Địa chỉ |
|---|---|
| Trang chủ | `https://trungdoan4.io.vn` |
| Trang quản trị | `https://trungdoan4.io.vn/#/admin/login` |
| API Backend | `https://api.trungdoan4.io.vn` |
| Repo Frontend | `https://github.com/QLam130902/trungdoan4.online` |
| Repo Backend | `https://github.com/QLam130902/trungdoan4.server` |

---

## XI. LỊCH SỬ DEPLOY

| Ngày | Phiên bản | Nội dung |
|---|---|---|
| 28/04/2026 | v1.0 | Deploy lần đầu — hệ thống cơ bản |
| 03/05/2026 | v1.1 | Dashboard, phân trang, xuất Excel, bảo mật JWT, tên miền `trungdoan4.io.vn` |

# ==============================================================================
# TÀI LIỆU KỸ THUẬT & HƯỚNG DẪN SỬ DỤNG (WIKI / DOCS)
# PLUGIN: NovusLixi (Hệ Thống Lì Xì Server Đa Tiền Tệ)
# ==============================================================================
**Phiên bản:** `1.0.0`  
**Hỗ trợ nền tảng:** Paper / Purpur 1.21+ (Java 21)  
**Phụ thuộc (Dependencies):** ExcellentEconomy (hoặc Vault), PlaceholderAPI  
**Tác giả:** NovusMC Development Team  

---

## 1. Giới thiệu tổng quan
**NovusLixi** là plugin lì xì server hiệu năng cao được phát triển nhằm thay thế hoàn toàn hệ thống Skript lì xì truyền thống (>1,700 dòng lệnh reflection). Plugin giải quyết triệt để vấn đề giọt tick (tick-lag) khi nhiều người chơi cùng phát và nhận lì xì cùng một thời điểm.

### Các ưu điểm vượt trội:
- **Xử lý Bất đồng bộ (Async Architecture):** Toàn bộ tiến trình đọc/ghi file dữ liệu bao lì xì, kiểm tra thời hạn và quét hoàn tiền đều chạy trên các luồng worker riêng biệt, không block Main Server Thread.
- **An toàn Đa luồng (Thread-Safe):** Sử dụng cơ chế khóa phân đoạn và `synchronized claim()` đảm bảo 1 bao lì xì không bao giờ bị nhận quá số suất, không thể bị dupe tiền dù nhiều người chơi click đồng thời trong cùng 1 tick.
- **Hỗ trợ Kinh tế 3 Cấp:** Tích hợp trực tiếp với hệ thống tiền tệ mới của server:
  - `kimte` (Kim Tệ - Tiền tệ phổ thông)
  - `linhngoc` (Linh Ngọc - Tiền tệ cao cấp / donate)
  - `hongngoc` (Hồng Ngọc - Tiền tệ hiếm)
- **Cơ chế Hoàn Quỹ Thông Minh:** Nếu bao lì xì hết hạn mà chưa có người nhận hết số suất, phần tiền còn dư sẽ tự động được hoàn trả về tài khoản của người tạo khi hết hạn.
- **Thông Báo Trực Quan:** Hiển thị BossBar động toàn server khi có người chơi phát lì xì giá trị lớn.

---

## 2. Danh sách Lệnh & Quyền hạn (Commands & Permissions)

| Lệnh | Bí danh (Alias) | Quyền hạn (Permission) | Mô tả |
| :--- | :--- | :--- | :--- |
| `/lixi <loại_tiền> <tổng_quỹ> <số_suất> [thời_gian]` | `/phatlixi` | `novusmc.lixi.use` | Tạo một bao lì xì mới để phát cho toàn server. |
| `/laylixi [mã_id]` | `/nhanlixi` | `novusmc.lixi.use` | Xem danh sách các bao lì xì đang hoạt động hoặc bấm nhận lì xì theo mã ID. |
| `/lixiadmin scan` | Không | `novusmc.lixi.admin` | Quét thủ công toàn bộ lì xì hết hạn và hoàn tiền phần dư ngay lập tức. |
| `/lixiadmin reload` | Không | `novusmc.lixi.admin` | Tải lại tệp cấu hình `config.yml` mà không cần khởi động lại server. |
| `/lixiadmin clear` | Không | `novusmc.lixi.admin` | Đóng khẩn cấp toàn bộ lì xì đang phát và hoàn tiền cho các chủ sở hữu. |

---

## 3. Quy tắc tính toán & Hoàn quỹ (Business Logic)
1. **Giá trị mỗi suất:**
   $$\text{Giá trị mỗi suất} = \lfloor \frac{\text{Tổng quỹ}}{\text{Số suất}} \rfloor$$
2. **Quy tắc nhận:**
   - Người tạo bao lì xì **không thể** tự nhận lì xì của chính mình.
   - Mỗi người chơi online chỉ được nhận **tối đa 1 suất** trên mỗi bao lì xì.
3. **Phí tạo & duy trì:**
   - Phí tạo cơ bản (mặc định: `50 Kim Tệ`).
   - Phí cộng thêm theo thời gian tồn tại: `12h` (miễn phí), `24h` (+10 Kim Tệ), `48h` (+19 Kim Tệ).
   - Phí tạo lì xì không được hoàn lại khi hết hạn.

---

## 4. Hướng dẫn Cấu hình chi tiết (`config.yml`)

```yaml
# Tiền tố hiển thị trong tin nhắn chat
prefix: "&8[&6Lì Xì&8] &7"

# Chi phí tạo bao lì xì (Kim Tệ)
create-fee: 50

# Phí phụ thu duy trì theo thời gian
duration-fees:
  12h: 0
  24h: 10
  48h: 19

# Cấu hình thanh BossBar hiển thị thông báo phát lì xì
bossbar:
  enabled: true
  duration-seconds: 12
  color: RED
  style: SOLID
  title: "&6&l[LÌ XÌ SERVER] &f{player} &eđang phát &c{amount} {currency}&e! Nhập &b/laylixi &eđể nhận ngay!"

# Chu kỳ quét tự động (giây) để hoàn tiền các bao lì xì hết hạn
expiry-scan-seconds: 30

# Định nghĩa các loại tiền tệ kết nối với ExcellentEconomy
currencies:
  kimte:
    display-name: "&6Kim Tệ"
    symbol: "KT"
    command-add: "excellenteconomy give {player} kimte {amount}"
    command-take: "excellenteconomy take {player} kimte {amount}"
  linhngoc:
    display-name: "&bLinh Ngọc"
    symbol: "LN"
    command-add: "excellenteconomy give {player} linhngoc {amount}"
    command-take: "excellenteconomy take {player} linhngoc {amount}"
  hongngoc:
    display-name: "&cHồng Ngọc"
    symbol: "HN"
    command-add: "excellenteconomy give {player} hongngoc {amount}"
    command-take: "excellenteconomy take {player} hongngoc {amount}"
```

---

## 5. Cấu trúc lưu trữ dữ liệu (Data Storage)
- **Lì xì đang phát:** Lưu trữ tại thư mục `plugins/NovusLixi/data/active/{id}.yml`.
- **Lịch sử lì xì đã hoàn tất:** Lưu trữ tại thư mục `plugins/NovusLixi/data/history/{id}.yml`.
- File YAML lưu chi tiết: Mã ID, UUID người tạo, Tên người tạo, Loại tiền, Tổng quỹ, Số suất ban đầu, Số suất còn lại, và Danh sách UUID của tất cả người chơi đã nhận.

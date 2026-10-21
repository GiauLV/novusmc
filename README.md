# ⚔️ NovusMC - Minecraft Survival RPG ⚔️
Hệ thống tài liệu kỹ thuật, mã nguồn plugin tùy chỉnh và báo cáo tối ưu hóa cho máy chủ NovusMC (Paper 1.21+).

---

## 📚 Danh mục Tài liệu Kỹ thuật (Wiki & Docs)

- 📜 **[Wiki Máy Chủ NovusMC & Hệ Thống Kinh Tế 3 Cấp (Kim Tệ, Hồng Ngọc, Linh Ngọc)](docs/WIKI_NOVUSMC_SERVER.md)** ⭐
  - Tổng quan lối chơi Survival MMORPG (MMOCore, MMOItems, MythicMobs, Tháp, Ngư nghiệp & Nông trại).
  - Chi tiết 3 loại tiền tệ: Nguồn kiếm (Inflow), Mục đích sử dụng (Sinks - Dùng để làm gì).
  - Sơ đồ tuần hoàn kinh tế (Mermaid Circular Economy Flow) & Bảng tra cứu lệnh giao dịch (`/money`, `/bal`, `/pay`, `/ee`, `/lixi`).
- 🧧 **[Wiki Plugin NovusLixi (Hệ thống Lì Xì Đa Tiền Tệ)](docs/WIKI_NOVUS_LIXI.md)**
  - Cơ chế tính toán quỹ, phân chia số suất và hoàn tiền tự động khi hết hạn.
  - Bảng tra cứu toàn bộ Lệnh & Quyền hạn (`/lixi`, `/laylixi`, `/lixiadmin`).
  - Hướng dẫn cấu hình đa tiền tệ (`kimte`, `linhngoc`, `hongngoc`) & BossBar.
- 👾 **[Wiki Module NovusBossTop (Async Discord Webhook)](docs/WIKI_NOVUS_BOSS_DAMAGE_DISCORD.md)**
  - Cơ chế theo dõi sát thương trong bộ nhớ RAM (`ConcurrentHashMap`) không gây lag máy chủ.
  - Luồng xử lý đa luồng (`CompletableFuture.runAsync()`) gửi báo cáo chiến tích sang Discord Webhook.
  - Định dạng thẻ Embed Top 1 - 10 và danh sách Boss được theo dõi.
- 📊 **[Báo Cáo Tổng Kết Khắc Phục Lỗi & Tối Ưu Hóa](docs/BAO_CAO_HOAN_TAT_TOI_UU.md)**
  - Khắc phục triệt để 5 lỗi cấu hình (Multiverse-Core, FancyNpcs, MythicMobs, ExcellentEnchants, ModelEngine).
  - Tối ưu hóa gom nhóm Skript từ 49 file xuống 33 file.
  - Giải phóng ~2.44 GB dung lượng ổ cứng từ các file sao lưu cũ.
- 💰 **[Báo Cáo Rà Soát Hệ Thống Kinh Tế 3 Cấp](docs/BAO_CAO_RA_SOAT_KINH_TE.md)**
  - Chuyển đổi toàn diện từ đồng vàng/đá quý sang Kim Tệ (`kimte`) và Linh Ngọc (`linhngoc`).

---

## 📦 Danh mục Plugin Tùy Chỉnh (Custom Plugins)

| Plugin | Mô tả | Phiên bản | Đường dẫn mã nguồn |
| :--- | :--- | :---: | :--- |
| **NovusLixi** | Plugin lì xì server hiệu năng cao bất đồng bộ (thay thế script cũ 1,750 dòng). | `1.0.0` | [`plugins/NovusLixi/`](plugins/NovusLixi/) |
| **NovusBossTop** | Plugin xếp hạng sát thương Boss MythicMobs gửi Discord qua Webhook. | `1.0.0` | [`plugins/NovusBossTop/`](plugins/NovusBossTop/) |

---

*Tài liệu được cập nhật tự động bởi Antigravity AI Assistant.*
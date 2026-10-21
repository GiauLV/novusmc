# BÁO CÁO RÀ SOÁT & KIỂM TRA HỆ THỐNG KINH TẾ (ECONOMY AUDIT)
**Server:** `https://panel.ultraservers.com/server/0d795e51` (`ULTRASERVERS_17122`)  
**Ngày kiểm tra:** 20/10/2026  
**Chuyển đổi:** 
- `dongvang` (dv) ➔ **`kimte`** (kt / Kim Tệ - Cấp 1: Phổ thông / Farmable)
- `daquy` (dq) ➔ **`linhngoc`** (ln / Linh Ngọc - Cấp 3: Donate / Discord độc quyền)
- **`hongngoc`** (hn / Hồng Ngọc - Cấp 2: Mới tạo, chuyên dùng cho cày cuốc trung & cao cấp)

---

## 1. TỔNG QUAN TÌNH TRẠNG HIỆN TẠI

1. **Plugin kinh tế cốt lõi (`ExcellentEconomy`)**:
   - Đã có đủ 3 file tiền tệ chính:
     - `currencies/kimte.yml` (`Column_Name: kimte`, `Decimal: true`, `Start_Value: 1000`)
     - `currencies/hongngoc.yml` (`Column_Name: hongngoc`, `Decimal: false`, `Start_Value: 0`)
     - `currencies/linhngoc.yml` (`Column_Name: linhngoc`, `Decimal: false`, `Start_Value: 0`)
   - `config.yml`: Đã cấu hình `EconomyCurrency: kimte` (Vault tự động tích hợp với Kim Tệ).
   - Trong `kimte.yml` và `linhngoc.yml`, các lệnh cũ `dv, dongvang` và `dq, daquy` vẫn được giữ làm alias phụ để tương thích tạm thời.
   - **Tuy nhiên**, các plugin GUI, Shop, Skript và các hook bên ngoài **vẫn đang trỏ vào ID cũ (`dongvang`, `daquy`, `dv`, `dq`)**, nếu không đồng bộ sẽ gây lỗi lệnh console hoặc hiển thị sai placeholder khi gỡ alias cũ.

---

## 2. DANH SÁCH CHI TIẾT CÁC NƠI CẦN ĐIỀU CHỈNH

### A. DeluxeMenus (15 Files cấu hình Menu)
Hệ thống DeluxeMenus đang thực thi trực tiếp các lệnh console `[console] dv ...` hoặc `[console] dq ...` và `[console] dongvang ...`.

| File cấu hình (`plugins/DeluxeMenus/gui_menus/...`) | Dòng phát hiện | Nội dung hiện tại | Đề xuất sửa đổi |
| :--- | :--- | :--- | :--- |
| **`shopall/shopclaim.yml`** | L88, L117 | `dv take`, `dv give` | Đổi thành `kimte take` / `kimte give` |
| **`shopall/shopgacha.yml`** | L111, L148 | `dv take` | Đổi thành `kimte take` |
| **`shopall/shopenchant.yml`** | L106, L141, L176 | `dq take` | Đổi thành `linhngoc take` (hoặc chuyển sang `hongngoc`) |
| **`shopall/shopgachadaquy.yml`** | L79, L115 | `dq take` | Đổi tên menu thành Gacha Linh Ngọc và sửa `linhngoc take` |
| **`shopall/shopdanhhieu.yml`** | 19 vị trí (L146..L1308) | `dongvang take` | Đổi thành `kimte take` |
| **`shopall/shopdanhhieu2.yml`** | 19 vị trí (L142..L1304) | `dongvang take` | Đổi thành `kimte take` |
| **`shopall/shoprank.yml`** | L60, L62, L138, L141, L217, L220 | `dq take`, `dv give` | Đổi thành `linhngoc take`, `kimte give` |
| **`shopall/shopsellclaim.yml`** | L91, L119, L147, L175, L203 | `dv give` | Đổi thành `kimte give` |
| **`shopall/shoptienich.yml`** | L92, L120, L148, L176 | `dv take` | Đổi thành `kimte take` |
| **`shopall/shopgemspawner.yml`** | 22 vị trí (L74..L558) | `dq take` | Đổi thành `linhngoc take` |
| **`shopall/shopgoiqua.yml`** | L90, L94, L694, L737, L741, L794, L798, L848 | `dq take`, `dv give` | Đổi thành `linhngoc take`, `kimte give` |
| **`shopall/shopspawner.yml`** | L88, L122, L157, L191, L224, L258, L292, L326, L362, L395, L430 | `dv take` | Đổi thành `kimte take` |
| **`shopall/shopupgrade.yml`** | L85, L89, L457, L506, L559, L563, L616, L618, L663, L706, L710, L763, L767, L817 | `dq take`, `dv give`, `dv take` | Đổi `dq take` ➔ `linhngoc take`, `dv` ➔ `kimte` |
| **`shop_cauca_dacbiet.yml`** | 11 vị trí (L32..L329) | `dq take` | Đổi thành `linhngoc take` (hoặc `hongngoc`) |
| **`shop_cauca_thuong.yml`** | 21 vị trí (L32..L585) | `dv take` | Đổi thành `kimte take` |

---

### B. EconomyShopGUI-Premium
- **Đã kiểm tra**: File `config.yml` dùng `economy-provider: VAULT`, đã tự động ăn theo `kimte`.
- **Cần sửa trực tiếp 2 file sau**:
  1. `plugins/EconomyShopGUI-Premium/shops/SpecialShop.yml`:
     - **Dòng 52**: `- COINSENGINE:daquy::15` ➔ Sửa thành `- COINSENGINE:linhngoc::15`
     - **Dòng 54**: `economy: COINSENGINE:daquy` ➔ Sửa thành `economy: COINSENGINE:linhngoc`
  2. `plugins/EconomyShopGUI-Premium/sections/cuahangthoigian.yml`:
     - **Dòng 13**: `economy: "COINS_ENGINE:dongvang"` ➔ Sửa thành `economy: "COINS_ENGINE:kimte"`

---

### C. DailyRewardsPlus (Phần thưởng đăng nhập ngày)
- **File**: `plugins/DailyRewardsPlus/Rewards.yml`
- **Tình trạng**: Có **61 vị trí** đang dùng lệnh:
  `- "dv give <playerName> <amount>"`
- **Đề xuất**: Sửa toàn bộ `- "dv give <playerName> ..."` thành `- "kimte give <playerName> ..."` (hoặc `- "ee give <playerName> kimte ..."`).

---

### D. Hệ Thống Skript (12 Scripts cốt lõi)
Hệ thống script tự viết của server chứa nhiều lệnh nội bộ liên quan đến giao dịch và lì xì:

1. **`plugins/Skript/scripts/NovusMail/02_dialog.sk` & `04_commands.sk`**:
   - `02_dialog.sk` (L99, L100, L115, L119): Các nút bấm chọn gửi thư đang là `"dongvang"` (&6Đồng Vàng) và `"daquy"` (&bĐá Quý).
     - *Cần sửa*: Đổi thành `"kimte"` (&6Kim Tệ), `"linhngoc"` (&bLinh Ngọc) và **bổ sung thêm nút thứ 3 cho `"hongngoc"` (&dHồng Ngọc)**.
   - `04_commands.sk` (L23, L76, L98, L123): Lệnh `/nthu quick <player> dv/dq` ➔ Đổi thành hỗ trợ `kt`, `ln`, `hn` (kimte, linhngoc, hongngoc).
2. **`plugins/Skript/scripts/server/lixi/lixi_FIXED_V11_CUSTOMNAMEPLATES_NOTIFY.sk`**:
   - L137, L139: `if {_currency} is "dongvang": ... else if {_currency} is "daquy":` ➔ Đổi thành `"kimte"`, `"linhngoc"` (và thêm nhánh `"hongngoc"` nếu muốn cho lì xì cả Hồng Ngọc).
   - L156, L162: `dv take`, `dv give` ➔ `kimte take`, `kimte give`
   - L168, L174: `dq take`, `dq give` ➔ `linhngoc take`, `linhngoc give`
   - L858, L870, L954, L1463: `lixiBalance({_p}, "daquy")` ➔ `"linhngoc"`, `fees.total-daquy` ➔ `fees.total-linhngoc`.
3. **`plugins/Skript/scripts/chilltime2.sk`**:
   - L213: `execute console command "dv give %{_name}% %{_dv}% -s"` ➔ `kimte give`
   - L217: `execute console command "dq give %{_name}% %{_gems}% -s"` ➔ `linhngoc give`
4. **`plugins/Skript/scripts/goi_da_quy_cao_cap_REALTIME_LOG_V4_1.sk`**:
   - L41: `currency-command: daquy` ➔ Đổi thành `currency-command: linhngoc`
5. **`plugins/Skript/scripts/hethongmuaban.sk`**:
   - L154, L197: `hmbBalance(player, "dongvang")` ➔ `kimte`
   - L205, L275, L279, L285, L414..L447: `dv take`, `dv give` ➔ `kimte take`, `kimte give`
   - L263, L271, L325, L333, L400, L408: `hmbBalance(player, "daquy")`, `dq take` ➔ `linhngoc`
6. **`plugins/Skript/scripts/linhluc.sk`**:
   - L48: `dongvang-placeholder: excellenteconomy_balance_raw_dongvang` ➔ Sửa thành `excellenteconomy_balance_raw_kimte`
7. **`plugins/Skript/scripts/novus_danhhieu_fixed_v5.sk`**:
   - L414: `execute console command "dongvang take %name of {_p}% %{_price}% -sf -s"` ➔ Sửa thành `kimte take`
8. **`plugins/Skript/scripts/novus_necros_spin (1).sk`**:
   - L12: `currency-id: daquy` ➔ Sửa thành `currency-id: linhngoc`
9. **`plugins/Skript/scripts/novus_packages.sk`**:
   - L53, L115, L150: `dv take`, `dv give` ➔ `kimte take`, `kimte give`
   - L80, L110, L147: `dq take` ➔ `linhngoc take`

---

### E. Các Plugin Custom Novus
1. **`plugins/NovusCoreSuperEssential/Chats/custom_keywords.yml`**:
   - L19: `[dongvang]` ➔ Sửa thành `[kimte]`
   - L34: `[daquy]` ➔ Sửa thành `[linhngoc]` (và bổ sung thêm từ khóa `[hongngoc]`)
   - L38: `economy: daquy` ➔ Sửa thành `economy: linhngoc`
2. **`plugins/NovusLeoThap/config.yml`**:
   - L8: `currency: daquy` ➔ Đề xuất: Đổi thành `currency: linhngoc` **HOẶC** chuyển sang dùng **`currency: hongngoc`** (bởi vì Leo Tháp là tính năng cày cuốc thử thách cao, rất phù hợp để làm nguồn thưởng chính cho Hồng Ngọc!).
3. **`plugins/NovusMail/config.yml`**:
   - L18..L23:
     ```yaml
     kimte:
       aliases: [dv, dongvang, kt, kimte]
       give-command: "kimte give %player% %amount%"
     ```
   - L24..L28:
     ```yaml
     linhngoc:
       aliases: [dq, daquy, ln, linhngoc]
       give-command: "linhngoc give %player% %amount%"
     ```
   - Bổ sung thêm nhánh cho `hongngoc`:
     ```yaml
     hongngoc:
       aliases: [hn, hongngoc]
       give-command: "hongngoc give %player% %amount%"
     ```

---

## 3. CÁC PLUGIN AN TOÀN / ĐÃ ĐỒNG BỘ THEO VAULT
Các plugin sau đã được quét và xác nhận an toàn vì sử dụng trực tiếp chuẩn Vault API (tự động điều phối vào `kimte`):
- **`PlayerMarket`**: `economy.type: Vault`
- **`ClansPlus`**: `currency-requirement.type: VAULT` (phí tạo clan 1000 Kim Tệ)
- **`Quests`**: Lệnh thưởng sử dụng `- eco give {player} <amount>` (alias mặc định của Vault Kim Tệ)
- **`TAB`**: Không cấu hình cứng placeholder tiền tệ, an toàn.

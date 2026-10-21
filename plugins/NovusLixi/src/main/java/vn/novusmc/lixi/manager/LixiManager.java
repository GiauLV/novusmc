package vn.novusmc.lixi.manager;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import vn.novusmc.lixi.NovusLixi;
import vn.novusmc.lixi.model.LixiPackage;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LixiManager {

    private final NovusLixi plugin;
    private final Map<String, LixiPackage> activePackages = new ConcurrentHashMap<>();
    private final File activeFolder;
    private final File historyFolder;

    public LixiManager(NovusLixi plugin) {
        this.plugin = plugin;
        this.activeFolder = new File(plugin.getDataFolder(), "data/active");
        this.historyFolder = new File(plugin.getDataFolder(), "data/history");

        if (!activeFolder.exists()) activeFolder.mkdirs();
        if (!historyFolder.exists()) historyFolder.mkdirs();

        startExpiryTask();
    }

    public void loadActivePackages() {
        File[] files = activeFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File f : files) {
            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(f);
                String id = yaml.getString("id");
                UUID owner = UUID.fromString(yaml.getString("owner-uuid"));
                String ownerName = yaml.getString("owner-name");
                String currency = yaml.getString("currency");
                double fund = yaml.getDouble("total-fund");
                int slots = yaml.getInt("total-slots");
                long expire = yaml.getLong("expire-time");

                LixiPackage pkg = new LixiPackage(id, owner, ownerName, currency, fund, slots, expire);
                for (String cu : yaml.getStringList("claimed-uuids")) {
                    pkg.getClaimedPlayers().add(UUID.fromString(cu));
                }
                activePackages.put(id, pkg);
            } catch (Exception e) {
                plugin.getLogger().warning("Khong the load li xi: " + f.getName());
            }
        }
    }

    public void createPackage(Player creator, String currency, double totalFund, int totalSlots, long durationMillis) {
        String id = "LX_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
        long expireTime = System.currentTimeMillis() + durationMillis;

        LixiPackage pkg = new LixiPackage(id, creator.getUniqueId(), creator.getName(), currency, totalFund, totalSlots, expireTime);
        activePackages.put(id, pkg);
        savePackage(pkg);

        // Broadcast BossBar
        showBroadcastBossBar(creator.getName(), (long)totalFund, currency);
    }

    public boolean claimPackage(Player player, String id) {
        LixiPackage pkg = activePackages.get(id);
        if (pkg == null || pkg.isExpired()) {
            player.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cBao lì xì này không tồn tại hoặc đã hết hạn!"));
            return false;
        }

        if (pkg.getOwnerUuid().equals(player.getUniqueId())) {
            player.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cBạn không thể tự nhận bao lì xì của chính mình!"));
            return false;
        }

        if (pkg.getClaimedPlayers().contains(player.getUniqueId())) {
            player.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cBạn đã nhận bao lì xì này rồi!"));
            return false;
        }

        if (pkg.claim(player.getUniqueId())) {
            double amount = pkg.getSlotValue();
            giveMoney(player.getName(), pkg.getCurrency(), amount);
            player.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&aBạn đã nhận được &e" + (long)amount + " " + pkg.getCurrency() + "&a từ &f" + pkg.getOwnerName() + "&a!"));
            savePackage(pkg);

            if (pkg.getRemainingSlots() <= 0) {
                archivePackage(pkg, "FULL_CLAIMED");
            }
            return true;
        } else {
            player.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cBao lì xì này đã hết lượt nhận!"));
            return false;
        }
    }

    private void showBroadcastBossBar(String owner, long amount, String currency) {
        if (!plugin.getConfig().getBoolean("bossbar.enabled", true)) return;
        String rawTitle = plugin.getConfig().getString("bossbar.title", "&6[LÌ XÌ SERVER] &f{player} &eđang phát &c{amount} {currency}&e!")
                .replace("{player}", owner)
                .replace("{amount}", String.valueOf(amount))
                .replace("{currency}", currency);

        BossBar bar = Bukkit.createBossBar(NovusLixi.color(rawTitle), BarColor.RED, BarStyle.SOLID);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bar.addPlayer(p);
        }

        int duration = plugin.getConfig().getInt("bossbar.duration-seconds", 12);
        Bukkit.getScheduler().runTaskLater(plugin, bar::removeAll, duration * 20L);
    }

    private void startExpiryTask() {
        int interval = plugin.getConfig().getInt("expiry-scan-seconds", 30) * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (LixiPackage pkg : new ArrayList<>(activePackages.values())) {
                if (pkg.isExpired() || pkg.getRemainingSlots() <= 0) {
                    archivePackage(pkg, pkg.isExpired() ? "EXPIRED" : "FULL_CLAIMED");
                }
            }
        }, interval, interval);
    }

    public synchronized void archivePackage(LixiPackage pkg, String reason) {
        activePackages.remove(pkg.getId());
        File activeFile = new File(activeFolder, pkg.getId() + ".yml");
        if (activeFile.exists()) activeFile.delete();

        // Hoàn lại tiền dư thừa nếu còn slot chưa nhận
        if (pkg.getRemainingSlots() > 0) {
            double refund = pkg.getRemainingSlots() * pkg.getSlotValue();
            giveMoney(pkg.getOwnerName(), pkg.getCurrency(), refund);
        }

        File histFile = new File(historyFolder, pkg.getId() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("id", pkg.getId());
        yaml.set("owner-uuid", pkg.getOwnerUuid().toString());
        yaml.set("owner-name", pkg.getOwnerName());
        yaml.set("currency", pkg.getCurrency());
        yaml.set("total-fund", pkg.getTotalFund());
        yaml.set("total-slots", pkg.getTotalSlots());
        yaml.set("remaining-slots", pkg.getRemainingSlots());
        yaml.set("reason", reason);
        try {
            yaml.save(histFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePackage(LixiPackage pkg) {
        File f = new File(activeFolder, pkg.getId() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("id", pkg.getId());
        yaml.set("owner-uuid", pkg.getOwnerUuid().toString());
        yaml.set("owner-name", pkg.getOwnerName());
        yaml.set("currency", pkg.getCurrency());
        yaml.set("total-fund", pkg.getTotalFund());
        yaml.set("total-slots", pkg.getTotalSlots());
        yaml.set("expire-time", pkg.getExpireTimeMillis());

        List<String> list = new ArrayList<>();
        for (UUID u : pkg.getClaimedPlayers()) list.add(u.toString());
        yaml.set("claimed-uuids", list);

        try {
            yaml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        for (LixiPackage p : activePackages.values()) {
            savePackage(p);
        }
    }

    public Map<String, LixiPackage> getActivePackages() {
        return activePackages;
    }

    public void giveMoney(String player, String currency, double amount) {
        String cmd = plugin.getConfig().getString("currencies." + currency + ".command-add");
        if (cmd != null) {
            cmd = cmd.replace("{player}", player).replace("{amount}", String.valueOf((long)amount));
            final String fCmd = cmd;
            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fCmd));
        }
    }
}

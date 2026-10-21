package vn.novusmc.bosstop.listener;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BossDamageListener implements Listener {

    private final JavaPlugin plugin;
    private volatile String webhookUrl;
    private final HttpClient httpClient;
    private final DecimalFormat df = new DecimalFormat("#,###");

    private final Map<UUID, Map<String, Double>> bossDamageTracker = new ConcurrentHashMap<>();

    public BossDamageListener(JavaPlugin plugin, String webhookUrl) {
        this.plugin = plugin;
        this.webhookUrl = webhookUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void setWebhookUrl(String url) {
        this.webhookUrl = url;
    }

    public void clearTrackers() {
        bossDamageTracker.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!MythicBukkit.inst().getAPIHelper().isMythicMob(event.getEntity())) return;
        ActiveMob mob = MythicBukkit.inst().getAPIHelper().getMythicMobInstance(event.getEntity());
        if (mob == null) return;

        String mobType = mob.getType().getInternalName();
        if (!isMonitoredBoss(mobType)) return;

        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                damager = (Player) proj.getShooter();
            }
        }

        if (damager == null) return;

        UUID bossUuid = event.getEntity().getUniqueId();
        double dmg = event.getFinalDamage();

        bossDamageTracker.computeIfAbsent(bossUuid, k -> new ConcurrentHashMap<>())
                .merge(damager.getName(), dmg, Double::sum);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMythicDeath(MythicMobDeathEvent event) {
        ActiveMob mob = event.getMob();
        UUID bossUuid = mob.getUniqueId();

        Map<String, Double> damageMap = bossDamageTracker.remove(bossUuid);
        if (damageMap == null || damageMap.isEmpty()) return;

        String bossDisplayName = mob.getType().getDisplayName().get();

        CompletableFuture.runAsync(() -> {
            try {
                List<Map.Entry<String, Double>> sortedList = damageMap.entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .collect(Collectors.toList());

                double totalDamageDealt = sortedList.stream().mapToDouble(Map.Entry::getValue).sum();

                StringBuilder description = new StringBuilder();
                description.append("🏆 **BẢNG XẾP HẠNG SÁT THƯƠNG DIỆT BOSS** 🏆\\n");
                description.append("👾 **Boss:** ").append(bossDisplayName).append("\\n");
                description.append("💥 **Tổng sát thương:** ").append(df.format(totalDamageDealt)).append("\\n\\n");

                int rank = 1;
                int topLimit = plugin.getConfig().getInt("top-limit", 10);
                for (Map.Entry<String, Double> entry : sortedList) {
                    if (rank > topLimit) break;
                    String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : "▫️";
                    double percent = (entry.getValue() / Math.max(1, totalDamageDealt)) * 100.0;
                    description.append(String.format("%s **#%d %s**: %s ST (%.1f%%)\\n",
                            medal, rank, entry.getKey(), df.format(entry.getValue()), percent));
                    rank++;
                }

                sendDiscordWebhook(description.toString());
            } catch (Exception e) {
                plugin.getLogger().warning("[NovusBossTop] Loi khi xu ly async top damage: " + e.getMessage());
            }
        });
    }

    private void sendDiscordWebhook(String text) {
        if (webhookUrl == null || webhookUrl.isBlank()) return;

        int color = plugin.getConfig().getInt("embed-color", 15158332);
        String jsonPayload = String.format("{\"embeds\": [{\"title\": \"⚔️ CHIẾN BÁO HẠ GỤC BOSS ⚔️\", \"description\": \"%s\", \"color\": %d, \"timestamp\": \"%s\"}]}",
                text, color, Instant.now().toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 400) {
                        plugin.getLogger().warning("[NovusBossTop] Discord Webhook response code: " + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    plugin.getLogger().warning("[NovusBossTop] Khong the gui webhook: " + ex.getMessage());
                    return null;
                });
    }

    private boolean isMonitoredBoss(String internalName) {
        List<String> list = plugin.getConfig().getStringList("monitored-bosses");
        for (String b : list) {
            if (b.equalsIgnoreCase(internalName)) return true;
        }
        return internalName.toLowerCase().contains("boss") ||
               internalName.toLowerCase().contains("warlord") ||
               internalName.toLowerCase().contains("vael");
    }
}

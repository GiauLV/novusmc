package vn.novusmc.bosstop;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import vn.novusmc.bosstop.command.BossTopCommand;
import vn.novusmc.bosstop.listener.BossDamageListener;

public class NovusBossTop extends JavaPlugin {

    private static NovusBossTop instance;
    private BossDamageListener damageListener;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        String webhookUrl = getConfig().getString("webhook-url", "");
        this.damageListener = new BossDamageListener(this, webhookUrl);
        getServer().getPluginManager().registerEvents(damageListener, this);

        if (getCommand("bosstop") != null) {
            getCommand("bosstop").setExecutor(new BossTopCommand(this));
        }

        getLogger().info("NovusBossTop da khoi dong thanh cong (Async MythicMobs Webhook Engine)!");
    }

    @Override
    public void onDisable() {
        if (damageListener != null) {
            damageListener.clearTrackers();
        }
        getLogger().info("NovusBossTop da tat.");
    }

    public static NovusBossTop getInstance() {
        return instance;
    }

    public BossDamageListener getDamageListener() {
        return damageListener;
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}

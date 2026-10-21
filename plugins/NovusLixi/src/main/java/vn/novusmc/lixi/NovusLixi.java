package vn.novusmc.lixi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import vn.novusmc.lixi.command.LayLixiCommand;
import vn.novusmc.lixi.command.LixiAdminCommand;
import vn.novusmc.lixi.command.LixiCommand;
import vn.novusmc.lixi.manager.LixiManager;

public class NovusLixi extends JavaPlugin {

    private static NovusLixi instance;
    private LixiManager lixiManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.lixiManager = new LixiManager(this);
        this.lixiManager.loadActivePackages();

        if (getCommand("lixi") != null) {
            getCommand("lixi").setExecutor(new LixiCommand(this));
        }
        if (getCommand("laylixi") != null) {
            getCommand("laylixi").setExecutor(new LayLixiCommand(this));
        }
        if (getCommand("lixiadmin") != null) {
            getCommand("lixiadmin").setExecutor(new LixiAdminCommand(this));
        }

        getLogger().info("NovusLixi da khoi dong thanh cong (High-Performance Engine)!");
    }

    @Override
    public void onDisable() {
        if (lixiManager != null) {
            lixiManager.saveAll();
        }
        getLogger().info("NovusLixi da tat.");
    }

    public static NovusLixi getInstance() {
        return instance;
    }

    public LixiManager getLixiManager() {
        return lixiManager;
    }

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}

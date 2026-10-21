package vn.novusmc.lixi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vn.novusmc.lixi.NovusLixi;
import vn.novusmc.lixi.model.LixiPackage;

public class LixiAdminCommand implements CommandExecutor {

    private final NovusLixi plugin;

    public LixiAdminCommand(NovusLixi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("novusmc.lixi.admin")) {
            sender.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cBạn không có quyền thực hiện lệnh này!"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&eLệnh admin: &f/lixiadmin reload &7| &f/lixiadmin scan &7| &f/lixiadmin clear"));
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&aĐã tải lại cấu hình config.yml!"));
            return true;
        }

        if (sub.equals("scan")) {
            int scanned = 0;
            for (LixiPackage pkg : plugin.getLixiManager().getActivePackages().values()) {
                if (pkg.isExpired() || pkg.getRemainingSlots() <= 0) {
                    plugin.getLixiManager().archivePackage(pkg, "ADMIN_SCAN");
                    scanned++;
                }
            }
            sender.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&aĐã quét và lưu trữ &f" + scanned + " &abao lì xì hết hạn/đã hết lượt!"));
            return true;
        }

        if (sub.equals("clear")) {
            int total = plugin.getLixiManager().getActivePackages().size();
            for (LixiPackage pkg : plugin.getLixiManager().getActivePackages().values()) {
                plugin.getLixiManager().archivePackage(pkg, "ADMIN_FORCE_CLEAR");
            }
            sender.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cĐã đóng và hoàn tiền toàn bộ &f" + total + " &cbao lì xì đang phát!"));
            return true;
        }

        return true;
    }
}

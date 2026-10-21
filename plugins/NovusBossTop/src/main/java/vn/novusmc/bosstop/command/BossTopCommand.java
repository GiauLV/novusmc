package vn.novusmc.bosstop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vn.novusmc.bosstop.NovusBossTop;

public class BossTopCommand implements CommandExecutor {

    private final NovusBossTop plugin;

    public BossTopCommand(NovusBossTop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("novusmc.bosstop.admin")) {
            sender.sendMessage(NovusBossTop.color(plugin.getConfig().getString("prefix") + "&cBạn không có quyền sử dụng lệnh này!"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            String url = plugin.getConfig().getString("webhook-url", "");
            plugin.getDamageListener().setWebhookUrl(url);
            sender.sendMessage(NovusBossTop.color(plugin.getConfig().getString("prefix") + "&aĐã tải lại cấu hình config.yml thành công!"));
            return true;
        }

        sender.sendMessage(NovusBossTop.color(plugin.getConfig().getString("prefix") + "&eDùng: &f/bosstop reload &7để tải lại cấu hình Webhook."));
        return true;
    }
}

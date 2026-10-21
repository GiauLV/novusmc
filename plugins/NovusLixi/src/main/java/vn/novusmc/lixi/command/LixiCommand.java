package vn.novusmc.lixi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vn.novusmc.lixi.NovusLixi;

public class LixiCommand implements CommandExecutor {

    private final NovusLixi plugin;

    public LixiCommand(NovusLixi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Lenh nay chi danh cho nguoi choi!");
            return true;
        }

        Player p = (Player) sender;
        if (args.length < 3) {
            p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cCách dùng: &f/lixi <loại_tiền: kimte|linhngoc|hongngoc> <tổng_quỹ> <số_suất> [thời_gian: 12h|24h|48h]"));
            return true;
        }

        String currency = args[0].toLowerCase();
        if (!plugin.getConfig().contains("currencies." + currency)) {
            p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cLoại tiền không hợp lệ! Hỗ trợ: &fkimte, linhngoc, hongngoc"));
            return true;
        }

        double fund;
        int slots;
        try {
            fund = Double.parseDouble(args[1]);
            slots = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cTổng quỹ và số suất phải là số hợp lệ!"));
            return true;
        }

        if (fund <= 0 || slots <= 0) {
            p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cGiá trị phải lớn hơn 0!"));
            return true;
        }

        if (fund < slots) {
            p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&cTổng quỹ tối thiểu phải lớn hơn hoặc bằng số suất!"));
            return true;
        }

        long durationMillis = 24 * 3600 * 1000L; // mặc định 24h
        if (args.length >= 4) {
            String dur = args[3].toLowerCase();
            if (dur.equals("12h")) durationMillis = 12 * 3600 * 1000L;
            else if (dur.equals("48h")) durationMillis = 48 * 3600 * 1000L;
        }

        // Deduct money via command
        String takeCmd = plugin.getConfig().getString("currencies." + currency + ".command-take");
        if (takeCmd != null) {
            takeCmd = takeCmd.replace("{player}", p.getName()).replace("{amount}", String.valueOf((long)fund));
            p.getServer().dispatchCommand(p.getServer().getConsoleSender(), takeCmd);
        }

        plugin.getLixiManager().createPackage(p, currency, fund, slots, durationMillis);
        p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&aĐã tạo thành công bao lì xì trị giá &e" + (long)fund + " " + currency + "&a cho &f" + slots + "&a người!"));
        return true;
    }
}

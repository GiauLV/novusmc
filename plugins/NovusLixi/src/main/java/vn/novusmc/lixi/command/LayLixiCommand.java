package vn.novusmc.lixi.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vn.novusmc.lixi.NovusLixi;
import vn.novusmc.lixi.model.LixiPackage;

import java.util.Map;

public class LayLixiCommand implements CommandExecutor {

    private final NovusLixi plugin;

    public LayLixiCommand(NovusLixi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Lenh nay chi danh cho nguoi choi!");
            return true;
        }

        Player p = (Player) sender;
        Map<String, LixiPackage> pkgs = plugin.getLixiManager().getActivePackages();

        if (args.length >= 1) {
            String id = args[0];
            plugin.getLixiManager().claimPackage(p, id);
            return true;
        }

        if (pkgs.isEmpty()) {
            p.sendMessage(NovusLixi.color(plugin.getConfig().getString("prefix") + "&eHiện tại không có bao lì xì nào đang phát!"));
            return true;
        }

        p.sendMessage(NovusLixi.color("&6&l=== DANH SÁCH LÌ XÌ ĐANG HOẠT ĐỘNG ==="));
        for (LixiPackage pkg : pkgs.values()) {
            if (!pkg.isExpired() && pkg.getRemainingSlots() > 0) {
                String status = pkg.getClaimedPlayers().contains(p.getUniqueId()) ? "&7(Đã nhận)" :
                        pkg.getOwnerUuid().equals(p.getUniqueId()) ? "&e(Của bạn)" : "&a[BẤM ĐỂ NHẬN: /laylixi " + pkg.getId() + "]";
                p.sendMessage(NovusLixi.color("&e- &f" + pkg.getOwnerName() + " &7| &c" + (long)pkg.getSlotValue() + " " + pkg.getCurrency() + "/suất &7| Còn: &b" + pkg.getRemainingSlots() + "/" + pkg.getTotalSlots() + " &7| " + status));
            }
        }
        p.sendMessage(NovusLixi.color("&6&l======================================"));
        return true;
    }
}

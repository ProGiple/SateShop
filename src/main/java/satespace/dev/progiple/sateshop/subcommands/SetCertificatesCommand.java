package satespace.dev.progiple.sateshop.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Args;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.data.SQLManager;

import java.util.List;

@SubCommand(appliedCommand = "sateshop", commandIdentifiers = "setcertificates")
@Permissions("#.setcertificates")
@Args(min = 3, max = Integer.MAX_VALUE)
public class SetCertificatesCommand implements LunaExecutor {
    // /shop setcertificates <player> <amount>

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(strings[1]);
        if (offlinePlayer == null) {
            ConfigManager.sendMessage(sender, "playerIsNull", "name-%-" + strings[1]);
            return;
        }

        int amount = Math.max(0, LunaMath.toInt(strings[2]));
        SQLManager.setCertificatesCost(offlinePlayer.getUniqueId(), amount);
        ConfigManager.sendMessage(sender, "setCertificates",
                "name-%-" + offlinePlayer.getName(),
                "amount-%-" + amount);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return list.size() == 1 ? Utils.getPlayerNicks(list.get(0), sender) :
                list.size() == 2 ? List.of("<Кол-во>") : null;
    }
}

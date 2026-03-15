package satespace.dev.progiple.sateshop.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Args;
import org.novasparkle.lunaspring.API.commands.annotations.Flags;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.menus.menus.impl.GiftCertificateMenu;

import java.util.List;

@SubCommand(appliedCommand = "sateshop", commandIdentifiers = "gift")
@Flags(NoArgCommand.AccessFlag.PLAYER_ONLY)
@Args(min = 2, max = Integer.MAX_VALUE)
public class GiftCertificateCommand implements LunaExecutor {
    // shop gift <player>

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(strings[1]);
        if (player == null) {
            ConfigManager.sendMessage(sender, "playerIsNull", "name-%-" + strings[1]);
            return;
        }

        GiftCertificateMenu menu = new GiftCertificateMenu((Player) sender, player);
        MenuManager.openInventory(menu);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return list.size() == 1 ? Utils.getPlayerNicks(list.get(0), sender) : null;
    }
}

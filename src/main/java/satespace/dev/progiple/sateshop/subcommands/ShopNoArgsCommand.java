package satespace.dev.progiple.sateshop.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Flags;
import org.novasparkle.lunaspring.API.commands.annotations.ZeroArgCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.menus.menus.impl.CategoryMenu;

@ZeroArgCommand("sateshop")
@Flags(NoArgCommand.AccessFlag.PLAYER_ONLY)
public class ShopNoArgsCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Player player = (Player) sender;

        if (player.hasPermission("sateshop.blocked")) {
            ConfigManager.sendMessage(sender, "blocked");
            return;
        }

        CategoryMenu categoryMenu = new CategoryMenu(player, null, null);
        MenuManager.openInventory(categoryMenu);
    }
}

package satespace.dev.progiple.sateshop.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Args;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.util.service.managers.VanishManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.novasparkle.lunaspring.self.configuration.Message;
import satespace.dev.progiple.sateshop.data.Categories;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.menus.impl.CategoryMenu;

import java.util.List;

@SubCommand(appliedCommand = "sateshop", commandIdentifiers = "open")
@Args(min = 2, max = 3)
public class ShopOpenCommand implements LunaExecutor {
    // shop open <id> <player>

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(Categories.getCategories()
                .stream()
                .map(Category::id)
                .toList(), list.get(0)) : list.size() == 2 ? Utils.getPlayerNicks(list.get(1), sender) : null;
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        String menuId = strings[1];

        Category category = Categories.getCategory(menuId);
        if (category == null) {
            ConfigManager.sendMessage(sender, "categoryIsNull", "id-%-" + menuId);
            return;
        }

        if (strings.length == 2) {
            if (sender instanceof Player player) {
                if (sender.hasPermission("sateshop.blocked")) {
                    ConfigManager.sendMessage(sender, "blocked");
                    return;
                }

                CategoryMenu menu = new CategoryMenu(player, category, null);
                MenuManager.openInventory(menu);
            }
            else {
                ConfigManager.sendMessage(sender, "playerIsNull", "name-%-" + sender.getName());
                return;
            }
        }

        if (sender.hasPermission("sateshop.openother")) {
            Player player = VanishManager.exact(sender, strings[2]);
            if (player != null) {
                CategoryMenu menu = new CategoryMenu(player, category, null);
                MenuManager.openInventory(menu);
            }
            else
                ConfigManager.sendMessage(sender, "playerIsNull", "name-%-" + strings[2]);
        }
        else
            Message.NO_PERMISSION.send(sender);
    }
}

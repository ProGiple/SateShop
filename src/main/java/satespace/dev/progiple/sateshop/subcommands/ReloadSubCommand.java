package satespace.dev.progiple.sateshop.subcommands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import satespace.dev.progiple.sateshop.data.Categories;
import satespace.dev.progiple.sateshop.data.ConfigManager;

@SubCommand(appliedCommand = "sateshop", commandIdentifiers = "reload")
@Permissions("#.reload")
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        ConfigManager.reload();
        Categories.loadCategories();
        ConfigManager.sendMessage(sender, "reload");
    }
}

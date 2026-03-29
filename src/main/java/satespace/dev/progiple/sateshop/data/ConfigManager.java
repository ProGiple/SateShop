package satespace.dev.progiple.sateshop.data;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.PluginConfig;
import satespace.dev.progiple.sateshop.SateShop;

@UtilityClass
public class ConfigManager {
    private final PluginConfig config;
    static {
        config = new PluginConfig(SateShop.getPlugin());
    }

    public ConfigurationSection getSqlSection() {
        return config.getSection("sql");
    }

    public void reload() {
        config.reload();
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public void sendMessage(CommandSender sender, String path, String... rpl) {
        config.sendMessage(sender, path, rpl);
    }
}

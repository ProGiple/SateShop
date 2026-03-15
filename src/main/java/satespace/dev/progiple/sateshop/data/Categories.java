package satespace.dev.progiple.sateshop.data;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.util.service.managers.ColorManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import satespace.dev.progiple.sateshop.SateShop;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.ShopItem;

import java.io.File;
import java.util.*;

@UtilityClass
public class Categories {
    @Getter
    private final Set<Category> categories = new HashSet<>();

    public Category getCategory(String id) {
        return Utils.find(categories, c -> c.id().equals(id)).orElse(null);
    }

    public Category getCategory() {
        return Objects.requireNonNull(getCategory(ConfigManager.getString("mainCategory")));
    }

    public Category getCategoryByName(String name) {
        return Utils.find(categories, c -> c.name().equalsIgnoreCase(name)).orElse(null);
    }

    public void loadCategories() {
        categories.clear();

        File dir = new File(SateShop.getPlugin().getDataFolder(), "categories/");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();
                for (File file : files) {
                    IConfig config = new IConfig(file);

                    List<ShopItem> itemList = new ArrayList<>();
                    ConfigurationSection section = config.getSection("items");
                    if (section != null) for (String key : section.getKeys(false)) {
                        ConfigurationSection itemSection = section.getConfigurationSection(key);
                        ShopItem shopItem = new ShopItem(
                                key,
                                Material.matchMaterial(itemSection.getString("material")),
                                itemSection.getString("name", null),
                                itemSection.getInt("amount"),
                                itemSection.getStringList("description"),
                                itemSection.getBoolean("enchanted"),
                                itemSection.getString("baseHead", null),
                                itemSection.getInt("cost"),
                                itemSection.getStringList("commands")
                        );

                        itemList.add(shopItem);
                    }

                    Category category = new Category(
                            file.getName().replace(".yml", ""),
                            ColorManager.color(config.getString("name")),
                            config.getStringList("description"),
                            itemList
                    );

                    categories.add(category);
                }
            }
        }
    }
}

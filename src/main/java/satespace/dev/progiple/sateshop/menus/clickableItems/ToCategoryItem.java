package satespace.dev.progiple.sateshop.menus.clickableItems;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;
import satespace.dev.progiple.sateshop.data.Categories;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.menus.impl.CategoryMenu;

import java.util.ArrayList;
import java.util.List;

public class ToCategoryItem extends Item {
    public static final int STR_LENGTH = "category-".length();

    private final Category category;
    public ToCategoryItem(ConfigurationSection section) {
        super(section);
        this.category = Categories.getCategory(section.getName().substring(STR_LENGTH));

        this.setDisplayName(this.getDisplayName(), "name-%-" + category.name());

        List<String> lore = new ArrayList<>();
        for (String s : this.defaultLore) {
            if (s.contains("[description]"))
                lore.addAll(category.description());
            else
                lore.add(s);
        }

        this.setLore(lore, "name-%-" + category.name());
    }

    @Override
    public Item onClick(InventoryClickEvent e) {
        CategoryMenu categoryMenu = (CategoryMenu) this.getMenu();
        MenuManager.openInventory(new CategoryMenu((Player) e.getWhoClicked(), category, categoryMenu.recreatable));
        return this;
    }
}

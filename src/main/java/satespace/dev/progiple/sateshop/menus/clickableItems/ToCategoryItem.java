package satespace.dev.progiple.sateshop.menus.clickableItems;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;
import satespace.dev.progiple.sateshop.data.Categories;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.menus.AShopMenu;
import satespace.dev.progiple.sateshop.menus.menus.impl.CategoryMenu;

import java.util.ArrayList;
import java.util.List;

public class ToCategoryItem extends Item {
    public static final int STR_LENGTH = "category-".length();

    private final Category category;
    private final boolean isSelected;
    public ToCategoryItem(ConfigurationSection section, CategoryMenu menu) {
        super(section);
        this.category = Categories.getCategory(section.getName().substring(STR_LENGTH));

        this.isSelected = menu.category.equals(this.category);
        this.setDisplayName(this.getDisplayName(), "name-%-" + category.name());

        List<String> lore = new ArrayList<>();
        for (String s : this.defaultLore) {
            if (s.contains("[description]"))
                lore.addAll(category.description());
            else if (s.contains("[selected]")) {
                if (isSelected)
                    lore.addAll(menu.getSelectedCategoryLines());
            }
            else
                lore.add(s);
        }

        this.setLore(lore, "name-%-" + category.name());
        if (this.isSelected) this.setGlowing(true);
    }

    @Override
    public Item onClick(InventoryClickEvent e) {
        if (this.isSelected) return this;
        AShopMenu categoryMenu = (AShopMenu) this.getMenu();
        MenuManager.openInventory(new CategoryMenu((Player) e.getWhoClicked(), category, categoryMenu.recreatable));
        return this;
    }
}

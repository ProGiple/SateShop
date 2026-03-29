package satespace.dev.progiple.sateshop.menus.menus.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satemenus.menus.menus.Recreatable;
import satespace.dev.progiple.sateshop.SateShop;
import satespace.dev.progiple.sateshop.Tools;
import satespace.dev.progiple.sateshop.data.Categories;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.data.SQLManager;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.ShopItem;
import satespace.dev.progiple.sateshop.menus.clickableItems.BackItem;
import satespace.dev.progiple.sateshop.menus.clickableItems.SellingItem;
import satespace.dev.progiple.sateshop.menus.clickableItems.ToCategoryItem;
import satespace.dev.progiple.sateshop.menus.menus.AShopMenu;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CategoryMenu extends AShopMenu {
    private static final CooldownPrevent<UUID> prevent = new CooldownPrevent<>(125);

    public final Category category;
    public final String popularStatusName;
    public final int certificates;
    public CategoryMenu(@NotNull Player player, @Nullable Category category, @Nullable Recreatable recreatable) {
        super(player, new IConfig(SateShop.getPlugin().getDataFolder(), "menus/shop"), recreatable);
        this.certificates = SQLManager.getCertificatesCost(player.getUniqueId());

        if (category == null) category = Categories.getCategory();
        this.category = category;

        this.popularStatusName = ConfigManager.getString("popularStatus");
        this.initializeInv();
    }

    @Override
    protected String initializeTitle(String defaultTitle) {
        return Utils.applyReplacements(defaultTitle, "category-%-" + category.name());
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        ConfigurationSection items = config.getSection("items");
        for (String key : items.getKeys(false)) {
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            if (itemSection == null) continue;

            Item item;
            if (key.equalsIgnoreCase("back"))
                item = new BackItem(itemSection);
            else if (key.startsWith("category-"))
                item = new ToCategoryItem(itemSection, this);
            else if (key.startsWith("selling")) {
                List<String> order = itemSection.getStringList("order");
                List<Integer> slots = Utils.getSlotList(order);

                int sale = Tools.getSale(getPlayer());
                sale = Math.max(sale, Tools.getCategorySale(getPlayer(), category));

                ConfigurationSection ifHasSaleSection = itemSection.getConfigurationSection("ifHasSale");

                Iterator<ShopItem> shopItems = category.items().iterator();
                for (int slot : slots) {
                    if (!shopItems.hasNext()) break;

                    ShopItem shopItem = shopItems.next();
                    sale = Math.max(sale, Tools.getItemSale(getPlayer(), shopItem));

                    SellingItem sellingItem = new SellingItem(
                            sale > 0 ? ifHasSaleSection : itemSection,
                            slot,
                            this,
                            shopItem,
                            sale);
                    this.addItems(true, sellingItem);
                }

                continue;
            }
            else
                continue;

            this.addItems(true, item);
        }

        super.onOpen(e);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (prevent.isCancelled(event, event.getWhoClicked().getUniqueId())) return;
        super.onClick(event);
    }

    public List<String> getSelectedCategoryLines() {
        return config.getStringList("selectedCategoryLines");
    }

    @Override
    public IMenu reCreate(Player player) {
        return new CategoryMenu(player, this.category, this.recreatable);
    }
}

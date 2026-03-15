package satespace.dev.progiple.sateshop.menus.clickableItems;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satemenus.menus.menus.Recreatable;
import satespace.dev.progiple.sateshop.SateShop;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.data.SQLManager;
import satespace.dev.progiple.sateshop.Tools;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.ShopItem;
import satespace.dev.progiple.sateshop.menus.menus.impl.ApplyMenu;
import satespace.dev.progiple.sateshop.menus.menus.impl.CategoryMenu;

import java.util.function.BiPredicate;

public class SellingItem extends Item {
    public static final BiPredicate<Category, ShopItem> isPopular =
            (c, i) -> SQLManager.topSellingItems.contains(c.id() + "." + i.id());

    private final ShopItem shopItem;
    private final int sale;
    private final Category category;
    private int playerBalance;
    public SellingItem(ConfigurationSection section,
                       int slot,
                       CategoryMenu shopMenu,
                       ShopItem shopItem,
                       int sale) {
        super(section, slot);
        this.shopItem = shopItem;

        this.category = shopMenu.category;
        this.playerBalance = SateShop.getSjapi().getJewels(shopMenu.getPlayer());

        if (shopItem.material() != null) setMaterial(shopItem.material());
        if (shopItem.amount() > 0) setAmount(shopItem.amount());
        if (shopItem.enchanted()) setGlowing(true);
        if (shopItem.baseHead() != null) NBTManager.base64head(getItemStack(), shopItem.baseHead());

        Category category = shopMenu.category;
        this.sale = sale;

        String statusName = isPopular.test(category, shopItem) ? "" : shopMenu.popularStatusName;
        String[] rpl = {
                "status-%-" + statusName,
                "name-%-" + shopItem.name(),
                "category-%-" + category.name(),
                "fullCost-%-" + shopItem.cost(),
                "cost-%-" + (shopItem.cost() * (1.0 - this.sale / 100)),
                "sale-%-" + sale,
                "certificates-%-" + shopMenu.certificates,
                "balance-%-" + playerBalance,
                "fullBalance-%-" + (playerBalance + shopMenu.certificates),
        };

        this.setDisplayName(Utils.setPlaceholders(shopMenu.getPlayer(), this.getDisplayName()), rpl);
        this.replaceLore(l -> Utils.setPlaceholders(shopMenu.getPlayer(), Utils.applyReplacements(l, rpl)));
    }

    @Override
    public Item onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        int cost = (int) (shopItem.cost() * (1.0 - (double) this.sale / 100));

        int certificates = ((CategoryMenu) this.getMenu()).certificates;
        if (certificates + playerBalance < cost) {
            certificates = SQLManager.getCertificatesCost(player.getUniqueId());
            this.playerBalance = SateShop.getSjapi().getJewels(player);
            if (certificates + playerBalance < cost) {
                ConfigManager.sendMessage(player, "hasntJewels",
                        "name-%-" + shopItem.name(),
                        "amount-%-" + shopItem.amount(),
                        "cost-%-" + cost);
                return this;
            }
        }

        Recreatable recreatable = this.getMenu() instanceof Recreatable r ? r : null;
        ApplyMenu applyMenu = new ApplyMenu(player, category, shopItem, recreatable, cost);
        MenuManager.openInventory(applyMenu);
        return this;
    }
}

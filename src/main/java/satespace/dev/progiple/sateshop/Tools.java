package satespace.dev.progiple.sateshop;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.ShopItem;

@UtilityClass
public class Tools {
    public int getItemSale(Player player, ShopItem shopItem) {
        String pattern = "sateshop.sale.item." + shopItem.id() + ".";
        return getPatternSale(player, pattern);
    }

    public int getCategorySale(Player player, Category category) {
        String pattern = "sateshop.sale.category." + category.id() + ".";
        return getPatternSale(player, pattern);
    }

    public int getSale(Player player) {
        String pattern = "sateshop.sale.all.";
        return getPatternSale(player, pattern);
    }

    public int getSale(Player player, Category category, ShopItem shopItem) {
        int allSale = getSale(player);
        int categSale = getCategorySale(player, category);
        int itemSale = getItemSale(player, shopItem);
        return Math.max(allSale, Math.max(categSale, itemSale));
    }

    public int getPatternSale(Player player, String pattern) {
        return player.getEffectivePermissions()
                .stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(p -> p.startsWith(pattern))
                .mapToInt(p -> LunaMath.toInt(p.substring(pattern.length())))
                .max()
                .orElse(0);
    }
}

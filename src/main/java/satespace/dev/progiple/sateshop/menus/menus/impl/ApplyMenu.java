package satespace.dev.progiple.sateshop.menus.menus.impl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.satemenus.menus.menus.Backable;
import org.satellite.dev.progiple.satemenus.menus.menus.Recreatable;
import satespace.dev.progiple.sateshop.SateShop;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.data.SQLManager;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.ShopItem;
import satespace.dev.progiple.sateshop.menus.clickableItems.BackItem;
import satespace.dev.progiple.sateshop.menus.menus.AShopMenu;

import java.util.List;

public class ApplyMenu extends AShopMenu {
    private final ShopItem shopItem;
    private final Category category;
    private final int cost;
    public ApplyMenu(@NotNull Player player,
                     Category category,
                     ShopItem shopItem,
                     @Nullable Recreatable recreatable,
                     int cost) {
        super(player, new IConfig(SateShop.getPlugin().getDataFolder(), "menus/apply"), recreatable);
        this.shopItem = shopItem;
        this.cost = cost;

        this.category = category;
        ConfigurationSection section = config.getSection("items");

        ConfigurationSection itemSection = section.getConfigurationSection("confirm");
        List<Integer> slots = Utils.getSlotList(itemSection.getStringList("slots"));
        for (int slot : slots) {
            ConfirmItem confirmItem = new ConfirmItem(itemSection, slot);
            this.addItems(false, confirmItem);
        }

        itemSection = section.getConfigurationSection("cancel");
        slots = Utils.getSlotList(itemSection.getStringList("slots"));
        for (int slot : slots) {
            BackItem backItem = new BackItem(itemSection, slot);
            this.addItems(false, backItem);
        }

        int infoSlot = section.getInt("infoItemSlot", -1);
        if (infoSlot >= 0) {
            Item item = new Item(
                    shopItem.material(),
                    shopItem.name(),
                    shopItem.description(),
                    shopItem.amount(),
                    (byte) infoSlot);
            NBTManager.base64head(item.getItemStack(), shopItem.baseHead());
            this.addItems(false, item);
        }
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.insertAll();
        super.onOpen(e);
    }

    @Override
    public IMenu reCreate(Player player) {
        return new ApplyMenu(player, category, shopItem, recreatable, cost);
    }

    private class ConfirmItem extends Item {
        public ConfirmItem(ConfigurationSection section, int slot) {
            super(section, slot);
        }

        @Override
        public Item onClick(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();

            int certificates = SQLManager.getCertificatesCost(player.getUniqueId());
            int balance = SateShop.getSjapi().getJewels(player);

            int leftCost = cost;
            String messageId;
            if (balance + certificates >= leftCost) {
                boolean restoreCertificatesFlag = certificates > 0;
                leftCost -= certificates;
                if (leftCost > 0) {
                    SateShop.getSjapi().removeJewels(player, certificates);
                    certificates = 0;
                }
                else
                    certificates = -leftCost;

                if (restoreCertificatesFlag) {
                    SQLManager.setCertificatesCost(player.getUniqueId(), certificates);
                }

                String[] rpl = {"player-%-" + player.getName(), "amount-%-" + shopItem.amount()};
                for (String command : shopItem.commands()) {
                    Utils.processCommandsWithActions(player, command, rpl);
                }

                messageId = "successBuy";
                SQLManager.addSellingItem(player, category, shopItem, cost);
            }
            else
                messageId = "hasntJewels";

            ConfigManager.sendMessage(player, messageId,
                    "name-%-" + shopItem.name(),
                    "cost-%-" + cost,
                    "amount-%-" + shopItem.amount());
            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            return this;
        }
    }
}

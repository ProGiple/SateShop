package satespace.dev.progiple.sateshop.menus.menus.impl;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.items.Item;
import satespace.dev.progiple.sateshop.SateShop;
import satespace.dev.progiple.sateshop.data.ConfigManager;
import satespace.dev.progiple.sateshop.data.SQLManager;
import satespace.dev.progiple.sateshop.menus.menus.AShopMenu;

import java.util.UUID;

public class GiftCertificateMenu extends AShopMenu {
    private static final CooldownPrevent<UUID> prevent = new CooldownPrevent<>(100);

    private final OfflinePlayer target;
    public GiftCertificateMenu(@NotNull Player player, OfflinePlayer target) {
        super(player, new IConfig(SateShop.getPlugin().getDataFolder(), "menus/certificates"), null);
        this.target = target;

        ConfigurationSection itemsSection = config.getSection("items");
        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection section = itemsSection.getConfigurationSection(key);
            this.addItems(false, new BuyCertificateItem(section));
        }

        this.initializeInv();
    }

    @Override
    protected String initializeTitle(String defaultTitle) {
        String name = target.getName();
        if (name == null) return config.getString("ifNullTitle");
        return defaultTitle.replace("[player]", name);
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        this.insertAll();
        super.onOpen(e);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (prevent.isCancelled(event, event.getWhoClicked().getUniqueId())) return;
        super.onClick(event);
    }

    @Override
    public IMenu reCreate(Player player) {
        return new GiftCertificateMenu(player, target);
    }

    private class BuyCertificateItem extends Item {
        private final int cost;
        public BuyCertificateItem(ConfigurationSection config) {
            super(config);
            this.cost = config.getInt("cost");
        }

        @Override
        public Item onClick(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();

            int balance = SateShop.getSjapi().getJewels(player);
            if (balance < cost) {
                return this;
            }

            SateShop.getSjapi().removeJewels(player, cost);
            int certificates = SQLManager.getCertificatesCost(target.getUniqueId());
            SQLManager.setCertificatesCost(target.getUniqueId(), certificates + cost);

            player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
            ConfigManager.sendMessage(player, "buyCertificate",
                    "target-%-" + target.getName(),
                    "cost-%-" + cost);

            if (target.isOnline()) {
                ConfigManager.sendMessage(target.getPlayer(), "getCertificate",
                        "buyer-%-" + player.getName(),
                        "cost-%-" + cost);
            }

            return this;
        }
    }
}

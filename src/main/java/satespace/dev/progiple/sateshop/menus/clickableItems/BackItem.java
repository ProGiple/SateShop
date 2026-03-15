package satespace.dev.progiple.sateshop.menus.clickableItems;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.novasparkle.lunaspring.API.menus.items.Item;
import org.satellite.dev.progiple.satemenus.menus.menus.Backable;

public class BackItem extends Item {
    public BackItem(ConfigurationSection section) {
        super(section);
    }

    public BackItem(ConfigurationSection section, int slot) {
        super(section, slot);
    }

    @Override
    public Item onClick(InventoryClickEvent e) {
        if (this.getMenu() instanceof Backable bm)
            if (!bm.back((Player) e.getWhoClicked()))
                e.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.PLUGIN);
        return super.onClick(e);
    }
}

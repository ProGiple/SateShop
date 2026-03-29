package satespace.dev.progiple.sateshop.menus.menus;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.configuration.IConfig;
import org.novasparkle.lunaspring.API.menus.AMenu;
import org.novasparkle.lunaspring.API.menus.IMenu;
import org.novasparkle.lunaspring.API.menus.MenuManager;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.satemenus.menus.menus.AnimatedMenu;
import org.satellite.dev.progiple.satemenus.menus.menus.Backable;
import org.satellite.dev.progiple.satemenus.menus.menus.Recreatable;
import org.satellite.dev.progiple.satemenus.menus.params.animations.Animations;
import org.satellite.dev.progiple.satemenus.menus.params.animations.IAnimation;

import java.util.List;

@Getter @Setter
public abstract class AShopMenu extends AMenu implements AnimatedMenu, Recreatable, Backable {
    protected final IConfig config;
    public final Recreatable recreatable;
    private IAnimation playingAnimation;
    public AShopMenu(@NotNull Player player, IConfig config, @Nullable Recreatable recreatable) {
        super(player);
        this.config = config;

        if (recreatable == null) {
            IMenu menu = MenuManager.getActiveMenu(player);
            this.recreatable = menu instanceof Recreatable r ? r : null;
        }
        else
            this.recreatable = recreatable;
    }

    protected void initializeInv() {
        String title = config.getString("title");
        this.initialize(title == null ? "" : this.initializeTitle(title),
                (byte) (config.getInt("rows") * 9),
                config.getSection("decorations"));
    }

    protected String initializeTitle(String defaultTitle) {
        return defaultTitle;
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        List<String> animationNames = config.getStringList("animations");
        IAnimation animation = Animations.get(LunaMath.getRandom(animationNames));
        if (animation != null) animation.play(this);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
        super.onClick(event);
    }

    @Override
    public boolean back(Player player) {
        if (this.recreatable != null) {
            IMenu menu = this.recreatable.reCreate(player);
            if (menu == null) return false;

            MenuManager.openInventory(menu);
            return true;
        }

        return false;
    }
}

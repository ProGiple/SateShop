package satespace.dev.progiple.sateshop.menus.menus;

import org.bukkit.entity.Player;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AShopMenu extends AMenu implements AnimatedMenu, Recreatable, Backable {
    protected final IConfig config;
    private final List<IAnimation> animations;
    public final Recreatable recreatable;
    public AShopMenu(@NotNull Player player, IConfig config, @Nullable Recreatable recreatable) {
        super(player);
        this.config = config;
        this.animations = new ArrayList<>();

        if (recreatable == null) {
            IMenu menu = MenuManager.getActiveMenu(player);
            this.recreatable = menu instanceof Recreatable r ? r : null;
        }
        else
            this.recreatable = recreatable;

        this.initialize(config.getString("title"),
                (byte) (config.getInt("rows") * 9),
                config.getSection("decorations"));
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        List<String> animationNames = config.getStringList("animations");
        IAnimation animation = Animations.get(LunaMath.getRandom(animationNames));
        if (animation != null) animation.play(this);
    }

    @Override
    public Collection<IAnimation> getPlayingAnimations() {
        return animations;
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

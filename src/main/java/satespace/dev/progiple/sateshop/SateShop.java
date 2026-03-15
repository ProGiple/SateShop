package satespace.dev.progiple.sateshop;

import lombok.Getter;
import org.novasparkle.lunaspring.API.commands.CommandInitializer;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.progiple.satejewels.SateJewels;
import org.satellite.progiple.satejewels.api.SJAPI;
import satespace.dev.progiple.sateshop.data.Categories;
import satespace.dev.progiple.sateshop.data.SQLManager;

public final class SateShop extends LunaPlugin {
    @Getter private static SateShop plugin;
    @Getter private static SJAPI sjapi;

    @Override
    public void onEnable() {
        super.onEnable();
        plugin = this;
        sjapi = SateJewels.getINSTANCE().getAPI();

        if (!this.dirExists())
            this.loadFile("categories/example.yml");

        this.loadFiles("menus/apply.yml", "menus/certificates.yml", "menus/shop.yml");

        this.saveDefaultConfig();
        Categories.loadCategories();

        SQLManager.initializeTopList();
        CommandInitializer.initialize(this, "#.subcommands");
    }
}

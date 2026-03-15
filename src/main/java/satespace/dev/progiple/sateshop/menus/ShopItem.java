package satespace.dev.progiple.sateshop.menus;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ShopItem(String id,
                       @Nullable Material material,
                       @Nullable String name,
                       int amount,
                       List<String> description,
                       boolean enchanted,
                       @Nullable String baseHead,
                       int cost,
                       List<String> commands) {
}

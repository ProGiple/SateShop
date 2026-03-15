package satespace.dev.progiple.sateshop.menus;

import java.util.List;

public record Category(String id,
                       String name,
                       List<String> description,
                       List<ShopItem> items) {
}

package satespace.dev.progiple.sateshop.data;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.database.AsyncExecutor;
import satespace.dev.progiple.sateshop.menus.Category;
import satespace.dev.progiple.sateshop.menus.ShopItem;

import java.util.*;

@UtilityClass
public class SQLManager {
    private final AsyncExecutor asyncExecutor;
    private final String certificateTable;
    private final String transactionsTable;
    public final Set<String> topSellingItems = new HashSet<>();
    static {
        asyncExecutor = new AsyncExecutor();

        asyncExecutor.executeSync("CREATE TABLE IF NOT EXISTS " + certificateTable +
                "(uuid varchar(36) PRIMARY KEY, amount int);");
        asyncExecutor.executeSync("CREATE TABLE IF NOT EXISTS " + transactionsTable +
                "(uuid varchar(36), playerName varchar(64), cost int, categoryId varchar(64), itemId varchar(64));");
    }

    public void initializeTopList() {
        int maxItems = ;

        List<String> topSellingItemsList = asyncExecutor.executeQuery("SELECT CONCAT(categoryId, '.', itemId) as item_key," +
                " SUM(cost) as total_cost" +
                " FROM " + transactionsTable +
                " GROUP BY categoryId, itemId" +
                " ORDER BY total_cost ASC" +
                " LIMIT ?;", rs -> rs.getString("item_key"), maxItems);
        topSellingItems.addAll(topSellingItemsList);
    }

    public int getCertificatesCost(UUID uuid) {
        List<Integer> list = asyncExecutor.executeQuery("SELECT * FROM " + certificateTable + " WHERE uuid=?;",
                rs -> rs.getInt("amount"), uuid.toString());
        if (list.isEmpty()) return 0;
        return list.get(0);
    }

    public void setCertificatesCost(UUID uuid, int newAmo) {
        asyncExecutor.executeSync("INSERT INTO " + certificateTable + " VALUES (?, ?)" +
                " ON DUPLICATE KEY UPDATE amount=?", uuid.toString(), newAmo, newAmo);
    }

    public int getSellingTotalCost(UUID uuid) {
        List<Integer> selling = asyncExecutor.executeQuery("SELECT SUM(cost) as total_cost" +
                " FROM " + transactionsTable +
                " WHERE uuid=?;",
                rs -> rs.getInt("total_cost"),
                uuid.toString());
        if (selling.isEmpty()) return 0;
        return selling.get(0);
    }

    public void addSellingItem(Player player, Category category, ShopItem shopItem, int cost) {
        asyncExecutor.executeAsync("INSERT INTO " + transactionsTable + " VALUES (?, ?, ?, ?, ?);",
                player.getUniqueId().toString(), player.getName(), cost, category.id(), shopItem.id());
    }
}

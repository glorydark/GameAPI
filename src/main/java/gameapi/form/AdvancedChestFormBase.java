package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.form.response.ChestResponse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/*
 * AdvancedForm was originally from lt-name's MemoriesOfTime-GameCore.
 * I (the author) made scant changes in constructors and so on to be easier to use.
 */
public abstract class AdvancedChestFormBase {

    protected Map<Integer, Item> inventory = new LinkedHashMap<>();

    protected Map<Integer, BiConsumer<Player, Item>> responseMap = new LinkedHashMap<>();

    public Map<Integer, Item> getInventory() {
        return inventory;
    }

    public Map<Integer, BiConsumer<Player, Item>> getResponseMap() {
        return responseMap;
    }

    public void dealResponse(Player player, ChestResponse chestResponse) {

    }

    public void close(Player player) {

    }

    public void showToPlayer(Player player) {

    }

    protected void closeProcess(Player player) {

    }
}
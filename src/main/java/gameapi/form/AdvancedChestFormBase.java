package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import gameapi.form.response.ChestResponse;
import gameapi.utils.FakeBlockCacheData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AdvancedChestFormBase {

    protected String title;

    protected Map<Integer, Item> inventory = new LinkedHashMap<>();

    protected Map<Integer, BiConsumer<Player, Item>> responseMap = new LinkedHashMap<>();

    protected LinkedHashMap<Player, List<FakeBlockCacheData>> fakeBlocks = new LinkedHashMap<>();

    public AdvancedChestFormBase(String title) {
        this.title = title;
    }

    public void dealResponse(Player player, ChestResponse chestResponse) {

    }

    public void close(Player player) {

    }

    public void showToPlayer(Player player) {

    }

    protected void closeProcess(Player player) {

    }

    public Map<Integer, Item> getInventory() {
        return inventory;
    }

    public Map<Integer, BiConsumer<Player, Item>> getResponseMap() {
        return responseMap;
    }

    public String getTitle() {
        return title;
    }
}
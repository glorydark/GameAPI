package gameapi.form.inventory;

import cn.nukkit.Player;
import cn.nukkit.inventory.BaseInventory;

/**
 * @author glorydark
 */
public class AdvancedFakeBlockInventory extends BaseInventory {

    protected String title;

    protected BlockFakeInventoryType fakeBlockFormType;

    public AdvancedFakeBlockInventory(BlockFakeInventoryType fakeBlockFormType) {
        this(null, fakeBlockFormType);
        this.fakeBlockFormType = fakeBlockFormType;
    }

    public AdvancedFakeBlockInventory(String title, BlockFakeInventoryType fakeBlockFormType) {
        super(null, fakeBlockFormType.getInventoryType());

        this.title = title == null ? fakeBlockFormType.getInventoryType().getDefaultTitle() : title;
        this.fakeBlockFormType = fakeBlockFormType;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BlockFakeInventoryType getFakeBlockFormType() {
        return fakeBlockFormType;
    }

    public void setFakeBlockFormType(BlockFakeInventoryType fakeBlockFormType) {
        this.fakeBlockFormType = fakeBlockFormType;
    }

    protected void postCloseExecute(Player player) {

    }

    public void closeForPlayer(Player player) {

    }

    public void showToPlayer(Player player) {

    }

    public BaseInventory getResultInventory() {
        return this;
    }
}

package gameapi.form.inventory;

import cn.nukkit.Player;
import cn.nukkit.inventory.BaseInventory;

/**
 * @author glorydark
 */
public class AdvancedFakeInventory extends BaseInventory {

    protected String title;

    protected FakeInventoryType fakeBlockFormType;

    public AdvancedFakeInventory(FakeInventoryType fakeBlockFormType) {
        this(null, fakeBlockFormType);
        this.fakeBlockFormType = fakeBlockFormType;
    }

    public AdvancedFakeInventory(String title, FakeInventoryType fakeBlockFormType) {
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

    public FakeInventoryType getFakeBlockFormType() {
        return fakeBlockFormType;
    }

    public void setFakeBlockFormType(FakeInventoryType fakeBlockFormType) {
        this.fakeBlockFormType = fakeBlockFormType;
    }

    protected void closeProcess(Player player) {

    }

    public void closeForPlayer(Player player) {

    }

    public void showToPlayer(Player player) {

    }
}

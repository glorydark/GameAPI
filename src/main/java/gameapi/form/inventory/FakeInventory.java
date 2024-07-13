package gameapi.form.inventory;

import cn.nukkit.Player;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import gameapi.form.AdvancedFakeBlockContainerFormBase;
import gameapi.utils.FakeBlockCacheData;

/**
 * Reference:
 * https://github.com/CloudburstMC/FakeInventories/blob/master/src/main/java/com/nukkitx/fakeinventories/inventory/FakeInventory.java
 */
public class FakeInventory extends BaseInventory {

    private String title;

    private final FakeBlockCacheData fakeBlockCacheData;

    private final AdvancedFakeBlockContainerFormBase formBase;

    public FakeInventory(AdvancedFakeBlockContainerFormBase formBase, FakeBlockCacheData fakeBlockCacheData, InventoryType inventoryType) {
        this(formBase, fakeBlockCacheData, inventoryType, formBase.getTitle());
    }

    public FakeInventory(AdvancedFakeBlockContainerFormBase formBase, FakeBlockCacheData fakeBlockCacheData, InventoryType inventoryType, String title) {
        super(null, inventoryType);

        this.setContents(formBase.getInventory());
        this.title = title == null ? inventoryType.getDefaultTitle() : title;
        this.formBase = formBase;
        this.fakeBlockCacheData = fakeBlockCacheData;
    }

    @Override
    public void onOpen(Player player) {
        ContainerOpenPacket packet = new ContainerOpenPacket();
        packet.windowId = player.getWindowId(this);
        packet.type = this.getType().getNetworkType();

        packet.x = this.fakeBlockCacheData.getX();
        packet.y = this.fakeBlockCacheData.getY();
        packet.z = this.fakeBlockCacheData.getZ();
        player.dataPacket(packet);

        super.onOpen(player);

        this.sendContents(player);
    }

    @Override
    public void onClose(Player player) {
        ContainerClosePacket packet = new ContainerClosePacket();
        packet.windowId = player.getWindowId(this);
        packet.wasServerInitiated = player.getClosingWindowId() != packet.windowId;
        player.dataPacket(packet);

        super.onClose(player);
        formBase.close(player);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AdvancedFakeBlockContainerFormBase getFormBase() {
        return formBase;
    }

    public FakeBlockCacheData getFakeBlockCacheData() {
        return fakeBlockCacheData;
    }
}
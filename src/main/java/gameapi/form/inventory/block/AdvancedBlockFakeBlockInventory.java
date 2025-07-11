package gameapi.form.inventory.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.inventory.AdvancedFakeBlockInventory;
import gameapi.form.inventory.BlockFakeInventoryType;
import gameapi.form.response.BlockInventoryResponse;
import gameapi.utils.FakeBlockCacheData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AdvancedBlockFakeBlockInventory extends AdvancedFakeBlockInventory {

    protected Map<Integer, BiConsumer<Player, BlockInventoryResponse>> responseMap = new LinkedHashMap<>();
    protected boolean itemMovable;
    @Deprecated
    protected boolean itemTakeIntoInventory;
    private List<FakeBlockCacheData> fakeBlockList = new ArrayList<>();

    public AdvancedBlockFakeBlockInventory(BlockFakeInventoryType fakeBlockFormType) {
        this(null, fakeBlockFormType);
    }

    public AdvancedBlockFakeBlockInventory(String title, BlockFakeInventoryType fakeBlockFormType) {
        super(title, fakeBlockFormType);
    }

    public void dealOnClickResponse(Player player, BlockInventoryResponse blockInventoryResponse) {

    }

    public Map<Integer, BiConsumer<Player, BlockInventoryResponse>> getResponseMap() {
        return responseMap;
    }

    public List<FakeBlockCacheData> getFakeBlockList() {
        return fakeBlockList;
    }

    public void setFakeBlockList(List<FakeBlockCacheData> fakeBlockList) {
        this.fakeBlockList = fakeBlockList;
    }

    @Override
    public void onOpen(Player player) {
        if (!this.getFakeBlockList().isEmpty()) {
            FakeBlockCacheData fakeBlock = this.getFakeBlockList().get(0);
            ContainerOpenPacket packet = new ContainerOpenPacket();
            packet.windowId = player.getWindowId(this);
            packet.type = this.getType().getNetworkType();

            packet.x = fakeBlock.getX();
            packet.y = fakeBlock.getY();
            packet.z = fakeBlock.getZ();
            player.dataPacket(packet);
        }
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
        this.postCloseExecute(player);
    }

    public void addItemToSlot(int slot, Item item) {
        Map<Integer, Item> itemMap = this.getContents();
        itemMap.put(slot, item);
        this.setContents(itemMap);
        if (!this.getViewers().isEmpty()) {
            this.sendContents(this.getViewers());
        }
    }

    public void addItemToSlot(int slot, ResponsiveElementSlotItem item) {
        Map<Integer, Item> itemMap = this.getContents();
        itemMap.put(slot, item.getItem());
        this.responseMap.put(slot, item.getOnClickResponse());
        this.setContents(itemMap);
        if (!this.getViewers().isEmpty()) {
            this.sendContents(this.getViewers());
        }
    }

    @Override
    public boolean clear(int index, boolean send) {
        this.responseMap.remove(index);
        return super.clear(index, send);
    }

    @Override
    public void clearAll() {
        this.responseMap.clear();
        super.clearAll();
    }

    public boolean isItemMovable() {
        return itemMovable;
    }

    public boolean isItemTakeIntoInventory() {
        return itemTakeIntoInventory;
    }

    public void setItemMovable(boolean itemMovable) {
        this.itemMovable = itemMovable;
    }

    public void setItemTakeIntoInventory(boolean itemTakeIntoInventory) {
        this.itemTakeIntoInventory = itemTakeIntoInventory;
    }

    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title, boolean pair) {
        // 获取blockEntity的tag，大箱子还需传入pair参数
        CompoundTag result = BlockEntity.getDefaultCompound(position, this.getFakeBlockFormType().getBlockEntityIdentifier())
                .putString("CustomName", title);
        if (pair) {
            // 这里默认写死pairX = -1
            int pairX = -1;
            result.putInt("pairx", position.getFloorX() + pairX)
                    .putInt("pairz", position.getFloorZ());
        }
        return result;
    }

    protected Position getValidPosition(Player player) {
        Level level = player.getLevel();
        if (player.getY() - 1 >= level.getMinBlockY()) {
            return player.add(0, -1, 0);
        } else if (player.getY() + 3 <= level.getMaxBlockY()) {
            return player.add(0, 3, 0);
        } else {
            return player.add(1, 0, 1);
        }
    }

    protected void removeFakeBlock(Player player) {
        if (!this.getFakeBlockList().isEmpty()) {
            List<FakeBlockCacheData> cacheDataList = this.getFakeBlockList();
            for (FakeBlockCacheData cacheData : cacheDataList) {
                UpdateBlockPacket pk = new UpdateBlockPacket();
                pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, cacheData.getBlock().getId(), cacheData.getBlock().getDamage());
                pk.flags = UpdateBlockPacket.FLAG_ALL;
                pk.x = cacheData.getX();
                pk.y = cacheData.getY();
                pk.z = cacheData.getZ();
                player.dataPacket(pk);
            }
            this.setFakeBlockList(new ArrayList<>());
        }
    }
}
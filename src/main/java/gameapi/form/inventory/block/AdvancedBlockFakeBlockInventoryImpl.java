package gameapi.form.inventory.block;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.InventoryMoveItemEvent;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import gameapi.form.inventory.BlockFakeInventoryType;
import gameapi.form.response.BlockInventoryResponse;
import gameapi.listener.AdvancedFormListener;
import gameapi.utils.FakeBlockCacheData;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 * <p>
 * Reference:
 * https://github.com/CloudburstMC/FakeInventories/blob/master/src/main/java/com/nukkitx/fakeinventories/inventory/FakeInventory.java
 */
public abstract class AdvancedBlockFakeBlockInventoryImpl extends AdvancedBlockFakeBlockInventory {
    protected BiConsumer<Player, BlockInventoryResponse> clickBiConsumer = null;

    protected Consumer<Player> closeConsumer = null;

    protected BiConsumer<Player, InventoryMoveItemEvent> onSlotChangeConsumer = null;

    public AdvancedBlockFakeBlockInventoryImpl(BlockFakeInventoryType fakeBlockFormType) {
        super(null, fakeBlockFormType);
    }

    public AdvancedBlockFakeBlockInventoryImpl(String title, BlockFakeInventoryType fakeBlockFormType) {
        super(title, fakeBlockFormType);
    }

    @Override
    public void showToPlayer(Player player) {
        Position position = getValidPosition(player);

        // 往客户端生成一个虚假方块
        UpdateBlockPacket pk = new UpdateBlockPacket();
        pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, this.getFakeBlockFormType().getBlockId(), 0);
        pk.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        pk.x = position.getFloorX();
        pk.y = position.getFloorY();
        pk.z = position.getFloorZ();
        player.dataPacket(pk);

        // 必要时客户端方面生成一个假的BlockEntity作为载体
        if (!this.getFakeBlockFormType().getBlockEntityIdentifier().isEmpty()) {
            BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
            blockEntityDataPacket.x = position.getFloorX();
            blockEntityDataPacket.y = position.getFloorY();
            blockEntityDataPacket.z = position.getFloorZ();
            try {
                blockEntityDataPacket.namedTag = NBTIO.write(this.getBlockEntityDataAt(position, title, false), ByteOrder.LITTLE_ENDIAN, true);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            player.dataPacket(blockEntityDataPacket);
        }

        // 记录假方块信息，用于之后移除
        FakeBlockCacheData fakeBlockCacheData = new FakeBlockCacheData(pk.x, pk.y, pk.z, player.getLevel(), position.getLevelBlock());
        this.getFakeBlockList().add(fakeBlockCacheData);

        AdvancedFormListener.addChestMenuCache(player, this);
        // 向玩家展示窗口
        player.addWindow(getResultInventory());
    }

    @Override
    public void dealOnClickResponse(Player player, BlockInventoryResponse blockInventoryResponse) {
        if (blockInventoryResponse == null) {
            Consumer<Player> consumer = this.getCloseConsumer();
            if (consumer != null) {
                consumer.accept(player);
            }
        } else {
            BiConsumer<Player, BlockInventoryResponse> consumer = this.getResponseMap().get(blockInventoryResponse.getSlot());
            if (consumer != null) {
                consumer.accept(player, blockInventoryResponse);
            }

            consumer = this.getClickBiConsumer();
            if (consumer != null) {
                consumer.accept(player, blockInventoryResponse);
            }
        }
    }

    public void dealOnSlotChangeResponse(InventoryMoveItemEvent event) {
        for (Player player : event.getViewers()) {
            this.getOnSlotChangeConsumer().accept(player, event);
        }
    }

    @Override
    public void closeForPlayer(Player player) {
        this.postCloseExecute(player);
    }

    @Override
    protected void postCloseExecute(Player player) {
        this.removeFakeBlock(player);
        AdvancedFormListener.removeChestMenuCache(player);
    }

    public BiConsumer<Player, BlockInventoryResponse> getClickBiConsumer() {
        return clickBiConsumer;
    }

    public Consumer<Player> getCloseConsumer() {
        return closeConsumer;
    }

    public void onSlotChange(BiConsumer<Player, InventoryMoveItemEvent> onMoveConsumer) {
        this.onSlotChangeConsumer = onMoveConsumer;
    }

    public BiConsumer<Player, InventoryMoveItemEvent> getOnSlotChangeConsumer() {
        return onSlotChangeConsumer;
    }
}

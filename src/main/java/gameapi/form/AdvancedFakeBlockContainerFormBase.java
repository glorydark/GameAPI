package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.scheduler.Task;
import gameapi.GameAPI;
import gameapi.form.inventory.FakeInventory;
import gameapi.form.response.ChestResponse;
import gameapi.utils.FakeBlockCacheData;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 * <p>
 * Reference:
 * https://github.com/CloudburstMC/FakeInventories/blob/master/src/main/java/com/nukkitx/fakeinventories/inventory/FakeInventory.java
 */
public abstract class AdvancedFakeBlockContainerFormBase extends AdvancedChestFormBase {

    protected String tileId;

    protected int blockId;
    protected BiConsumer<Player, ChestResponse> clickBiConsumer = null;

    protected Consumer<Player> closeConsumer = null;

    protected InventoryType inventoryType;

    protected boolean itemMovable;

    public AdvancedFakeBlockContainerFormBase(String tileId, int blockId, String title, InventoryType inventoryType) {
        this(tileId, blockId, title, inventoryType, false);
    }

    public AdvancedFakeBlockContainerFormBase(String tileId, int blockId, String title, InventoryType inventoryType, boolean itemMovable) {
        super(title);
        this.blockId = blockId;
        this.tileId = tileId;
        this.inventoryType = inventoryType;
        this.itemMovable = itemMovable;
    }

    @Override
    public void showToPlayer(Player player) {
        Position position = getValidPosition(player);

        UpdateBlockPacket pk = new UpdateBlockPacket();
        pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, this.blockId, 0);
        pk.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        pk.x = position.getFloorX();
        pk.y = position.getFloorY();
        pk.z = position.getFloorZ();
        player.dataPacket(pk);

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

        FakeBlockCacheData fakeBlockCacheData = new FakeBlockCacheData(pk.x, pk.y, pk.z, player.getLevel(), position.getLevelBlock());
        this.fakeBlocks.computeIfAbsent(player, player1 -> new ArrayList<>()).add(fakeBlockCacheData);

        FakeInventory fakeInventory = new FakeInventory(this, fakeBlockCacheData, this.getInventoryType());

        Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.plugin, new Task() {
            @Override
            public void onRun(int i) {
                player.addWindow(fakeInventory);
            }
        }, 3);
    }

    @Override
    public void dealResponse(Player player, ChestResponse chestResponse) {
        if (chestResponse == null) {
            Consumer<Player> consumer = this.getCloseConsumer();
            if (consumer != null) {
                consumer.accept(player);
            }
        } else {
            Item item = chestResponse.getItem();
            BiConsumer<Player, ChestResponse> consumer = this.getResponseMap().get(chestResponse.getSlot());
            if (consumer != null) {
                consumer.accept(player, chestResponse);
            }

            consumer = this.getClickBiConsumer();
            if (consumer != null) {
                consumer.accept(player, chestResponse);
            }
        }
    }

    @Override
    public void close(Player player) {
        this.closeProcess(player);
    }

    @Override
    protected void closeProcess(Player player) {
        removeFakeBlock(player);
    }

    protected void removeFakeBlock(Player player) {
        if (fakeBlocks.containsKey(player)) {
            List<FakeBlockCacheData> cacheDataList = this.fakeBlocks.get(player);
            for (FakeBlockCacheData cacheData : cacheDataList) {
                UpdateBlockPacket pk = new UpdateBlockPacket();
                pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, cacheData.getBlock().getId(), cacheData.getBlock().getDamage());
                pk.flags = UpdateBlockPacket.FLAG_ALL;
                pk.x = cacheData.getX();
                pk.y = cacheData.getY();
                pk.z = cacheData.getZ();
                player.dataPacket(pk);
            }
            this.fakeBlocks.remove(player);
        }
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

    public BiConsumer<Player, ChestResponse> getClickBiConsumer() {
        return clickBiConsumer;
    }

    public Consumer<Player> getCloseConsumer() {
        return closeConsumer;
    }

    protected CompoundTag getBlockEntityDataAt(Vector3 position, String title, boolean pair) {
        CompoundTag result = BlockEntity.getDefaultCompound(position, tileId)
                .putString("CustomName", title);
        if (pair) {
            int pairX = - 1;
            result.putInt("pairx", position.getFloorX() + pairX)
                    .putInt("pairz", position.getFloorZ());
        }
        return result;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public boolean isItemMovable() {
        return itemMovable;
    }
}

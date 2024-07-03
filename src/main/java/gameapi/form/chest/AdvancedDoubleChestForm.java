package gameapi.form.chest;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.scheduler.Task;
import gameapi.GameAPI;
import gameapi.form.AdvancedFakeBlockContainerFormBase;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.form.inventory.FakeInventory;
import gameapi.form.response.ChestResponse;
import gameapi.utils.FakeBlockCacheData;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author glorydark
 */
public class AdvancedDoubleChestForm extends AdvancedFakeBlockContainerFormBase {

    public AdvancedDoubleChestForm(String title) {
        this(title, false);
    }

    public AdvancedDoubleChestForm(String title, boolean movable) {
        super(BlockEntity.CHEST, Block.CHEST, title, InventoryType.DOUBLE_CHEST, movable);
    }

    public AdvancedDoubleChestForm onClick(BiConsumer<Player, ChestResponse> consumer) {
        this.clickBiConsumer = consumer;
        return this;
    }

    public AdvancedDoubleChestForm onClose(Consumer<Player> consumer) {
        this.closeConsumer = consumer;
        return this;
    }

    public AdvancedDoubleChestForm item(int slot, ResponsiveElementSlotItem slotItem) {
        Item item = slotItem.getItem();
        this.getInventory().put(slot, item);
        this.getResponseMap().put(slot, slotItem.getResponse());
        return this;
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

        UpdateBlockPacket pk1 = new UpdateBlockPacket();
        pk1.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, this.blockId, 0);
        pk1.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        pk1.x = position.getFloorX() - 1;
        pk1.y = position.getFloorY();
        pk1.z = position.getFloorZ();
        player.dataPacket(pk1);

        BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
        blockEntityDataPacket.x = position.getFloorX();
        blockEntityDataPacket.y = position.getFloorY();
        blockEntityDataPacket.z = position.getFloorZ();
        try {
            blockEntityDataPacket.namedTag = NBTIO.write(this.getBlockEntityDataAt(position, title, true), ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        player.dataPacket(blockEntityDataPacket);

        FakeBlockCacheData fakeBlockCacheData = new FakeBlockCacheData(pk.x, pk.y, pk.z, player.getLevel(), position.getLevelBlock());

        FakeBlockCacheData fakeBlockCacheData1 = new FakeBlockCacheData(pk1.x, pk1.y, pk1.z, player.getLevel(), position.add(-1, 0, 1).getLevelBlock());
        this.fakeBlocks.computeIfAbsent(player, player1 -> new ArrayList<>()).addAll(Arrays.asList(fakeBlockCacheData, fakeBlockCacheData1));

        FakeInventory fakeInventory = new FakeInventory(this, fakeBlockCacheData, this.getInventoryType());

        Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.plugin, new Task() {
            @Override
            public void onRun(int i) {
                player.addWindow(fakeInventory);
            }
        }, 3);
    }
}

package gameapi.form.inventory;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.InventoryType;

/**
 * @author glorydark
 */
public enum BlockFakeInventoryType {

    /**
     * These are interactive ui based on client-side fake block
     */
    CHEST(BlockEntity.CHEST, Block.CHEST, InventoryType.CHEST),
    DOUBLE_CHEST(BlockEntity.CHEST, Block.CHEST, InventoryType.DOUBLE_CHEST),
    ENDER_CHEST(BlockEntity.ENDER_CHEST, Block.ENDER_CHEST, InventoryType.ENDER_CHEST),
    SHULKER_BOX(BlockEntity.SHULKER_BOX, Block.SHULKER_BOX, InventoryType.SHULKER_BOX),
    HOPPER(BlockEntity.HOPPER, Block.HOPPER_BLOCK, InventoryType.HOPPER),
    DROPPER(BlockEntity.DROPPER, Block.DROPPER, InventoryType.DROPPER),
    DISPENSER(BlockEntity.DISPENSER, Block.DISPENSER, InventoryType.DISPENSER),
    BARREL(BlockEntity.BARREL, Block.BARREL, InventoryType.BARREL),
    // non-interactive uis, movable should be set to true
    FURNACE(BlockEntity.FURNACE, Block.FURNACE, InventoryType.FURNACE),
    WORKBENCH("", Block.WORKBENCH, InventoryType.WORKBENCH),
    ANVIL("", Block.ANVIL, InventoryType.ANVIL),
    ENCHANTED_TABLE(BlockEntity.ENCHANT_TABLE, Block.ENCHANTMENT_TABLE, InventoryType.ENCHANT_TABLE),
    BREWING_STAND(BlockEntity.BREWING_STAND, Block.BREWING_STAND_BLOCK, InventoryType.BREWING_STAND);

    final String blockEntityIdentifier;

    final int blockId;

    final InventoryType inventoryType;

    BlockFakeInventoryType(String blockEntityIdentifier, int blockId, InventoryType inventoryType) {
        this.blockEntityIdentifier = blockEntityIdentifier;
        this.blockId = blockId;
        this.inventoryType = inventoryType;
    }

    public String getBlockEntityIdentifier() {
        return blockEntityIdentifier;
    }

    public int getBlockId() {
        return blockId;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }
}

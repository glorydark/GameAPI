package gameapi.tools;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import gameapi.utils.NukkitTypeUtils;

/**
 * @author glorydark
 */
public class BlockTools {

    /**
     * This is a method to set blocks in an area.
     *
     * @param bb    the area you want to remove blocks
     * @param level the level you selected
     * @param block the block you want to replace the old ones
     */
    public static synchronized void setAreaBlocks(AxisAlignedBB bb, Block block, Level level) {
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to remove blocks in an area.
     *
     * @param bb    the area you want to remove blocks
     * @param level the level you selected
     */
    public static synchronized void removeAreaBlocks(AxisAlignedBB bb, Level level) {
        Block block = new BlockAir();
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to destroy blocks in an area.
     *
     * @param bb             the area you want to remove blocks
     * @param level          the level you selected
     * @param particleEffect the variety of particle you want to display
     */
    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, ParticleEffect particleEffect) {
        if (level == null) {
            return;
        }
        Block block = Block.get(0);
        if (particleEffect != null) {
            bb.forEach((i, i1, i2) -> {
                level.setBlock(i, i1, i2, block, false, false);
                level.addParticleEffect(new Location(i, i1, i2, level), particleEffect);
            });
        } else {
            bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
        }
    }

    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, boolean blockBreakEffect) {
        if (level == null) {
            return;
        }
        bb.forEach((i, i1, i2) -> {
            Block originalBlock = level.getBlock(i, i1, i2);
            level.setBlock(i, i1, i2, new BlockAir(), false, false);
            level.addParticle(new DestroyBlockParticle(new Vector3(i, i1, i2), originalBlock));
        });
    }

    public static String getIdentifierWithMeta(Block block) {
        Item item = block.toItem();
        switch (NukkitTypeUtils.getNukkitType()) {
            case POWER_NUKKIT_X:
            case POWER_NUKKIT_X_2:
            case MOT:
                return item.getNamespaceId() + ":" + item.getDamage();
            default:
                return item.getId() + ":" + item.getDamage();
        }
    }

    public static Block getBlockByString(String s) {
        String[] strings = s.split(":");
        Block block;
        if (strings.length == 1) {
            block = Block.get(Integer.parseInt(strings[0]));
        } else if (strings.length == 2) {
            block = Block.get(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
        } else {
            block = Block.get(0);
        }
        return block;
    }
}

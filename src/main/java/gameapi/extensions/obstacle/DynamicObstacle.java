package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import gameapi.utils.BlockInfoAndVecData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public abstract class DynamicObstacle {

    List<Block> blocks = new ArrayList<>();

    Block switchBlock;

    boolean isUsing;

    Level level;

    int startCoolDownTick;

    public DynamicObstacle(int startCoolDownTick, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList, int replaceBlockId, int replaceBlockMeta) {
        for (BlockInfoAndVecData blockInfoAndVecData : blockInfoAndVecDataList) {
            Block block = blockInfoAndVecData.toBlock(level);
            Block clone = Block.get(block.getId(), block.getDamage());
            clone.position(block.getLocation());
            if (!block.getChunk().isLoaded()) {
                try {
                    block.getChunk().load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.blocks.add(clone);
        }
        this.startCoolDownTick = startCoolDownTick;
        this.isUsing = false;
        this.switchBlock = Block.get(replaceBlockId, replaceBlockMeta);
        this.level = level;
    }

    public abstract void onTick();

    public abstract void onTread(Block block);

    public Block getSwitchBlock() {
        return switchBlock;
    }

    public void setSwitchBlock(Block switchBlock) {
        this.switchBlock = switchBlock;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public int getStartCoolDownTick() {
        return startCoolDownTick;
    }

    public void setStartCoolDownTick(int startCoolDownTick) {
        this.startCoolDownTick = startCoolDownTick;
    }
}

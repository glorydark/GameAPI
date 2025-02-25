package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import gameapi.utils.BlockInfoAndVecData;
import lombok.ToString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
@ToString
public abstract class DynamicObstacle {

    private List<Block> blocks = new ArrayList<>();

    private Level level;

    private int startCoolDownTick;

    private boolean enabled;

    public DynamicObstacle(int startCoolDownTick, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList) {
        for (BlockInfoAndVecData blockInfoAndVecData : blockInfoAndVecDataList) {
            if (blockInfoAndVecData.getVector3() != null) {
                Block block = blockInfoAndVecData.toBlock(level);
                if (block != null) {
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
            }
        }
        this.startCoolDownTick = startCoolDownTick;
        this.level = level;
        this.enabled = true;
    }

    public void onTick() {

    }

    public void onTread(Block block) {

    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

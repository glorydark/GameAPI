package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

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

    public DynamicObstacle(Level level, List<Vector3> vectorList, int replaceBlockId, int replaceBlockMeta) {
        for (Vector3 vector3 : vectorList) {
            Block block = level.getBlock(vector3).clone();
            if (!block.getChunk().isLoaded()) {
                try {
                    block.getChunk().load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.blocks.add(block);
        }
        this.isUsing = false;
        this.switchBlock = Block.get(replaceBlockId, replaceBlockMeta);
        this.level = level;
    }

    public abstract void onTick();

    public abstract void onTread(Block block);

    public Block getSwitchBlock() {
        return switchBlock;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void setSwitchBlock(Block switchBlock) {
        this.switchBlock = switchBlock;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}

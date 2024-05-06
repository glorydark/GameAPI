package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class DynamicObstacle {

    List<Block> blocks = new ArrayList<>();

    Block switchBlock;

    boolean isUsing;

    public DynamicObstacle(Level level, List<Vector3> vectorList, int replaceBlockId, int replaceBlockMeta) {
        for (Vector3 vector3 : vectorList) {
            Block block = level.getBlock(vector3).clone();
            this.blocks.add(block);
        }
        this.isUsing = false;
        this.switchBlock = Block.get(replaceBlockId, replaceBlockMeta);
    }

    public void onTick() {

    }

    public void onTread(Block block) {

    }

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
}

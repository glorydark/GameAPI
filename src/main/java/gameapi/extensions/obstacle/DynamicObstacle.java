package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
@Data
public class DynamicObstacle {

    List<Block> blocks = new ArrayList<>();

    Block switchBlocks;

    boolean isUsing;

    public DynamicObstacle(Level level, List<Vector3> vectorList, int replaceBlockId, int replaceBlockMeta) {
        for (Vector3 vector3 : vectorList) {
            Block block = level.getBlock(vector3).clone();
            this.blocks.add(block);
        }
        this.isUsing = false;
        this.switchBlocks = Block.get(replaceBlockId, replaceBlockMeta);
    }

    public void onTick() {

    }

    public void onTread(Block block) {

    }
}

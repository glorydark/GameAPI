package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.List;

/**
 * @author glorydark
 */
public class ObstacleIntermittentVanish extends DynamicObstacle {

    public int intervalTicks;

    public int currentTicks = 0;

    public ObstacleIntermittentVanish(int intervalTicks, Level level, List<Vector3> vectorList, int replaceBlockId, int replaceBlockMeta) {
        super(level, vectorList, replaceBlockId, replaceBlockMeta);
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void onTick() {
        currentTicks++;
        if (currentTicks >= intervalTicks) {
            currentTicks = 0;
            for (Block block : this.getBlocks()) {
                Block current = this.getLevel().getBlock(block);
                if (current.getId() == block.getId() && current.getDamage() == block.getDamage()) {
                    this.getLevel().setBlock(block, this.getSwitchBlock(), true, true);
                } else {
                    this.getLevel().setBlock(block, block, true, true);
                }
            }
        }
    }

    @Override
    public void onTread(Block block) {

    }
}

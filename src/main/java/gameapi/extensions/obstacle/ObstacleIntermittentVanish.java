package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;

import java.util.List;

/**
 * @author glorydark
 */
public class ObstacleIntermittentVanish extends DynamicObstacle {

    public int intervalTicks;

    public int currentTicks = 0;

    public ObstacleIntermittentVanish(int intervalTicks, int startCoolDownTick, Level level, List<Vector3> vectorList, int replaceBlockId, int replaceBlockMeta) {
        super(startCoolDownTick, level, vectorList, replaceBlockId, replaceBlockMeta);
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void onTick() {
        if (startCoolDownTick > 0) {
            startCoolDownTick -= GameAPI.GAME_TASK_INTERVAL;
            return;
        }
        currentTicks+=GameAPI.GAME_TASK_INTERVAL;
        if (currentTicks >= intervalTicks) {
            for (Block block : this.getBlocks()) {
                Block current = this.getLevel().getBlock(block);
                if (current.getId() == block.getId() && current.getDamage() == block.getDamage()) {
                    this.getLevel().setBlock(block, this.getSwitchBlock(), true, true);
                } else {
                    this.getLevel().setBlock(block, block, true, true);
                }
            }
            currentTicks = 0;
        }
    }

    @Override
    public void onTread(Block block) {

    }
}

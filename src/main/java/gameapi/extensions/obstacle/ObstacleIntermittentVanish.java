package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.extensions.obstacle.abstracts.AbstractDynamicObstacleBlockSwitchable;
import gameapi.utils.BlockInfoAndVecData;

import java.util.List;

/**
 * @author glorydark
 */
public class ObstacleIntermittentVanish extends AbstractDynamicObstacleBlockSwitchable {

    public int intervalTicks;

    public int currentTicks = 0;

    public ObstacleIntermittentVanish(int intervalTicks, int startCoolDownTick, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList) {
        super(startCoolDownTick, level, blockInfoAndVecDataList);
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void onTick() {
        if (this.getStartCoolDownTick() > 0) {
            this.setStartCoolDownTick(this.getStartCoolDownTick() - GameAPI.GAME_TASK_INTERVAL);
            return;
        }
        this.currentTicks += GameAPI.GAME_TASK_INTERVAL;
        if (this.currentTicks >= this.intervalTicks) {
            for (Block block : this.getBlocks()) {
                Block current = this.getLevel().getBlock(block);
                if (current.getId() == block.getId() && current.getDamage() == block.getDamage()) {
                    this.getLevel().setBlock(block, this.getCurrentSwitchBlock(), true, true);
                } else {
                    this.getLevel().setBlock(block, block, true, true);
                }
            }
            this.currentTicks = 0;
        }
    }
}

package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.extensions.obstacle.abstracts.AbstractDynamicObstacleBlockSwitchable;
import gameapi.utils.BlockInfoAndVecData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class ObstacleDynamicSectionWorm extends AbstractDynamicObstacleBlockSwitchable {

    public int intervalTicks;

    public int currentTicks = 0;

    public int nodeIndex = 0;

    public int sectionCount;

    public ObstacleDynamicSectionWorm(int intervalTicks, int startCoolDownTick, int sectionCount, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList) {
        super(startCoolDownTick, level, blockInfoAndVecDataList);
        this.intervalTicks = intervalTicks;
        this.sectionCount = sectionCount;
    }

    @Override
    public void onTick() {
        if (this.getStartCoolDownTick() > 0) {
            this.setStartCoolDownTick(this.getStartCoolDownTick() - GameAPI.GAME_TASK_INTERVAL);
            return;
        }
        this.currentTicks += GameAPI.GAME_TASK_INTERVAL;
        if (this.currentTicks >= this.intervalTicks) {
            List<Integer> showIndexList = new ArrayList<>();
            for (int i = nodeIndex; i <= this.nodeIndex + this.sectionCount; i++) {
                int realIndex = i;
                if (realIndex >= this.getBlocks().size()) {
                    realIndex -= this.getBlocks().size();
                }
                showIndexList.add(realIndex);
            }
            for (int i = 0; i < this.getBlocks().size(); i++) {
                Block block = this.getBlocks().get(i);
                if (showIndexList.contains(i)) {
                    this.getLevel().setBlock(block, block, true, true);
                } else {
                    this.getLevel().setBlock(block, this.getCurrentSwitchBlock(), true, true);
                }
            }
            this.nodeIndex += 1;
            if (this.nodeIndex >= this.getBlocks().size()) {
                this.nodeIndex -= this.getBlocks().size();
            }
            this.currentTicks = 0;
        }
    }
}
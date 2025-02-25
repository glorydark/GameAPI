package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import gameapi.GameAPI;
import gameapi.utils.BlockInfoAndVecData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class ObstacleDynamicSectionWormBackAndForth extends ObstacleDynamicSectionWorm {

    public ObstacleDynamicSectionWormBackAndForth(int intervalTicks, int startCoolDownTick, int sectionCount, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList) {
        super(intervalTicks, startCoolDownTick, sectionCount, level, blockInfoAndVecDataList);
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
            for (int i = this.nodeIndex; i <= this.nodeIndex + this.sectionCount; i++) {
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
            this.nodeIndex += this.sectionCount; // 与父类不同的逻辑
            if (this.nodeIndex >= this.getBlocks().size()) {
                this.nodeIndex -= this.getBlocks().size();
            }
            this.currentTicks = 0;
        }
    }
}
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
public class ObstacleDynamicSection extends DynamicObstacle {

    public int intervalTicks;

    public int currentTicks = 0;

    public int nodeIndex = 0;

    public int sectionCount;

    public ObstacleDynamicSection(int intervalTicks, int startCoolDownTick, int sectionCount, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList) {
        super(startCoolDownTick, level, blockInfoAndVecDataList, 0, 0);
        this.intervalTicks = intervalTicks;
        this.sectionCount = sectionCount;
    }

    @Override
    public void onTick() {
        if (this.startCoolDownTick > 0) {
            this.startCoolDownTick -= GameAPI.GAME_TASK_INTERVAL;
            return;
        }
        this.currentTicks += GameAPI.GAME_TASK_INTERVAL;
        if (this.currentTicks >= this.intervalTicks) {
            List<Integer> showIndexList = new ArrayList<>();
            for (int i = nodeIndex; i <= nodeIndex + sectionCount; i++) {
                int realIndex = i;
                if (realIndex >= this.blocks.size()) {
                    realIndex -= this.blocks.size();
                }
                showIndexList.add(realIndex);
            }
            for (int i = 0; i < this.getBlocks().size(); i++) {
                Block block = this.blocks.get(i);
                if (showIndexList.contains(i)) {
                    this.level.setBlock(block, block, true, true);
                } else {
                    this.level.setBlock(block, this.getSwitchBlock(), true, true);
                }
            }
            this.nodeIndex += 1;
            if (this.nodeIndex >= this.blocks.size()) {
                this.nodeIndex -= this.blocks.size();
            }
            this.currentTicks = 0;
        }
    }

    @Override
    public void onTread(Block block) {

    }
}

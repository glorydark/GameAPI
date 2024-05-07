package gameapi.extensions.obstacle;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;

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

    public ObstacleDynamicSection(int intervalTicks, int startCoolDownTick, int sectionCount, Level level, List<Vector3> vectorList) {
        super(startCoolDownTick, level, vectorList, 0, 0);
        this.intervalTicks = intervalTicks;
        this.sectionCount = sectionCount;
    }

    @Override
    public void onTick() {
        if (startCoolDownTick > 0) {
            startCoolDownTick -= GameAPI.GAME_TASK_INTERVAL;
            return;
        }
        currentTicks += GameAPI.GAME_TASK_INTERVAL;
        if (currentTicks >= intervalTicks) {
            List<Integer> showIndexList = new ArrayList<>();
            for (int i = nodeIndex; i <= nodeIndex + sectionCount; i++) {
                int realIndex = i;
                if (realIndex >= blocks.size()) {
                    realIndex -= blocks.size();
                }
                showIndexList.add(realIndex);
            }
            for (int i = 0; i < this.getBlocks().size(); i++) {
                Block block = blocks.get(i);
                if (showIndexList.contains(i)) {
                    this.level.setBlock(block, block, true, true);
                } else {
                    this.level.setBlock(block, new BlockAir(), true, true);
                }
            }
            this.nodeIndex += 1;
            if (this.nodeIndex >= blocks.size()) {
                this.nodeIndex -= blocks.size();
            }
            currentTicks = 0;
        }
    }

    @Override
    public void onTread(Block block) {

    }
}

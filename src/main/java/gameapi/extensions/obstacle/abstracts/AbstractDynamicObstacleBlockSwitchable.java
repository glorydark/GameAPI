package gameapi.extensions.obstacle.abstracts;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import gameapi.extensions.obstacle.DynamicObstacle;
import gameapi.extensions.obstacle.type.DynamicObstacleBlockSwitchable;
import gameapi.utils.BlockInfoAndVecData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 *
 * This aims at realising multi-state block-changing obstacles
 */
public abstract class AbstractDynamicObstacleBlockSwitchable extends DynamicObstacle implements DynamicObstacleBlockSwitchable {

    private List<Block> switchBlock = new ArrayList<>();

    private int blockChangeStateIndex = 0;

    public AbstractDynamicObstacleBlockSwitchable(int startCoolDownTick, Level level, List<BlockInfoAndVecData> blockInfoAndVecDataList) {
        super(startCoolDownTick, level, blockInfoAndVecDataList);
    }

    @Override
    public Block getCurrentSwitchBlock() {
        if (this.switchBlock.isEmpty()) {
            return Block.get(0); // todo: temp default set to air
        }
        if (this.switchBlock.size() == 1 || this.blockChangeStateIndex + 1 >= switchBlock.size()) {
            this.blockChangeStateIndex = 0;
        } else {
            this.blockChangeStateIndex++;
        }
        return switchBlock.get(this.blockChangeStateIndex);
    }

    @Override
    public List<Block> getSwitchBlock() {
        return switchBlock;
    }

    @Override
    public void setSwitchBlock(List<Block> switchBlock) {
        this.switchBlock = switchBlock;
    }

    @Override
    public void addSwitchBlock(Block switchBlock) {
        this.switchBlock.add(switchBlock);
    }

    @Override
    public int getBlockChangeStateIndex() {
        return this.blockChangeStateIndex;
    }

    @Override
    public void setBlockChangeStateIndex(int i) {
        this.blockChangeStateIndex = i;
    }
}

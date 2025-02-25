package gameapi.extensions.obstacle.type;

import cn.nukkit.block.Block;

import java.util.List;

/**
 * @author glorydark
 */
public interface DynamicObstacleBlockSwitchable {

    default Block getCurrentSwitchBlock() {
        if (this.getSwitchBlock().isEmpty()) {
            return Block.get(0); // todo: temp default set to air
        }
        if (this.getSwitchBlock().size() == 1 || this.getBlockChangeStateIndex() + 1 >= this.getSwitchBlock().size()) {
            this.setBlockChangeStateIndex(0);
        } else {
            this.setBlockChangeStateIndex(this.getBlockChangeStateIndex() + 1);
        }
        return this.getSwitchBlock().get(this.getBlockChangeStateIndex());
    }

    List<Block> getSwitchBlock();

    void addSwitchBlock(Block switchBlock);

    void setSwitchBlock(List<Block> switchBlock);

    int getBlockChangeStateIndex();

    void setBlockChangeStateIndex(int i);
}

package gameapi.commands.data.entry;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import lombok.Builder;
import lombok.ToString;

/**
 * @author glorydark
 */
@Builder
@ToString
public class SimpleOperationEntry implements OperationEntry {

    private int beforeBlockId;

    private int beforeBlockMeta;

    private Level level;

    private int floorX;

    private int floorY;

    private int floorZ;

    public void sudo() {
        if (level != null) {
            level.setBlock(new Vector3(floorX, floorY, floorZ), Block.get(beforeBlockId, beforeBlockMeta));
        }
    }
}

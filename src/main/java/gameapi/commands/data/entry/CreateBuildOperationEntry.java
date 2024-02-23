package gameapi.commands.data.entry;

import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import lombok.Builder;
import lombok.ToString;

/**
 * @author glorydark
 */
@Builder
@ToString
public class CreateBuildOperationEntry implements OperationEntry {

    private SimpleAxisAlignedBB area;

    private Level level;

    public void sudo() {
        area.forEach((i, i1, i2) -> level.setBlock(new Vector3(i, i1, i2), new BlockAir()));
    }
}

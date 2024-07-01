package gameapi.utils;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class FakeBlockCacheData {

    int x;

    int y;

    int z;

    Level level;

    Block block;
}

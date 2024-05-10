package gameapi.utils;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import gameapi.tools.SpatialTools;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class BlockInfoAndVecData {

    int id;

    int damage;

    Vector3 vector3;

    public BlockInfoAndVecData(String blockData) { // id:meta:x:y:z
        String[] splits = blockData.split(":");
        if (splits.length == 5) {
            Vector3 vector3 = SpatialTools.parseVectorFromString(blockData.replace(splits[0] + ":" + splits[1], ""));
            this.id = Integer.parseInt(splits[0]);
            this.damage = Integer.parseInt(splits[1]);
            this.vector3 = vector3;
        }
    }

    public BlockInfoAndVecData(int id, int damage, Location vector3) {
        this.id = id;
        this.damage = damage;
        this.vector3 = vector3;
    }

    public Block toBlock() {
        if (this.vector3 == null) {
            return null;
        } else {
            Block block = Block.get(id, damage);
            block.setComponents(vector3);
            return block;
        }
    }

    public Block toBlock(Level level) {
        if (this.vector3 == null) {
            return null;
        } else {
            Block block = Block.get(id, damage);
            block.setComponents(vector3);
            block.setLevel(level);
            return block;
        }
    }
}

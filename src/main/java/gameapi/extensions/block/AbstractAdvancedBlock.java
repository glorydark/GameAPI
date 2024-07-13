package gameapi.extensions.block;

import gameapi.event.block.RoomBlockEvent;
import gameapi.event.player.RoomPlayerInteractEvent;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
public class AbstractAdvancedBlock {

    private int blockId;

    private int blockMeta;

    public AbstractAdvancedBlock(int blockId, int blockMeta) {
        this.blockId = blockId;
        this.blockMeta = blockMeta;
    }

    public void onRespond(RoomBlockEvent event) {

    }

    public void onRespond(RoomPlayerInteractEvent event) {

    }
}

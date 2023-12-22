package gameapi.event.block;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import gameapi.event.Cancellable;
import gameapi.room.Room;

public class RoomBlockBreakEvent extends RoomBlockEvent implements Cancellable {

    protected Item item;

    protected Player player;

    protected BlockFace face;

    protected boolean instaBreak;

    protected Item[] blockDrops;

    protected int blockXP;

    public RoomBlockBreakEvent(Room room, Block block, Player player, Item item, boolean instaBreak, Item[] blockDrops, int blockXP, BlockFace face) {
        this.room = room;
        this.player = player;
        this.block = block;
        this.item = item;
        this.instaBreak = instaBreak;
        this.blockDrops = blockDrops;
        this.blockXP = blockXP;
        this.face = face;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public Block getBlock() {
        return super.getBlock();
    }

    public BlockFace getFace() {
        return face;
    }

    public boolean isInstaBreak() {
        return instaBreak;
    }

    public void setInstaBreak(boolean instaBreak) {
        this.instaBreak = instaBreak;
    }

    public int getDropExp() {
        return blockXP;
    }

    public void setDropExp(int blockXP) {
        this.blockXP = blockXP;
    }

    public Item[] getDrops() {
        return blockDrops;
    }

    public void setDrops(Item[] blockDrops) {
        this.blockDrops = blockDrops;
    }
}
package gameapi.extensions.supplyChest;

import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.level.Location;
import cn.nukkit.math.NukkitRandom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SupplyChest {

    Location location;

    List<SupplyItem> supplyItemList;

    long lastMillis;

    int maxItemCount;

    NukkitRandom random = new NukkitRandom();

    long intervalTicks; // If this equal to zero, it would not regenerate again.

    public boolean isCoolDownEnd(){
        if(intervalTicks > 0) {
            return this.getCoolDown() >= intervalTicks;
        }else{
            return false;
        }
    }

    public long getCoolDown(){
        return System.currentTimeMillis() - lastMillis;
    }

    public boolean onTickSupply() {
        // If it is not allowed to refresh, it will no longer refresh the items in the chest
        if(!this.isRefreshable()){
            return false;
        }
        // If the subtraction is less than intervalTicks, it will not refresh the supplies in the chest
        if(this.getCoolDown() < intervalTicks){
            return false;
        }
        // Update the latestMillis
        lastMillis = System.currentTimeMillis();
        BlockEntityChest entityChest = this.getEntity();
        if(entityChest != null) {
            entityChest.getInventory().clearAll();
            // Try to get the refreshments
            int count = 0;
            for(SupplyItem supplyItem: supplyItemList){
                if(count >= maxItemCount){
                    return true;
                }
                if(this.processFakeRandom(supplyItem.possibility)){
                    entityChest.getInventory().addItem(supplyItem.getItem());
                    count++;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isRefreshable() {
        return intervalTicks!=0;
    }

    public BlockEntityChest getEntity(){
        Block block = location.getLevel().getBlock(location.getLocation());
        if(block.getId() == 54) {
            return (BlockEntityChest) location.getLevel().getBlockEntity(location.getLocation());
        }
        return null;
    }

    /**
     * This is a method to get a random choice about whether it hits a winning streak or it is on a losing one.
     */
    public boolean processFakeRandom(int possibilities){
        Set<Integer> originIntegerSet = new HashSet<>();
        for(int i=1; i<=100; i++){
            originIntegerSet.add(i);
        }

        for(int i=1; i<=Math.min(possibilities, 100); i++){
            if(originIntegerSet.contains(random.nextRange(1, 100))){
                return true;
            }
        }
        return false;
    }

}

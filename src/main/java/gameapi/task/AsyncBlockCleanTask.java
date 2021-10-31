package gameapi.task;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockGrass;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.AsyncTask;
import gameapi.MainClass;
import gameapi.room.Room;

import java.util.LinkedList;
import java.util.Map;

public class AsyncBlockCleanTask extends AsyncTask {

    public Room room;

    public AsyncBlockCleanTask(Room room){
        this.room = room;
    }

    @Override
    public void onRun() {
        Level level = room.getStartSpawn().getLevel();
        if(level != null){
            long handleStart = System.currentTimeMillis();
            for (Block block : room.getBreakBlocks()) {
                if(block != null) {
                    level.setBlock(block.getLocation(), block);
                }
            }
            for (Block block : room.getPlaceBlocks()) {
                if(block != null) {
                    level.setBlock(block.getLocation(), new BlockAir());
                }
            }
            long handleEnd = System.currentTimeMillis();
            float second = (handleEnd - handleStart) / 1000;
            room.setBreakBlocks(new LinkedList<>());
            room.setPlaceBlocks(new LinkedList<>());
            MainClass.plugin.getLogger().info("总用时:"+second+"秒");
        }
    }
}

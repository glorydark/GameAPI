package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import gameapi.GameAPI;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author glorydark
 */
public class SchematicConverter {

    public static void createBuildFromSchematic(Player player, String fileName) {
        File file = new File(GameAPI.getPath() + File.separator + "schematics" + File.separator + fileName + ".schematic");
        if (!file.exists()) {
            player.sendMessage("File not found: " + file);
        }
        try {
            NBTInputStream inputStream = new NBTInputStream(Files.newInputStream(file.toPath()));
            Tag tag = inputStream.readTag();
            if (tag instanceof CompoundTag) {
                CompoundTag compoundTag = (CompoundTag) tag;
                // System.out.println(compoundTag.getValue().keySet());
                short width = (short) compoundTag.getValue().get("Width").getValue();
                short height = (short) compoundTag.getValue().get("Height").getValue();
                if (player.getFloorY() + height > player.getLevel().getMaxBlockY()) {
                    player.sendMessage("Height out of bound, maxY: " + height);
                    return;
                }
                short length = (short) compoundTag.getValue().get("Length").getValue();
                // int offsetX = (int) compoundTag.getValue().get("WEOffsetX").getValue();
                // int offsetY = (int) compoundTag.getValue().get("WEOffsetY").getValue();
                // int offsetZ = (int) compoundTag.getValue().get("WEOffsetZ").getValue();
                AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("Tips",
                        "您正在生成大小为" + length + ":" + width + ":" + height + "的建筑，是否继续？");
                simple.addButton(new ResponsiveElementButton("Start")
                        .onRespond(player1 ->
                                startGen(player1, (byte[]) compoundTag.getValue().get("Blocks").getValue(),
                                        (byte[]) compoundTag.getValue().get("Data").getValue(),
                                        width, height, length)
                        )
                );
                simple.addButton(new ElementButton("Cancel"));
                simple.showToPlayer(player);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startGen(Player player, byte[] schematicBlocks, byte[] schematicBlockData, int width, int height, int length) {
        List<BlockData> blockDataList = new ArrayList<>();
        List<BlockDataWithPos> blockDataPosList = new ArrayList<>();
        long startMillis = System.currentTimeMillis();
        AtomicLong lastStepStartMillis = new AtomicLong(startMillis);
        CompletableFuture.runAsync(() -> {
            for (int i = 0; i < schematicBlocks.length; i++) {
                int blockId = schematicBlocks[i] & 0xff;
                int data = schematicBlockData[i] & 0xff;
                blockDataList.add(new BlockData(blockId, data));
            }
            GameAPI.getInstance().getLogger().info("Finished loading " + blockDataPosList.size() + " blocks' ids and data! Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), lastStepStartMillis.get()));
        }).thenRun(() -> {
            {
                lastStepStartMillis.set(System.currentTimeMillis());
                for (int z = 0; z < width; z++) {
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < length; x++) {
                            int position = (y * length + z) * width + x;
                            if (position >= blockDataList.size()) {
                                continue;
                            }
                            BlockData current = blockDataList.get(position);
                            blockDataPosList.add(new BlockDataWithPos(current.id, current.data, x, y, z));
                        }
                    }
                }
                GameAPI.getInstance().getLogger().info("Finished parsing " + blockDataPosList.size() + " blocks' positions in the schematic! Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), lastStepStartMillis.get()));
            }
        }).thenRun(() -> createStructure(player, blockDataPosList));
    }

    public static void createStructure(Player player, List<BlockDataWithPos> blockDataWithPos) {
        Level level = player.getLevel();
        AtomicInteger finishedCount = new AtomicInteger();
        int maxCount = blockDataWithPos.size();
        int pieceSize = maxCount / 20;
        AtomicInteger percentage = new AtomicInteger();
        long startMillis = System.currentTimeMillis();
        player.sendMessage("Start generating schematics...");
        CompletableFuture.runAsync(() -> {
            for (BlockDataWithPos data : blockDataWithPos) {
                if (finishedCount.get() % pieceSize == 0) {
                    percentage.addAndGet(5);
                    GameAPI.getInstance().getLogger().info("Generating blocks [" + finishedCount.get() + "/" + maxCount + "] " + percentage + "% [Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillis) + "]");
                }
                if (data.id != 0) {
                    finishedCount.addAndGet(1);
                    level.setBlock(new Vector3(data.x + player.getFloorX(), data.y + player.getFloorY(), data.z + player.getFloorZ()), Block.get(data.id, data.data), true, true);
                }
            }
        }).thenRun(() -> GameAPI.getInstance().getLogger().info("Finished generating all " + maxCount + " blocks! [Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), startMillis) + "]"));
    }

    @Data
    @AllArgsConstructor
    public static class BlockData {
        private final int id;
        private final int data;
    }

    @Data
    @AllArgsConstructor
    public static class BlockDataWithPos {
        private final int id;
        private final int data;
        private final int x;
        private final int y;
        private final int z;
    }
}
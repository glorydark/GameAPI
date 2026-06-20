package gameapi.tools;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Config;
import gameapi.GameAPI;
import gameapi.utils.BuildBounds;
import gameapi.utils.RotationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class StructureSplicer {

    @Data
    @AllArgsConstructor
    public static class BuildPiece {
        String folderName;
        Vector3 worldOrigin;
        int rotation;
        RotationType rotationType;
    }

    public static CompoundTag loadExtraData(String folderName) {
        return WorldEditTools.loadBuildExtraData(folderName);
    }

    public static CompoundTag loadExtraData(File folder) {
        return WorldEditTools.loadBuildExtraData(folder);
    }

    /**
     * 将 extra 中指定 ListTag 字段从相对坐标转为绝对坐标
     * 每个分量 + worldOrigin
     */
    public static CompoundTag absolutizeExtraTag(CompoundTag extra, Vector3 worldOrigin, String... listKeys) {
        if (extra == null) return null;
        CompoundTag result = extra.clone();
        int ox = worldOrigin.getFloorX();
        int oy = worldOrigin.getFloorY();
        int oz = worldOrigin.getFloorZ();
        for (String key : listKeys) {
            if (result.contains(key)) {
                ListTag<IntTag> list = result.getList(key, IntTag.class);
                if (list.size() >= 3) {
                    ListTag<IntTag> newList = new ListTag<>(key);
                    newList.add(new IntTag("", list.get(0).getData() + ox));
                    newList.add(new IntTag("", list.get(1).getData() + oy));
                    newList.add(new IntTag("", list.get(2).getData() + oz));
                    result.put(key, newList);
                }
            }
        }
        return result;
    }

    /**
     * 将 extra 中 x/y/z 三个 int 字段从相对坐标转为绝对坐标
     */
    public static CompoundTag absolutizeExtraTag(CompoundTag extra, Vector3 worldOrigin, String xKey, String yKey, String zKey) {
        if (extra == null) return null;
        CompoundTag result = extra.clone();
        if (result.contains(xKey) && result.contains(yKey) && result.contains(zKey)) {
            int ox = worldOrigin.getFloorX();
            int oy = worldOrigin.getFloorY();
            int oz = worldOrigin.getFloorZ();
            result.putInt(xKey, result.getInt(xKey) + ox);
            result.putInt(yKey, result.getInt(yKey) + oy);
            result.putInt(zKey, result.getInt(zKey) + oz);
        }
        return result;
    }

    public static void placeBuild(Level level, BuildPiece piece) {
        placeBuild(level, piece, new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + piece.folderName + File.separator));
    }

    public static void placeBuild(Level level, BuildPiece piece, File folder) {
        File jsonFile = new File(folder, "build.json");
        if (!jsonFile.exists()) return;

        Config config = new Config(jsonFile, Config.JSON);
        List<Integer> rMax = config.getIntegerList("relativeMax");
        BuildBounds buildBounds = new BuildBounds(rMax.get(0), rMax.get(1), rMax.get(2));

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".nbt") && !name.equals("extra.nbt"));
        if (files == null) return;

        Vector3 origin = piece.worldOrigin;
        int rot = piece.rotation;
        RotationType rotType = piece.rotationType;

        for (File file : files) {
            CompoundTag tag;
            try {
                tag = NBTIO.read(file);
            } catch (IOException e) {
                GameAPI.getGameDebugManager().printError(e);
                continue;
            }
            for (CompoundTag blockTag : tag.getList("blocks", CompoundTag.class).getAll()) {
                int rx = blockTag.getInt("x");
                int ry = blockTag.getInt("y");
                int rz = blockTag.getInt("z");

                Vector3 rotated = switch (rotType) {
                    case AROUND_CENTER ->
                            buildBounds.getBlockPosAfterHorizontalRotatedByCenter(origin, rx, ry, rz, rot);
                    case AROUND_START_POSITION ->
                            rotateAroundPoint(origin, rx, ry, rz, rot);
                    default ->
                            new Vector3(origin.getFloorX() + rx, origin.getFloorY() + ry, origin.getFloorZ() + rz);
                };

                int x = rotated.getFloorX();
                int y = rotated.getFloorY();
                int z = rotated.getFloorZ();

                if (y > level.getMaxBlockY() || y < level.getMinBlockY()) continue;

                int blockId = blockTag.getInt("blockId");
                int damage = blockTag.getInt("damage");

                if (blockId == BlockID.AIR) continue;

                Block block = Block.get(blockId, damage);
                if (block == null) block = new BlockUnknown(blockId, damage);

                level.setBlock(new Vector3(x, y, z), block, true, false);

                if (blockTag.contains("blockEntityData")) {
                    CompoundTag beTag = blockTag.getCompound("blockEntityData");
                    Block placed = level.getBlock(new Vector3(x, y, z));
                    if (placed instanceof BlockEntityHolder holder) {
                        BlockEntity old = placed.getLevelBlockEntity();
                        if (old != null) old.close();
                        BlockEntity be = holder.createBlockEntity(beTag);
                        if (be instanceof BlockEntitySpawnable spawnable) {
                            spawnable.spawnToAll();
                        }
                    }
                }

                if (blockTag.contains("layer1")) {
                    CompoundTag layer1 = blockTag.getCompound("layer1");
                    int l1Id = layer1.getInt("blockId");
                    int l1Dam = layer1.getInt("damage");
                    if (l1Id != 0) {
                        level.setBlock(new Vector3(x, y, z), 1, Block.get(l1Id, l1Dam), true, false);
                    }
                }
            }
        }
    }

    public static void splice(Level level, List<BuildPiece> pieces) {
        splice(level, pieces, null, (BiConsumer<String, CompoundTag>) null);
    }

    public static void splice(Level level, List<BuildPiece> pieces, Consumer<BuildPiece> beforePlace) {
        splice(level, pieces, beforePlace, (BiConsumer<String, CompoundTag>) null);
    }

    public static void splice(Level level, List<BuildPiece> pieces,
                              Consumer<BuildPiece> beforePlace,
                              BiConsumer<String, CompoundTag> afterPiece) {
        for (BuildPiece piece : pieces) {
            if (beforePlace != null) beforePlace.accept(piece);
            placeBuild(level, piece);
            if (afterPiece != null) {
                CompoundTag extra = loadExtraData(piece.folderName);
                afterPiece.accept(piece.folderName, extra);
            }
        }
    }

    public static <T> void splice(Level level, List<BuildPiece> pieces,
                                  Function<String, T> dataLoader,
                                  BiConsumer<T, BuildPiece> postProcessor) {
        for (BuildPiece piece : pieces) {
            placeBuild(level, piece);
            if (dataLoader != null && postProcessor != null) {
                T data = dataLoader.apply(piece.folderName);
                postProcessor.accept(data, piece);
            }
        }
    }

    public static <T> void splice(Level level, List<T> configs,
                                  Function<T, BuildPiece> pieceFactory,
                                  Consumer<BuildPiece> beforePlace,
                                  BiConsumer<T, BuildPiece> afterPlace) {
        for (T config : configs) {
            BuildPiece piece = pieceFactory.apply(config);
            if (beforePlace != null) beforePlace.accept(piece);
            placeBuild(level, piece);
            if (afterPlace != null) afterPlace.accept(config, piece);
        }
    }

    private static Vector3 rotateAroundPoint(Vector3 startPos, int rx, int ry, int rz, int rotationDegree) {
        double x = startPos.getX() + rx;
        double z = startPos.getZ() + rz;
        double dx = x - startPos.getX();
        double dz = z - startPos.getZ();
        double rad = Math.toRadians(rotationDegree);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vector3(
                startPos.getX() + dx * cos - dz * sin,
                startPos.getY() + ry,
                startPos.getZ() + dx * sin + dz * cos
        );
    }
}

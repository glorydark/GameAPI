package gameapi.tools;

import cn.nukkit.GameVersion;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockStairs;
import cn.nukkit.block.BlockThin;
import cn.nukkit.block.BlockTripWire;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import gameapi.utils.NukkitTypeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author glorydark
 */
public class BlockTools {

    /**
     * This is a method to set blocks in an area.
     *
     * @param bb    the area you want to remove blocks
     * @param level the level you selected
     * @param block the block you want to replace the old ones
     */
    public static synchronized void setAreaBlocks(AxisAlignedBB bb, Block block, Level level) {
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to remove blocks in an area.
     *
     * @param bb    the area you want to remove blocks
     * @param level the level you selected
     */
    public static synchronized void removeAreaBlocks(AxisAlignedBB bb, Level level) {
        Block block = new BlockAir();
        bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
    }

    /**
     * This is a method to destroy blocks in an area.
     *
     * @param bb             the area you want to remove blocks
     * @param level          the level you selected
     * @param particleEffect the variety of particle you want to display
     */
    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, ParticleEffect particleEffect) {
        if (level == null) {
            return;
        }
        Block block = Block.get(0);
        if (particleEffect != null) {
            bb.forEach((i, i1, i2) -> {
                level.setBlock(i, i1, i2, block, false, false);
                level.addParticleEffect(new Location(i, i1, i2, level), particleEffect);
            });
        } else {
            bb.forEach((i, i1, i2) -> level.setBlock(i, i1, i2, block, false, false));
        }
    }

    public static synchronized void destroyAreaBlocks(AxisAlignedBB bb, Level level, boolean blockBreakEffect) {
        if (level == null) {
            return;
        }
        bb.forEach((i, i1, i2) -> {
            Block originalBlock = level.getBlock(i, i1, i2);
            level.setBlock(i, i1, i2, new BlockAir(), false, false);
            if (blockBreakEffect) {
                level.addParticle(new DestroyBlockParticle(new Vector3(i, i1, i2), originalBlock));
            }
        });
    }

    public static String getIdentifierWithMeta(Block block) {
        Item item = block.toItem();
        switch (NukkitTypeUtils.getNukkitType()) {
            case POWER_NUKKIT_X:
            case POWER_NUKKIT_X_2:
            case MOT:
                return item.getNamespaceId() + ":" + item.getDamage();
            default:
                return item.getId() + ":" + item.getDamage();
        }
    }

    public static Block getBlockByString(String s) {
        String[] strings = s.split(":");
        Block block;
        if (strings.length == 1) {
            block = Block.get(Integer.parseInt(strings[0]));
        } else if (strings.length == 2) {
            block = Block.get(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]));
        } else {
            block = Block.get(0);
        }
        return block;
    }

    public static Block getBlockfromString(String string) {
        return Item.fromString(string).getBlock();
    }

    public static void placeBlock(Block block, Vector3 vector3, Level level) {
        block.setComponents(vector3);
        block.setLevel(level);
        block.place(null, block, block, BlockFace.DOWN, 0, 0, 0, null);
    }

    /**
     * 读取当前服务端协议号（MOT 的 {@code GameVersion.getFeatureVersion().getProtocol()}）。
     * 用反射避免在非 MOT 端加载失败，读取失败时返回 0。
     */
    public static int getServerProtocol() {
        try {
            return GameVersion.getFeatureVersion().getProtocol();
        } catch (Throwable t) {
            return 0;
        }
    }

    /**
     * 是否为受 1.26.50(v2193)+ state-driven connections 影响的方块。
     */
    public static boolean isConnectionDriven(Block block) {
        return block instanceof BlockThin || block instanceof BlockFence
                || block instanceof BlockStairs || block instanceof BlockTripWire;
    }

    /**
     * 按邻居重算单个方块的连接/角落位（写回其 damage），返回是否发生变化。
     */
    public static boolean updateConnection(Block block) {
        if (block instanceof BlockThin) {
            return ((BlockThin) block).updateConnections();
        }
        if (block instanceof BlockFence) {
            return ((BlockFence) block).updateConnections();
        }
        if (block instanceof BlockStairs) {
            return ((BlockStairs) block).updateCorner();
        }
        if (block instanceof BlockTripWire) {
            return ((BlockTripWire) block).updateConnections();
        }
        return false;
    }

    private static volatile Set<Integer> connectionDrivenBlockIds;

    /**
     * 由 {@link Block#list} 构建受连接状态影响的方块 id 集合（延迟初始化）。
     */
    public static Set<Integer> connectionDrivenBlockIds() {
        Set<Integer> ids = connectionDrivenBlockIds;
        if (ids != null) {
            return ids;
        }
        synchronized (BlockTools.class) {
            if (connectionDrivenBlockIds == null) {
                Set<Integer> found = new HashSet<>();
                Class<?>[] list = Block.list;
                if (list != null) {
                    for (int id = 0; id < list.length; id++) {
                        Class<?> type = list[id];
                        if (type != null && (BlockThin.class.isAssignableFrom(type)
                                || BlockFence.class.isAssignableFrom(type)
                                || BlockStairs.class.isAssignableFrom(type)
                                || BlockTripWire.class.isAssignableFrom(type))) {
                            found.add(id);
                        }
                    }
                }
                connectionDrivenBlockIds = found;
            }
            return connectionDrivenBlockIds;
        }
    }

    /**
     * 模仿 MOT {@code Level.fixLegacyBlockConnections}：整区块扫描，按邻居重算所有
     * 楼梯/玻璃板/铁栏/栅栏/绊线的连接/角落位并写回。
     *
     * @return 状态发生变化的坐标
     */
    public static List<Vector3> fixChunkConnections(Level level, int chunkX, int chunkZ) {
        if (level == null) {
            return new ArrayList<>();
        }
        return fixChunkConnections(level, chunkX, chunkZ, level.getMinBlockY(), level.getMaxBlockY());
    }

    /**
     * 同上，但限定扫描的 Y 范围（用于缩小开销）。
     */
    public static List<Vector3> fixChunkConnections(Level level, int chunkX, int chunkZ, int minY, int maxY) {
        List<Vector3> changed = new ArrayList<>();
        if (level == null) {
            return changed;
        }
        BaseFullChunk chunk = level.getChunk(chunkX, chunkZ);
        if (chunk == null) {
            return changed;
        }
        Set<Integer> targetIds = connectionDrivenBlockIds();
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;
        for (int y = minY; y <= maxY; y++) {
            for (int lx = 0; lx < 16; lx++) {
                for (int lz = 0; lz < 16; lz++) {
                    int id = chunk.getBlockId(lx, y, lz);
                    if (id == 0 || !targetIds.contains(id)) {
                        continue;
                    }
                    int wx = baseX + lx;
                    int wz = baseZ + lz;
                    Block block = level.getBlock(wx, y, wz);
                    if (!isConnectionDriven(block)) {
                        continue;
                    }
                    if (updateConnection(block)) {
                        Vector3 pos = new Vector3(wx, y, wz);
                        level.setBlock(pos, block, true, false);
                        changed.add(pos);
                    }
                }
            }
        }
        return changed;
    }

    /**
     * 重算指定区块及其 8 个相邻区块内的连接/角落位（建筑边缘的连接可能跨区块）。
     *
     * @return 状态发生变化的坐标
     */
    public static List<Vector3> fixChunkConnectionsAround(Level level, int chunkX, int chunkZ) {
        List<Vector3> changed = new ArrayList<>();
        if (level == null) {
            return changed;
        }
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                changed.addAll(fixChunkConnections(level, chunkX + dx, chunkZ + dz));
            }
        }
        return changed;
    }
}

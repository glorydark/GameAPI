package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.utils.BlockWeightEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 高性能世界编辑工具
 * 相比 WorldEditTools，使用更底层的 API 跳过事件/回调，大幅提升性能
 * <p>
 * 性能对比（16x16x16 = 4096 方块）：
 * - WorldEditTools (setBlock + 事件/回调): ~200-500ms
 * - FastWorldEditTools (setBlockAtLayer): ~50-100ms
 *
 * @author glorydark
 */
public class FastWorldEditTools {

    // 每个子任务处理的最大方块数
    private static final int TASK_THRESHOLD = 4096;

    // ==================== fastFill ====================

    public static void fastFill(Level level, Vector3 pos1, Vector3 pos2, Block block) {
        fastFill(new ConsoleCommandSender(), level, pos1, pos2, block, false);
    }

    public static void fastFill(Level level, Vector3 pos1, Vector3 pos2, Block block, boolean hollow) {
        fastFill(new ConsoleCommandSender(), level, pos1, pos2, block, hollow);
    }

    public static void fastFill(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, Block block) {
        fastFill(sender, level, pos1, pos2, block, false);
    }

    public static void fastFill(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, Block block, boolean hollow) {
        long startMillis = System.currentTimeMillis();

        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        Set<Vector3> positions = new HashSet<>();

        bb.forEach((x, y, z) -> {
            if (hollow) {
                boolean isOnXFace = (x == bb.getMinX() || x == bb.getMaxX());
                boolean isOnYFace = (y == bb.getMinY() || y == bb.getMaxY());
                boolean isOnZFace = (z == bb.getMinZ() || z == bb.getMaxZ());
                if (!(isOnXFace || isOnYFace || isOnZFace)) {
                    return;
                }
            }
            positions.add(new Vector3(x, y, z));
        });

        fastSetBlocks(sender, level, positions, block, startMillis, "Fill");
    }

    // ==================== fastReplace ====================

    public static void fastReplace(Level level, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock) {
        fastReplace(new ConsoleCommandSender(), level, pos1, pos2, sourceBlock, targetBlock, false);
    }

    public static void fastReplace(Level level, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        fastReplace(new ConsoleCommandSender(), level, pos1, pos2, sourceBlock, targetBlock, checkDamage);
    }

    public static void fastReplace(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock) {
        fastReplace(sender, level, pos1, pos2, sourceBlock, targetBlock, false);
    }

    public static void fastReplace(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        long startMillis = System.currentTimeMillis();

        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);

        // 先收集所有位置
        List<Vector3> allPositions = new ArrayList<>();
        bb.forEach((x, y, z) -> allPositions.add(new Vector3(x, y, z)));

        // 并行检查并收集需要替换的位置
        Set<Vector3> toReplace = ConcurrentHashMap.newKeySet();
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new FilterAction(level, allPositions, sourceBlock, checkDamage, toReplace, 0, allPositions.size()));

        // 批量设置
        fastSetBlocks(sender, level, toReplace, targetBlock, startMillis, "Replace");
    }

    // ==================== fastSetBlocks ====================

    public static void fastSetBlocks(Level level, Set<Vector3> positions, Block block) {
        fastSetBlocks(new ConsoleCommandSender(), level, positions, block, System.currentTimeMillis(), "SetBlocks");
    }

    public static void fastSetBlocks(CommandSender sender, Level level, Set<Vector3> positions, Block block) {
        fastSetBlocks(sender, level, positions, block, System.currentTimeMillis(), "SetBlocks");
    }

    /**
     * 核心：高性能批量设置方块
     * 使用 setBlockAtLayer 跳过事件/回调/实体更新
     */
    public static void fastSetBlocks(CommandSender sender, Level level, Set<Vector3> positions, Block block, long startMillis, String operation) {
        if (positions.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + "[FastWorldEdit] " + operation + " failed: No positions to process.");
            return;
        }

        int blockId = block.getId();
        int blockData = block.getDamage();

        // 收集所有需要发送的方块
        List<Vector3> blockList = new ArrayList<>(positions);

        // 并行处理
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new SetBlockAction(level, blockList, blockId, blockData, 0, blockList.size()));

        // 批量发送给玩家
        sendBlocksToPlayers(level, positions);

        long endMillis = System.currentTimeMillis();
        long costMillis = endMillis - startMillis;

        sender.sendMessage(TextFormat.GREEN + "[FastWorldEdit] " + operation + " completed!"
                + TextFormat.GRAY + " Blocks: " + TextFormat.WHITE + positions.size()
                + TextFormat.GRAY + " | Cost: " + TextFormat.WHITE + costMillis + "ms"
                + TextFormat.GRAY + " | Block: " + TextFormat.AQUA + block.getName() + ":" + block.getDamage());
    }

    // ==================== directSetBlocks ====================

    public static void directSetBlocks(Level level, Collection<Vector3> positions, Block block) {
        directSetBlocks(new ConsoleCommandSender(), level, positions, block);
    }

    /**
     * 直接批量设置方块（跳过所有检查）
     * 用于确定位置有效且不需要替换检查的场景
     */
    public static void directSetBlocks(CommandSender sender, Level level, Collection<Vector3> positions, Block block) {
        long startMillis = System.currentTimeMillis();

        if (positions.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + "[FastWorldEdit] DirectSet failed: No positions to process.");
            return;
        }

        int blockId = block.getId();
        int blockData = block.getDamage();

        for (Vector3 pos : positions) {
            int x = pos.getFloorX();
            int y = pos.getFloorY();
            int z = pos.getFloorZ();

            if (y < level.getMinBlockY() || y > level.getMaxBlockY()) {
                continue;
            }

            // 使用轻量级 API，跳过事件/回调
            level.setBlockAtLayer(x, y, z, 0, blockId, blockData);
        }

        // 批量发送给玩家
        sendBlocksToPlayers(level, positions);

        long endMillis = System.currentTimeMillis();
        long costMillis = endMillis - startMillis;

        sender.sendMessage(TextFormat.GREEN + "[FastWorldEdit] DirectSet completed!"
                + TextFormat.GRAY + " Blocks: " + TextFormat.WHITE + positions.size()
                + TextFormat.GRAY + " | Cost: " + TextFormat.WHITE + costMillis + "ms"
                + TextFormat.GRAY + " | Block: " + TextFormat.AQUA + block.getName() + ":" + block.getDamage());
    }

    // ==================== asyncFill ====================

    public static void asyncFill(Level level, Vector3 pos1, Vector3 pos2, Block block, Runnable onComplete) {
        asyncFill(new ConsoleCommandSender(), level, pos1, pos2, block, onComplete);
    }

    public static void asyncFill(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, Block block, Runnable onComplete) {
        Server.getInstance().getScheduler().scheduleTask(GameAPI.getInstance(), () -> {
            fastFill(sender, level, pos1, pos2, block);
            if (onComplete != null) {
                onComplete.run();
            }
        }, false);
    }

    // ==================== asyncReplace ====================

    public static void asyncReplace(Level level, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock, boolean checkDamage, Runnable onComplete) {
        asyncReplace(new ConsoleCommandSender(), level, pos1, pos2, sourceBlock, targetBlock, checkDamage, onComplete);
    }

    public static void asyncReplace(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock, boolean checkDamage, Runnable onComplete) {
        // 先收集位置（可以在线程池中进行）
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        List<Vector3> allPositions = new ArrayList<>();
        bb.forEach((x, y, z) -> allPositions.add(new Vector3(x, y, z)));

        // 并行过滤
        Set<Vector3> toReplace = ConcurrentHashMap.newKeySet();
        ForkJoinPool.commonPool().execute(new FilterAction(level, allPositions, sourceBlock, checkDamage, toReplace, 0, allPositions.size()));

        // 等待过滤完成后在主线程设置
        Server.getInstance().getScheduler().scheduleTask(GameAPI.getInstance(), () -> {
            try {
                // 等待过滤完成（带超时）
                ForkJoinPool.commonPool().awaitQuiescence(30, java.util.concurrent.TimeUnit.SECONDS);
                directSetBlocks(sender, level, toReplace, targetBlock);
            } catch (Exception e) {
                GameAPI.getGameDebugManager().printError(e);
                sender.sendMessage(TextFormat.RED + "[FastWorldEdit] AsyncReplace error: " + e.getMessage());
            }
            if (onComplete != null) {
                onComplete.run();
            }
        }, false);
    }

    // ==================== fastFillRandom ====================

    public static void fastFillRandom(Level level, Vector3 pos1, Vector3 pos2, List<BlockWeightEntry> entries) {
        fastFillRandom(new ConsoleCommandSender(), level, pos1, pos2, entries, false);
    }

    public static void fastFillRandom(Level level, Vector3 pos1, Vector3 pos2, List<BlockWeightEntry> entries, boolean hollow) {
        fastFillRandom(new ConsoleCommandSender(), level, pos1, pos2, entries, hollow);
    }

    public static void fastFillRandom(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, List<BlockWeightEntry> entries) {
        fastFillRandom(sender, level, pos1, pos2, entries, false);
    }

    public static void fastFillRandom(CommandSender sender, Level level, Vector3 pos1, Vector3 pos2, List<BlockWeightEntry> entries, boolean hollow) {
        long startMillis = System.currentTimeMillis();

        if (entries == null || entries.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + "[FastWorldEdit] FillRandom failed: No block entries specified.");
            return;
        }

        // Parse entries into parallel arrays
        int count = entries.size();
        int[] choiceIds = new int[count];
        int[] choiceDatas = new int[count];
        int[] cumulativeWeights = new int[count];
        int totalWeight = 0;
        for (int idx = 0; idx < count; idx++) {
            BlockWeightEntry entry = entries.get(idx);
            Block block = entry.toBlock();
            choiceIds[idx] = block.getId();
            choiceDatas[idx] = block.getDamage();
            totalWeight += entry.getWeight();
            cumulativeWeights[idx] = totalWeight;
        }

        if (totalWeight <= 0) {
            sender.sendMessage(TextFormat.YELLOW + "[FastWorldEdit] FillRandom failed: Total weight must be positive.");
            return;
        }

        // Collect positions
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        List<Vector3> allPositions = new ArrayList<>();
        bb.forEach((x, y, z) -> {
            if (hollow) {
                boolean isOnXFace = (x == bb.getMinX() || x == bb.getMaxX());
                boolean isOnYFace = (y == bb.getMinY() || y == bb.getMaxY());
                boolean isOnZFace = (z == bb.getMinZ() || z == bb.getMaxZ());
                if (!(isOnXFace || isOnYFace || isOnZFace)) return;
            }
            allPositions.add(new Vector3(x, y, z));
        });

        if (allPositions.isEmpty()) {
            sender.sendMessage(TextFormat.YELLOW + "[FastWorldEdit] FillRandom failed: No positions to process.");
            return;
        }

        // Parallel weighted fill
        ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.invoke(new WeightedSetBlockAction(level, allPositions, choiceIds, choiceDatas, cumulativeWeights, totalWeight, 0, allPositions.size()));

        // Batch send
        sendBlocksToPlayers(level, allPositions);

        long endMillis = System.currentTimeMillis();
        long costMillis = endMillis - startMillis;

        sender.sendMessage(TextFormat.GREEN + "[FastWorldEdit] FillRandom completed!"
                + TextFormat.GRAY + " Blocks: " + TextFormat.WHITE + allPositions.size()
                + TextFormat.GRAY + " | Cost: " + TextFormat.WHITE + costMillis + "ms"
                + TextFormat.GRAY + " | Choices: " + TextFormat.AQUA + count);
    }

    // ==================== 内部方法 ====================

    /**
     * 批量发送方块更新给玩家
     */
    private static void sendBlocksToPlayers(Level level, Collection<Vector3> positions) {
        if (positions.isEmpty()) return;

        // 按区块分组，减少数据包数量
        Map<Long, List<Vector3>> chunkMap = new HashMap<>();

        for (Vector3 pos : positions) {
            long chunkKey = Level.chunkHash(pos.getFloorX() >> 4, pos.getFloorZ() >> 4);
            chunkMap.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(pos);
        }

        // 为每个区块的玩家发送更新
        for (Map.Entry<Long, List<Vector3>> entry : chunkMap.entrySet()) {
            List<Vector3> chunkBlocks = entry.getValue();
            if (chunkBlocks.isEmpty()) continue;

            Vector3 first = chunkBlocks.get(0);
            int chunkX = first.getFloorX() >> 4;
            int chunkZ = first.getFloorZ() >> 4;

            Player[] players = level.getChunkPlayers(chunkX, chunkZ).values().toArray(new Player[0]);
            if (players.length == 0) continue;

            // 转换为数组并发送
            Vector3[] blocksArray = chunkBlocks.toArray(new Vector3[0]);
            level.sendBlocks(players, blocksArray, UpdateBlockPacket.FLAG_ALL_PRIORITY);
        }
    }

    // ==================== ForkJoin Actions ====================

    /**
     * 并行设置方块的 Action
     */
    private static class SetBlockAction extends RecursiveAction {
        private final Level level;
        private final List<Vector3> positions;
        private final int blockId;
        private final int blockData;
        private final int start;
        private final int end;

        SetBlockAction(Level level, List<Vector3> positions, int blockId, int blockData, int start, int end) {
            this.level = level;
            this.positions = positions;
            this.blockId = blockId;
            this.blockData = blockData;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            int length = end - start;
            if (length <= TASK_THRESHOLD) {
                // 直接处理
                for (int i = start; i < end; i++) {
                    Vector3 pos = positions.get(i);
                    int y = pos.getFloorY();
                    if (y >= level.getMinBlockY() && y <= level.getMaxBlockY()) {
                        level.setBlockAtLayer(pos.getFloorX(), y, pos.getFloorZ(), 0, blockId, blockData);
                    }
                }
            } else {
                // 分割任务
                int mid = start + length / 2;
                SetBlockAction left = new SetBlockAction(level, positions, blockId, blockData, start, mid);
                SetBlockAction right = new SetBlockAction(level, positions, blockId, blockData, mid, end);
                invokeAll(left, right);
            }
        }
    }

    /**
     * 并行过滤位置的 Action
     */
    private static class FilterAction extends RecursiveAction {
        private final Level level;
        private final List<Vector3> positions;
        private final Block sourceBlock;
        private final boolean checkDamage;
        private final Set<Vector3> result;
        private final int start;
        private final int end;

        FilterAction(Level level, List<Vector3> positions, Block sourceBlock, boolean checkDamage, Set<Vector3> result, int start, int end) {
            this.level = level;
            this.positions = positions;
            this.sourceBlock = sourceBlock;
            this.checkDamage = checkDamage;
            this.result = result;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            int length = end - start;
            if (length <= TASK_THRESHOLD) {
                int sourceId = sourceBlock.getId();
                int sourceData = sourceBlock.getDamage();
                for (int i = start; i < end; i++) {
                    Vector3 pos = positions.get(i);
                    Block current = level.getBlock(pos);
                    if (current.getId() == sourceId) {
                        if (!checkDamage || current.getDamage() == sourceData) {
                            result.add(pos);
                        }
                    }
                }
            } else {
                int mid = start + length / 2;
                FilterAction left = new FilterAction(level, positions, sourceBlock, checkDamage, result, start, mid);
                FilterAction right = new FilterAction(level, positions, sourceBlock, checkDamage, result, mid, end);
                invokeAll(left, right);
            }
        }
    }

    /**
     * 并行按权重随机填充方块的 Action
     */
    private static class WeightedSetBlockAction extends RecursiveAction {
        private final Level level;
        private final List<Vector3> positions;
        private final int[] choiceIds;
        private final int[] choiceDatas;
        private final int[] cumulativeWeights;
        private final int totalWeight;
        private final int start;
        private final int end;

        WeightedSetBlockAction(Level level, List<Vector3> positions, int[] choiceIds, int[] choiceDatas, int[] cumulativeWeights, int totalWeight, int start, int end) {
            this.level = level;
            this.positions = positions;
            this.choiceIds = choiceIds;
            this.choiceDatas = choiceDatas;
            this.cumulativeWeights = cumulativeWeights;
            this.totalWeight = totalWeight;
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            int length = end - start;
            if (length <= TASK_THRESHOLD) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int i = start; i < end; i++) {
                    Vector3 pos = positions.get(i);
                    int y = pos.getFloorY();
                    if (y < level.getMinBlockY() || y > level.getMaxBlockY()) continue;

                    int r = random.nextInt(totalWeight);
                    int choice = 0;
                    for (int j = 0; j < cumulativeWeights.length; j++) {
                        if (r < cumulativeWeights[j]) {
                            choice = j;
                            break;
                        }
                    }

                    level.setBlockAtLayer(pos.getFloorX(), y, pos.getFloorZ(), 0, choiceIds[choice], choiceDatas[choice]);
                }
            } else {
                int mid = start + length / 2;
                WeightedSetBlockAction left = new WeightedSetBlockAction(level, positions, choiceIds, choiceDatas, cumulativeWeights, totalWeight, start, mid);
                WeightedSetBlockAction right = new WeightedSetBlockAction(level, positions, choiceIds, choiceDatas, cumulativeWeights, totalWeight, mid, end);
                invokeAll(left, right);
            }
        }
    }
}

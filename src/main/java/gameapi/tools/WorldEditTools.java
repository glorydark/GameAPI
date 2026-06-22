package gameapi.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.types.debugshape.DebugArrow;
import cn.nukkit.network.protocol.types.debugshape.DebugBox;
import cn.nukkit.network.protocol.types.debugshape.DebugText;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.annotation.Internal;
import gameapi.task.BlockFillTask;
import gameapi.task.BlockReplaceTask;
import gameapi.utils.BuildBounds;
import gameapi.utils.IntegerAxisAlignBB;
import gameapi.utils.NukkitTypeUtils;
import gameapi.utils.RotationType;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author glorydark
 */
public class WorldEditTools {

    protected static boolean generatingLargeBuild = false;

    private static final int MAX_BLOCKS_PER_TICK = 5000;

    public static void fill(Location pos1, Location pos2, Block block, boolean isReplacedExistedBlock) {
        if (!pos1.isValid() || !pos2.isValid()) {
            GameAPI.getGameDebugManager().error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        fill(null, pos1, pos2, pos1.getLevel(), block, isReplacedExistedBlock);
    }

    public static void fill(Player player, Vector3 pos1, Vector3 pos2, Block block, boolean isReplacedExistedBlock) {
        fill(player, pos1, pos2, player.getLevel(), block, isReplacedExistedBlock);
    }

    public static void fill(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, Block block, boolean isReplacedExistedBlock) {
        fill(sender, pos1, pos2, level, block, false, isReplacedExistedBlock);
    }

    public static void fill(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, Block block, boolean isHollow, boolean isReplacedExistedBlock) {
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        if (block == null) {
            if (sender != null) {
                sender.sendMessage(TextFormat.RED + "Block is undefined!");
            }
        } else {
            BlockFillTask fillTask = new BlockFillTask(level, block);
            bb.forEach((i, i1, i2) -> {
                // 检查是否在外层边界上（六个面）
                boolean isOnXFace = (i == bb.getMinX() || i == bb.getMaxX());
                boolean isOnYFace = (i1 == bb.getMinY() || i1 == bb.getMaxY());
                boolean isOnZFace = (i2 == bb.getMinZ() || i2 == bb.getMaxZ());

                // 对于空心填充，只填充构成六个面的方块
                // 即至少在一个轴向上处于边界位置
                boolean isOnOuterSurface = isOnXFace || isOnYFace || isOnZFace;

                if (isHollow) {
                    if (!isOnOuterSurface) {
                        return; // 不在外层表面上，跳过（保持空心）
                    }
                }
                Vector3 pos = new Vector3(i, i1, i2);
                if (!isReplacedExistedBlock && level.getBlock(pos).getId() != 0) {
                    return;
                }
                fillTask.addPos(pos);
            });
            GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
        }
    }

    public static void fill(Vector3 pos1, Vector3 pos2, Level level, Block block, boolean isReplacedExistedBlock) {
        fill(new ConsoleCommandSender(), pos1, pos2, level, block, false, isReplacedExistedBlock);
    }

    public static void fill(Vector3 pos1, Vector3 pos2, Level level, Block block, boolean isHollow, boolean isReplacedExistedBlock) {
        fill(new ConsoleCommandSender(), pos1, pos2, level, block, isHollow, isReplacedExistedBlock);
    }

    public static void createCircle(Location centerPos, Block block, double radius, boolean fillInside) {
        if (!centerPos.isValid()) {
            GameAPI.getGameDebugManager().error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        createCircle(null, centerPos, centerPos.getLevel(), block, radius, fillInside);
    }

    public static void createCircle(Player player, Vector3 centerPos, Block block, double radius, boolean fillInside) {
        createCircle(player, centerPos, player.getLevel(), block, radius, fillInside);
    }

    public static void createCircle(CommandSender sender, Vector3 centerPos, Level level, Block block, double radius, boolean fillInside) {
        createCircle(sender, centerPos, level, block, radius, fillInside? CircleType.DEFAULT: CircleType.EMPTY_INSIDE);
    }

    public static enum CircleType {
        DEFAULT,
        EMPTY_INSIDE,
        IGNORE_PERIPHERY
    }

    public static void createCircle(CommandSender sender, Vector3 centerPos, Level level, Block block, double radius, CircleType fillType) {
        final Vector3 centerPosFloored = centerPos.floor();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(centerPos, centerPos);
        bb = bb.expand(radius, 0, radius);
        BlockFillTask fillTask = new BlockFillTask(level, block);
        bb.forEach((i, i1, i2) -> {
            Vector3 pos = new Vector3(i, i1, i2);
            if (pos.distance(centerPosFloored) <= radius) {
                boolean periphery = pos.distance(centerPosFloored) <= radius - 1;
                switch (fillType) {
                    case EMPTY_INSIDE -> {
                        if (periphery) {
                            return;
                        }
                    }
                    case IGNORE_PERIPHERY -> {
                        if (!periphery) {
                            return;
                        }
                    }
                }
                fillTask.addPos(pos);
            }
        });
        GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
        if (sender != null) {
            sender.sendMessage(TextFormat.GREEN + "Already create a ball with " + fillTask.join() + "/" + fillTask.getImmutablePosList().size() + " blocks with " + block.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), fillTask.getEndMillis())));
        }
    }

    public static void createBall(Location centerPos, Block block, double radius, boolean fillInside) {
        if (!centerPos.isValid()) {
            GameAPI.getGameDebugManager().error(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        createBall(null, centerPos, centerPos.getLevel(), block, radius, fillInside, false);
    }

    public static void createBall(Player player, Vector3 centerPos, Block block, double radius, boolean fillInside) {
        createBall(player, centerPos, player.getLevel(), block, radius, fillInside, false);
    }

    public static void createBall(CommandSender sender, Vector3 centerPos, Level level, Block block, double radius, boolean fillInside, boolean halfHorizontally) {
        final Vector3 centerPosFloored = centerPos.floor();
        AxisAlignedBB bb = new SimpleAxisAlignedBB(centerPos, centerPos);
        bb = bb.expand(radius, radius, radius);
        BlockFillTask fillTask = new BlockFillTask(level, block);
        bb.forEach((i, i1, i2) -> {
            Vector3 pos = new Vector3(i, i1, i2);
            if (!halfHorizontally || i1 <= centerPosFloored.getFloorY()) {
                if (pos.distance(centerPosFloored) <= radius) {
                    if (!fillInside && pos.distance(centerPosFloored) <= radius - 1) {
                        return;
                    }
                    fillTask.addPos(pos);
                }
            }
        });
        GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
        if (sender != null) {
            sender.sendMessage(TextFormat.GREEN + "Already fill " + fillTask.join() + "/" + fillTask.getImmutablePosList().size() + " blocks with " + block.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), fillTask.getEndMillis())));
        }
    }


    public static void createCircle(Vector3 centerPos, Level level, Block block, double radius, boolean fillInside, boolean halfHorizontally) {
        createBall(new ConsoleCommandSender(), centerPos, level, block, radius, fillInside, halfHorizontally);
    }

    public static void replaceBlock(Location pos1, Location pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        if (!pos1.isValid() || !pos2.isValid()) {
            GameAPI.getGameDebugManager().info(TextFormat.RED + "Center pos hasn't defined the level yet!");
            return;
        }
        replaceBlock(null, pos1, pos2, pos1.getLevel(), sourceBlock, targetBlock, checkDamage);
    }

    public static void replaceBlock(Player player, Vector3 pos1, Vector3 pos2, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        replaceBlock(player, pos1, pos2, player.getLevel(), sourceBlock, targetBlock, checkDamage);
    }

    public static void replaceBlock(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);
        if (sourceBlock == null || targetBlock == null) {
            if (sender != null) {
                sender.sendMessage(TextFormat.RED + "Block is undefined!");
            }
        } else {
            BlockReplaceTask replaceTask = new BlockReplaceTask(level, sourceBlock, targetBlock, checkDamage);
            bb.forEach((i, i1, i2) -> replaceTask.addPos(new Vector3(i, i1, i2)));
            GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(replaceTask);
            if (sender != null) {
                sender.sendMessage(TextFormat.GREEN + "Already replace " + replaceTask.getImmutablePosList().size() + " blocks from " + sourceBlock.getName() + " to " + targetBlock.getName() + ", cost: " + (SmartTools.timeDiffMillisToString(System.currentTimeMillis(), replaceTask.getEndMillis())));
            }
        }
    }

    public static void replaceBlock(Vector3 pos1, Vector3 pos2, Level level, Block sourceBlock, Block targetBlock, boolean checkDamage) {
        replaceBlock(new ConsoleCommandSender(), pos1, pos2, level, sourceBlock, targetBlock, checkDamage);
    }

    public static void fillTetrahedron(Player player, Vector3 pos1, Vector3 pos2, Vector3 pos3, Vector3 pos4, Block block) {
        fillTetrahedron(pos1, pos2, pos3, pos4, player.getLevel(), block, false);
    }

    public static void fillTetrahedron(Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, Level level, Block block, boolean hollow) {
        // 获取包围盒
        AxisAlignedBB box = new SimpleAxisAlignedBB(
                Math.min(Math.min(p1.x, p2.x), Math.min(p3.x, p4.x)),
                Math.min(Math.min(p1.y, p2.y), Math.min(p3.y, p4.y)),
                Math.min(Math.min(p1.z, p2.z), Math.min(p3.z, p4.z)),
                Math.max(Math.max(p1.x, p2.x), Math.max(p3.x, p4.x)),
                Math.max(Math.max(p1.y, p2.y), Math.max(p3.y, p4.y)),
                Math.max(Math.max(p1.z, p2.z), Math.max(p3.z, p4.z))
        );

        double totalVol = tetraVolume(p1, p2, p3, p4);
        if (totalVol <= 0) return; // 共面情况

        double eps = totalVol * 1e-4; // 原来是 1e-6

        box.forEach((x, y, z) -> {
            Vector3 p = new Vector3(x + 0.5, y + 0.5, z + 0.5);

            // 判断该点是否在四面体内
            double v1 = tetraVolume(p, p2, p3, p4);
            double v2 = tetraVolume(p1, p, p3, p4);
            double v3 = tetraVolume(p1, p2, p, p4);
            double v4 = tetraVolume(p1, p2, p3, p);
            // double sum = v1 + v2 + v3 + v4;

            if (isBlockInsideTetra(x, y, z, p1, p2, p3, p4, totalVol, eps)) {
                if (hollow) {
                    if (!isSurface(x, y, z, p1, p2, p3, p4, totalVol, eps)) return;
                }
                level.setBlock(new Vector3(x, y, z), block);
            }
        });
    }

    // 判断是否在四面体表面上
    private static boolean isSurface(int x, int y, int z, Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4, double totalVol, double eps) {
        int[][] dirs = {{1,0,0},{-1,0,0},{0,1,0},{0,-1,0},{0,0,1},{0,0,-1}};
        for (int[] d : dirs) {
            Vector3 np = new Vector3(x + 0.5 + d[0], y + 0.5 + d[1], z + 0.5 + d[2]);
            double v1 = tetraVolume(np, p2, p3, p4);
            double v2 = tetraVolume(p1, np, p3, p4);
            double v3 = tetraVolume(p1, p2, np, p4);
            double v4 = tetraVolume(p1, p2, p3, np);
            double sum = v1 + v2 + v3 + v4;
            if (Math.abs(sum - totalVol) > eps) return true; // 邻居在外面
        }
        return false;
    }

    private static boolean isBlockInsideTetra(int x, int y, int z,
                                              Vector3 p1, Vector3 p2, Vector3 p3, Vector3 p4,
                                              double totalVol, double eps) {
        // 方块的8个角
        double[] offsets = {0, 1};
        for (double dx : offsets) {
            for (double dy : offsets) {
                for (double dz : offsets) {
                    Vector3 corner = new Vector3(x + dx, y + dy, z + dz);
                    double v1 = tetraVolume(corner, p2, p3, p4);
                    double v2 = tetraVolume(p1, corner, p3, p4);
                    double v3 = tetraVolume(p1, p2, corner, p4);
                    double v4 = tetraVolume(p1, p2, p3, corner);
                    double sum = v1 + v2 + v3 + v4;
                    if (Math.abs(sum - totalVol) <= eps) return true; // 任意一个角在内即可
                }
            }
        }
        return false;
    }

    // 四面体体积（混合积）
    private static double tetraVolume(Vector3 a, Vector3 b, Vector3 c, Vector3 d) {
        double ax = a.x - d.x, ay = a.y - d.y, az = a.z - d.z;
        double bx = b.x - d.x, by = b.y - d.y, bz = b.z - d.z;
        double cx = c.x - d.x, cy = c.y - d.y, cz = c.z - d.z;

        double crossX = by * cz - bz * cy;
        double crossY = bz * cx - bx * cz;
        double crossZ = bx * cy - by * cx;

        double det = ax * crossX + ay * crossY + az * crossZ;
        return Math.abs(det) / 6.0;
    }

    @Internal
    public static void previewBuild(Player player, String fileName, Vector3 startPos) {
        File jsonFile = new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + fileName + File.separator + "build.json");
        if (!jsonFile.exists()) {
            player.sendMessage("build.json not found");
            return;
        }

        Config config = new Config(jsonFile, Config.JSON);

        List<Integer> rMax = config.getIntegerList("relativeMax");

        int rx2 = rMax.get(0);
        int ry2 = rMax.get(1);
        int rz2 = rMax.get(2);

        previewBuild(player, new BuildBounds(rx2, ry2, rz2), startPos);
    }

    @Internal
    public static void previewBuild(Player player, Vector3 pos1, Vector3 pos2) {
        IntegerAxisAlignBB bb = new IntegerAxisAlignBB(pos1, pos2);
        Vector3 bounds = new Vector3(
                bb.getMaxX() - bb.getMinX(),
                bb.getMaxY() - bb.getMinY(),
                bb.getMaxZ() - bb.getMinZ()
        );
        BuildBounds buildBounds = new BuildBounds(bounds);
        Vector3 min = new Vector3(
                bb.getMinX(),
                bb.getMinY(),
                bb.getMinZ()
        );
        previewBuild(player, buildBounds, min);
    }

    @Internal
    public static void previewBuild(Player player, BuildBounds buildBounds, Vector3 startPos) {

        // generateBuild 使用 getFloorX/Y/Z，preview 需取整对齐
        Vector3 origin = new Vector3(startPos.getFloorX(), startPos.getFloorY(), startPos.getFloorZ());
        Vector3 boundBox = buildBounds.getShapeBounds();
        Vector3 bottomCenter = buildBounds.getShapeBottomCenter(origin);

        // 清旧预览
        player.removeAllShapes();

        Level level = player.getLevel();

        // DebugBox 的 position 是中心点，boxBounds 是完整尺寸
        Vector3 boxCenter = origin.add(boundBox.clone().multiply(0.5));
        player.addShape(
                new DebugBox(
                        1,
                        level.getDimension(),
                        boxCenter.asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.WHITE,
                        boundBox.asVector3f())
        );

        player.addShape(
                new DebugArrow(
                        2,
                        level.getDimension(),
                        bottomCenter.asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.GREEN,
                        bottomCenter.north(5).asVector3f(),
                        1f,
                        0.2f,
                        1)
        );

        player.addShape(
                new DebugArrow(
                        3,
                        level.getDimension(),
                        bottomCenter.asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.RED,
                        bottomCenter.up(5).asVector3f(),
                        1f,
                        0.2f,
                        1)
        );

        player.addShape(
                new DebugArrow(
                        4,
                        level.getDimension(),
                        bottomCenter.asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.YELLOW,
                        bottomCenter.east(5).asVector3f(),
                        1f,
                        0.2f,
                        1)
        );

        player.addShape(
                new DebugText(
                        5,
                        level.getDimension(),
                        bottomCenter.north(5).asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.GREEN,
                        "X")
        );

        player.addShape(
                new DebugText(
                        6,
                        level.getDimension(),
                        bottomCenter.up(5).asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.RED,
                        "Y")
        );

        player.addShape(
                new DebugText(
                        7,
                        level.getDimension(),
                        bottomCenter.east(5).asVector3f(),
                        1.0f,
                        new Vector3f(),
                        60f,
                        Color.YELLOW,
                        "Z")
        );
    }


    @Internal
    public static void generateBuild(Player player, String fileName, Vector3 startPos) {
        generateBuild(player, fileName, startPos, player.getLevel(), 0, RotationType.NONE, false);
    }

    @Internal
    public static void generateBuild(CommandSender sender, String fileName, Vector3 startPos, Level level) {
        generateBuild(sender, fileName, startPos, level, 0, RotationType.NONE, false, "nbt");
    }

    @Internal
    public static void generateBuild(CommandSender sender, String fileName, Vector3 startPos, Level level, int rotationDegree, RotationType rotationType) {
        generateBuild(sender, fileName, startPos, level, rotationDegree, rotationType, false, "nbt");
    }

    @Internal
    public static void generateBuild(CommandSender sender, String fileName, Vector3 startPos, Level level, int rotationDegree, RotationType rotationType, boolean forceFast) {
        generateBuild(sender, fileName, startPos, level, rotationDegree, rotationType, forceFast, "nbt");
    }

    @Internal
    public static void generateBuild(CommandSender sender, String fileName, Vector3 startPos, Level level, int rotationDegree, RotationType rotationType, boolean forceFast, String format) {
        if (generatingLargeBuild) {
            sender.sendMessage("You have started a task of creating or saving build. Please wait...");
            return;
        }

        boolean isCbd = format.equalsIgnoreCase("cbd");
        String ext = isCbd ? ".cbd" : ".nbt";
        File folder = new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + fileName + File.separator);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(ext));
        if (files == null || files.length == 0) {
            sender.sendMessage("Cannot find building folder or no " + ext + " files: " + fileName);
            return;
        }

        File jsonFile = new File(folder, "build.json");
        if (!jsonFile.exists()) {
            sender.sendMessage("build.json not found");
            return;
        }

        Config config = new Config(jsonFile, Config.JSON);
        List<Integer> rMax = config.getIntegerList("relativeMax");
        BuildBounds buildBounds = new BuildBounds(rMax.get(0), rMax.get(1), rMax.get(2));

        generatingLargeBuild = true;
        long totalStart = System.currentTimeMillis();
        int maxGenerateSections = files.length;
        sender.sendMessage("Start generating building task... [Count: " + maxGenerateSections + "]");
        GameAPI.getInstance().getLogger().info("Start generating building task... [Count: " + maxGenerateSections + "]");

        // Phase 1: Parse all files in parallel on the thread pool
        List<CompletableFuture<Map.Entry<Integer, List<BuildBlockEntry>>>> futures = new ArrayList<>();
        int idx = 0;
        for (File file : files) {
            File f = file;
            int sectionIdx = idx++;
            futures.add(CompletableFuture.supplyAsync(() -> {
                long sectionStart = System.currentTimeMillis();
                try {
                    byte[] fileData = Files.readAllBytes(f.toPath());
                    List<RawNbtParser.BlockEntry> rawEntries;
                    if (isCbd) {
                        rawEntries = CompactBuildFormat.decode(fileData).blocks();
                    } else {
                        rawEntries = new RawNbtParser(fileData).parseBlocks();
                    }

                    List<BuildBlockEntry> sectionEntries = new ArrayList<>(rawEntries.size());
                    for (RawNbtParser.BlockEntry entry : rawEntries) {
                        Vector3 rotated = switch (rotationType) {
                            case AROUND_CENTER ->
                                    buildBounds.getBlockPosAfterHorizontalRotatedByCenter(startPos, entry.x(), entry.y(), entry.z(), rotationDegree);
                            case AROUND_START_POSITION -> rotateAroundPoint(startPos, entry.x(), entry.y(), entry.z(), rotationDegree);
                            default ->
                                    new Vector3(startPos.getFloorX() + entry.x(), startPos.getFloorY() + entry.y(), startPos.getFloorZ() + entry.z());
                        };

                        int x = rotated.getFloorX();
                        int y = rotated.getFloorY();
                        int z = rotated.getFloorZ();

                        if (y > level.getMaxBlockY() || y < level.getMinBlockY()) continue;
                        if (entry.blockId() == BlockID.AIR) continue;

                        CompoundTag beTag = entry.blockEntityData() != null ? NBTIO.read(entry.blockEntityData()) : null;

                        sectionEntries.add(new BuildBlockEntry(x, y, z, entry.blockId(), entry.damage(), beTag, entry.layer1Id(), entry.layer1Damage()));
                    }
                    GameAPI.getInstance().getLogger().info("Parsed section [" + (sectionIdx + 1) + "/" + maxGenerateSections + "] (" + sectionEntries.size() + " blocks, " + (System.currentTimeMillis() - sectionStart) + "ms)");
                    return Map.entry(sectionIdx, sectionEntries);
                } catch (IOException e) {
                    throw new CompletionException("Failed to read file: " + f.getName(), e);
                }
            }, GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .sorted(Map.Entry.comparingByKey())
                        .map(Map.Entry::getValue)
                        .toList())
                .thenAccept(sections -> {
                    int totalBlocks = sections.stream().mapToInt(List::size).sum();
                    sender.sendMessage("Phase 1 complete: [" + fileName + "] " + sections.size() + " sections, " + totalBlocks + " blocks, cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), totalStart));
                    GameAPI.getInstance().getLogger().info("Phase 1 complete: [" + fileName + "] " + sections.size() + " sections, " + totalBlocks + " blocks, cost: " + (System.currentTimeMillis() - totalStart) + "ms");

                    List<List<BuildBlockEntry>> finalSections;
                    int finalSectionCount;
                    if (forceFast) {
                        finalSections = sections;
                        finalSectionCount = sections.size();
                    } else {
                        List<List<BuildBlockEntry>> batches = new ArrayList<>();
                        for (List<BuildBlockEntry> section : sections) {
                            if (section.size() <= MAX_BLOCKS_PER_TICK) {
                                batches.add(section);
                            } else {
                                for (int i = 0; i < section.size(); i += MAX_BLOCKS_PER_TICK) {
                                    batches.add(section.subList(i, Math.min(i + MAX_BLOCKS_PER_TICK, section.size())));
                                }
                            }
                        }
                        finalSections = batches;
                        finalSectionCount = batches.size();
                    }

                    List<List<BuildBlockEntry>> finalSectionsRef = finalSections;
                    int finalSectionCountRef = finalSectionCount;
                    Server.getInstance().getScheduler().scheduleTask(GameAPI.getInstance(), () -> {
                        scheduleBuildPlacement(sender, level, finalSectionsRef, 0, totalStart, new AtomicInteger(0), finalSectionCountRef, fileName);
                    }, false);
                }).exceptionally(throwable -> {
                    GameAPI.getGameDebugManager().printError(throwable);
                    generatingLargeBuild = false;
                    sender.sendMessage("Build generation failed: " + throwable.getMessage());
                    return null;
                });
    }

    private record BuildBlockEntry(int x, int y, int z, int blockId, int damage, CompoundTag blockEntityTag, int layer1Id, int layer1Damage) {}

    private static void scheduleBuildPlacement(CommandSender sender, Level level, List<List<BuildBlockEntry>> sections,
                                                int sectionIdx, long totalStart,
                                                AtomicInteger completedSections, int totalSections, String fileName) {
        if (sectionIdx >= sections.size()) {
            generatingLargeBuild = false;
            int totalBlocks = sections.stream().mapToInt(List::size).sum();
            sender.sendMessage("Finish all building tasks! [" + fileName + "] Total blocks: " + totalBlocks + ". Section Count: " + completedSections.get() + ". Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), totalStart));
            GameAPI.getInstance().getLogger().info("Finish all building tasks! [" + fileName + "] Total blocks: " + totalBlocks + ". Section Count: " + completedSections.get() + ". Time cost: " + SmartTools.timeDiffMillisToString(System.currentTimeMillis(), totalStart));
            return;
        }

        long sectionStart = System.currentTimeMillis();
        List<BuildBlockEntry> entries = sections.get(sectionIdx);
        List<Vector3> allPositions = new ArrayList<>(entries.size());

        for (BuildBlockEntry entry : entries) {
            Vector3 pos = new Vector3(entry.x, entry.y, entry.z);
            allPositions.add(pos);

            if (entry.blockEntityTag != null) {
                Block block = Block.get(entry.blockId, entry.damage);
                if (block == null) block = new BlockUnknown(entry.blockId, entry.damage);
                level.setBlock(pos, block, true, false);

                Block placed = level.getBlock(pos);
                BlockEntity old = placed.getLevelBlockEntity();
                if (old != null) old.close();
                if (placed instanceof BlockEntityHolder holder) {
                    BlockEntity be = holder.createBlockEntity(entry.blockEntityTag);
                    if (be instanceof BlockEntitySpawnable spawnable) {
                        spawnable.spawnToAll();
                    }
                }
            } else {
                level.setBlockAtLayer(entry.x, entry.y, entry.z, 0, entry.blockId, entry.damage);
            }

            if (entry.layer1Id != 0) {
                level.setBlockAtLayer(entry.x, entry.y, entry.z, 1, entry.layer1Id, entry.layer1Damage);
            }
        }

        sendBuildBlocksToPlayers(level, allPositions);

        completedSections.incrementAndGet();
        String timeDiff = SmartTools.timeDiffMillisToString(System.currentTimeMillis(), totalStart);
        String sectionTime = String.valueOf(System.currentTimeMillis() - sectionStart);
        sender.sendMessage("Finish generating building task [" + completedSections.get() + "/" + totalSections + "]! (" + entries.size() + " blocks, " + sectionTime + "ms, total: " + timeDiff + ")");
        GameAPI.getInstance().getLogger().info("Finish generating building task [" + completedSections.get() + "/" + totalSections + "]! (" + entries.size() + " blocks, " + sectionTime + "ms)");

        Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.getInstance(),
            () -> scheduleBuildPlacement(sender, level, sections, sectionIdx + 1, totalStart, completedSections, totalSections, fileName), 1);
    }

    private static void sendBuildBlocksToPlayers(Level level, List<Vector3> positions) {
        if (positions.isEmpty()) return;

        Map<Long, List<Vector3>> chunkMap = new HashMap<>();
        for (Vector3 pos : positions) {
            long chunkKey = Level.chunkHash(pos.getFloorX() >> 4, pos.getFloorZ() >> 4);
            chunkMap.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(pos);
        }

        for (Map.Entry<Long, List<Vector3>> entry : chunkMap.entrySet()) {
            List<Vector3> chunkBlocks = entry.getValue();
            if (chunkBlocks.isEmpty()) continue;

            Vector3 first = chunkBlocks.get(0);
            int chunkX = first.getFloorX() >> 4;
            int chunkZ = first.getFloorZ() >> 4;

            Player[] players = level.getChunkPlayers(chunkX, chunkZ).values().toArray(new Player[0]);
            if (players.length == 0) continue;

            level.sendBlocks(players, chunkBlocks.toArray(new Vector3[0]), UpdateBlockPacket.FLAG_ALL_PRIORITY);
        }
    }

    public static CompoundTag loadBuildExtraData(String folderName) {
        return loadBuildExtraData(new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + folderName + File.separator));
    }

    public static CompoundTag loadBuildExtraData(File folder) {
        try {
            File extraFile = new File(folder, "extra.nbt");
            if (extraFile.exists()) {
                return NBTIO.read(extraFile);
            }
        } catch (IOException e) {
            GameAPI.getGameDebugManager().printError(e);
        }
        return null;
    }

    /**
     * 将 extraTag 中的指定 ListTag 字段从绝对坐标转为相对坐标
     * extra 里有 {"endPoint": [I: 100, 5, 200]}，选区 minPos = (90, 5, 185)
     * 转换后变成 {"endPoint": [I: 10, 0, 15]}
     */
    public static CompoundTag relativizeExtraTag(CompoundTag extra, Vector3 minPos, String... listKeys) {
        if (extra == null) return null;
        CompoundTag result = extra.clone();
        int mx = minPos.getFloorX();
        int my = minPos.getFloorY();
        int mz = minPos.getFloorZ();
        for (String key : listKeys) {
            if (result.contains(key)) {
                ListTag<IntTag> list = result.getList(key, IntTag.class);
                if (list.size() >= 3) {
                    ListTag<IntTag> newList = new ListTag<>(key);
                    newList.add(new IntTag("", list.get(0).getData() - mx));
                    newList.add(new IntTag("", list.get(1).getData() - my));
                    newList.add(new IntTag("", list.get(2).getData() - mz));
                    result.put(key, newList);
                }
            }
        }
        return result;
    }

    /**
     * 将 extraTag 中指定 x/y/z 三个 int 字段从绝对坐标转为相对坐标
     * extra 里有 {endX: 100, endY: 5, endZ: 200}，选区 minPos = (90, 5, 185)
     * 转换后变成 {endX: 10, endY: 0, endZ: 15}
     */
    public static CompoundTag relativizeExtraTag(CompoundTag extra, Vector3 minPos, String xKey, String yKey, String zKey) {
        if (extra == null) return null;
        CompoundTag result = extra.clone();
        if (result.contains(xKey) && result.contains(yKey) && result.contains(zKey)) {
            int mx = minPos.getFloorX();
            int my = minPos.getFloorY();
            int mz = minPos.getFloorZ();
            result.putInt(xKey, result.getInt(xKey) - mx);
            result.putInt(yKey, result.getInt(yKey) - my);
            result.putInt(zKey, result.getInt(zKey) - mz);
        }
        return result;
    }

    public static Vector3 rotateXZ(Vector3 vector3, int degree) {
        degree = ((degree % 360) + 360) % 360;

        double x = vector3.getX();
        double y = vector3.getY();
        double z = vector3.getZ();
        switch (degree) {
            case 0:
                return new Vector3(x, y, z);
            case 90:
                return new Vector3(-z, y, x);
            case 180:
                return new Vector3(-x, y, -z);
            case 270:
                return new Vector3(z, y, -x);
            default:
                throw new IllegalArgumentException("Only 0,90,180,270 are supported");
        }
    }

    /**
     * 围绕起始点旋转坐标
     * @param startPos 起始位置（旋转中心）
     * @param rx 相对x坐标
     * @param ry 相对y坐标
     * @param rz 相对z坐标
     * @param rotationDegree 旋转角度（仅水平旋转）
     * @return 旋转后的世界坐标
     */
    private static Vector3 rotateAroundPoint(Vector3 startPos, int rx, int ry, int rz, int rotationDegree) {
        // 将相对坐标转换为以startPos为中心的绝对坐标
        double x = startPos.getX() + rx;
        double y = startPos.getY() + ry;
        double z = startPos.getZ() + rz;

        // 计算相对于startPos的偏移
        double dx = x - startPos.getX();
        double dz = z - startPos.getZ();

        // 应用旋转
        double radians = Math.toRadians(rotationDegree);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double rotatedX = dx * cos - dz * sin;
        double rotatedZ = dx * sin + dz * cos;

        // 计算最终坐标
        return new Vector3(
                startPos.getX() + rotatedX,
                y, // Y坐标不变（仅水平旋转）
                startPos.getZ() + rotatedZ
        );
    }

    @Internal
    public static void saveBuild(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level) {
        saveBuild(sender, pos1, pos2, level, null);
    }

    public static void saveBuild(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, CompoundTag extraTag) {
        saveBuild(sender, pos1, pos2, level, extraTag, "nbt");
    }

    public static void saveBuild(CommandSender sender, Vector3 pos1, Vector3 pos2, Level level, CompoundTag extraTag, String format) {
        saveBuild(sender,
                new Vector3(Math.min(pos1.getFloorX(), pos2.getFloorX()),
                        Math.min(pos1.getFloorY(), pos2.getFloorY()),
                        Math.min(pos1.getFloorZ(), pos2.getFloorZ())),
                pos1, pos2, level, extraTag, format);
    }

    @Internal
    public static void saveBuild(CommandSender sender, Vector3 minPos, Vector3 pos1, Vector3 pos2, Level level) {
        saveBuild(sender, minPos, pos1, pos2, level, null);
    }

    public static void saveBuild(CommandSender sender, Vector3 minPos, Vector3 pos1, Vector3 pos2, Level level, CompoundTag extraTag) {
        saveBuild(sender, minPos, pos1, pos2, level, extraTag, "nbt");
    }

    @Internal
    public static void saveBuild(CommandSender sender, Vector3 minPos, Vector3 pos1, Vector3 pos2, Level level, CompoundTag extraTag, String format) {
        if (generatingLargeBuild) {
            GameAPI.getInstance().getLogger().info("You have started a task of creating or saving build. Please wait...");
            return;
        }
        generatingLargeBuild = true;
        IntegerAxisAlignBB integerAxisAlignBB = new IntegerAxisAlignBB(pos1, pos2);
        String name = String.valueOf(System.currentTimeMillis());
        long saveStartMillis = System.currentTimeMillis();

        IntegerAxisAlignBB[] bbs = integerAxisAlignBB.splitAABB(64, 64, 64);
        GameAPI.getGameDebugManager().info("Start building save task in {" + integerAxisAlignBB + "}");
        sender.sendMessage("Start building save task in {" + integerAxisAlignBB + "}");

        sender.sendMessage("For better performance, the range are cut into various sections: ");
        GameAPI.getInstance().getLogger().info("For better performance, the range are cut into various sections: ");
        for (int i = 0; i < bbs.length; i++) {
            sender.sendMessage("- [" + i + "] " + bbs[i].toString());
            GameAPI.getInstance().getLogger().info("- [" + i + "] " + bbs[i].toString());
        }

        AtomicLong readBlockCountAll = new AtomicLong(0);
        AtomicInteger sectionCount = new AtomicInteger(0);
        new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + name + "/").mkdirs();
        File jsonFile = new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + name + File.separator + "build.json");

        Config config = new Config(jsonFile, Config.JSON);

        int cx = minPos.getFloorX();
        int cy = minPos.getFloorY();
        int cz = minPos.getFloorZ();

        int minX = integerAxisAlignBB.getMinX();
        int minY = integerAxisAlignBB.getMinY();
        int minZ = integerAxisAlignBB.getMinZ();
        int maxX = integerAxisAlignBB.getMaxX();
        int maxY = integerAxisAlignBB.getMaxY();
        int maxZ = integerAxisAlignBB.getMaxZ();

        config.set("minPos", Arrays.asList(cx, cy, cz));
        config.set("maxPos", Arrays.asList(maxX, maxY, maxZ));

        config.set("relativeMin", Arrays.asList(
                minX - cx,
                minY - cy,
                minZ - cz
        ));

        config.set("relativeMax", Arrays.asList(
                maxX - cx,
                maxY - cy,
                maxZ - cz
        ));

        config.save();

        if (extraTag != null) {
            File extraFile = new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + name + File.separator + "extra.nbt");
            try (FileOutputStream fileOutputStream = new FileOutputStream(extraFile)) {
                fileOutputStream.write(NBTIO.write(extraTag));
            } catch (IOException e) {
                GameAPI.getGameDebugManager().printError(e);
            }
        }

        boolean isCbd = format.equalsIgnoreCase("cbd");
        CompletableFuture.runAsync(() -> {
            try {
                for (int bbsIndex = 0; bbsIndex < bbs.length; bbsIndex++) {
                    IntegerAxisAlignBB newBB = bbs[bbsIndex];
                    sender.sendMessage("Starting task " + bbsIndex + "...");
                    long startMillisForSection = System.currentTimeMillis();
                    AtomicLong queryBlockTimes = new AtomicLong(0);
                    long maxCount = newBB.getSize();
                    AtomicLong lastTipPercentage = new AtomicLong(0);
                    AtomicLong readBlockCountForSection = new AtomicLong(0);
                    CompoundTag tag = isCbd ? null : new CompoundTag().putList(new ListTag<>("blocks"));
                    List<RawNbtParser.BlockEntry> cbdBlocks = isCbd ? new ArrayList<>() : null;
                    int finalBbsIndex = bbsIndex;
                    int rxMin = minPos.getFloorX();
                    int ryMin = minPos.getFloorY();
                    int rzMin = minPos.getFloorZ();
                    newBB.forEach(((i, i1, i2) -> {
                        Block blockAtPosition = level.getBlock(i, i1, i2, true);
                        if (blockAtPosition.getId() != Block.AIR) {
                            int x = i - rxMin;
                            int y = i1 - ryMin;
                            int z = i2 - rzMin;
                            int blockId = blockAtPosition.getId();
                            int damage = blockAtPosition.getDamage();
                            int l1Id = 0, l1Dam = 0;
                            if (NukkitTypeUtils.getNukkitType() == NukkitTypeUtils.NukkitType.MOT) {
                                Block blockLayer1 = level.getBlock(i, i1, i2, 1, true);
                                l1Id = blockLayer1.getId();
                                l1Dam = blockLayer1.getDamage();
                            }
                            if (isCbd) {
                                byte[] beData = null;
                                if (blockAtPosition instanceof BlockEntityHolder holder) {
                                    BlockEntity blockEntity = holder.getBlockEntity();
                                    if (blockEntity != null) {
                                        blockEntity.saveNBT();
                                        try {
                                            beData = NBTIO.write(blockEntity.namedTag);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        GameAPI.getInstance().getLogger().info("[" + finalBbsIndex + "] Save blockEntity " + blockEntity.getSaveId() + " at " + blockAtPosition.asBlockVector3().asVector3().toString());
                                    }
                                }
                                cbdBlocks.add(new RawNbtParser.BlockEntry(x, y, z, blockId, damage, beData, l1Id, l1Dam));
                            } else {
                                CompoundTag addTag = new CompoundTag()
                                        .putInt("x", x)
                                        .putInt("y", y)
                                        .putInt("z", z)
                                        .putInt("blockId", blockId)
                                        .putInt("damage", damage);
                                if (l1Id != 0) {
                                    addTag.putCompound("layer1", new CompoundTag()
                                            .putInt("blockId", l1Id)
                                            .putInt("damage", l1Dam));
                                }
                                if (blockAtPosition instanceof BlockEntityHolder holder) {
                                    BlockEntity blockEntity = holder.getBlockEntity();
                                    if (blockEntity != null) {
                                        blockEntity.saveNBT();
                                        CompoundTag save = blockEntity.namedTag;
                                        addTag.putCompound("blockEntityData", save);
                                        GameAPI.getInstance().getLogger().info("[" + finalBbsIndex + "] Save blockEntity " + blockEntity.getSaveId() + " at " + blockAtPosition.asBlockVector3().asVector3().toString() + ", nbt: " + save.toSNBT());
                                    }
                                }
                                tag.getList("blocks", CompoundTag.class).add(addTag);
                            }
                            readBlockCountAll.getAndIncrement();
                            readBlockCountForSection.getAndIncrement();
                        }
                        queryBlockTimes.getAndIncrement();
                        if (queryBlockTimes.get() > (maxCount / 100) * (lastTipPercentage.get() + 5)) {
                            GameAPI.getInstance().getLogger().info("[" + finalBbsIndex + "] Saving sections... §e" + lastTipPercentage + "%");
                            lastTipPercentage.getAndAdd(5);
                        }
                        if (queryBlockTimes.get() >= newBB.getSize()) {
                            if (readBlockCountForSection.get() != 0) {
                                String sectionExt = isCbd ? ".cbd" : ".nbt";
                                File file = new File(GameAPI.getPath() + File.separator + "buildings" + File.separator + name + File.separator + System.currentTimeMillis() + "_" + UUID.randomUUID() + sectionExt);
                                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                                    if (isCbd) {
                                        fileOutputStream.write(CompactBuildFormat.encode(cbdBlocks, maxX - rxMin, maxY - ryMin, maxZ - rzMin));
                                    } else {
                                        fileOutputStream.write(NBTIO.write(tag));
                                    }
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                sectionCount.getAndIncrement();
                                StringBuilder builder = new StringBuilder();
                                builder.append("[").append(finalBbsIndex).append("] ")
                                        .append("Finish saving building in ")
                                        .append(name)
                                        .append(" | ")
                                        .append("Time Cost: ").append(SmartTools.timeDiffMillisToString(startMillisForSection, System.currentTimeMillis()));
                                sender.sendMessage(builder.toString());
                                GameAPI.getInstance().getLogger().info(builder.toString());
                            } else {
                                StringBuilder builder = new StringBuilder();
                                builder.append("[").append(finalBbsIndex).append("] ")
                                        .append(" Finish reading the section. The section contains nothing but air blocks. Turning into next sections...");
                                sender.sendMessage(builder.toString());
                                GameAPI.getInstance().getLogger().info(builder.toString());
                            }
                        }
                    }));
                }
            } catch (Throwable t) {
                GameAPI.getGameDebugManager().printError(t);
            }
        }, GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR).thenRun(() -> {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Finish saving building task! Build name: ").append(name)
                            .append(" | Section count: ").append(sectionCount.get())
                            .append(" | Block count: ").append(readBlockCountAll.get())
                            .append(" | Time cost: ").append(SmartTools.timeDiffMillisToString(System.currentTimeMillis(), saveStartMillis));
                    sender.sendMessage(builder.toString());
                    GameAPI.getInstance().getLogger().info(builder.toString());
                    generatingLargeBuild = false;
                }
        );
    }

    public static void createMultiplePlatform(Player player, int angleRotation, int radius, int platformRadius, Block block) {
        Vector3 center = player.floor();
        Vector3 v1 = center.add(radius, 0, radius);
        Vector3 v2 = center.add(radius, 0, -radius);
        Vector3 v3 = center.add(-radius, 0, radius);
        Vector3 v4 = center.add(-radius, 0, -radius);
        createBall(player, v1, player.getLevel(), block, platformRadius, true, true);
        createBall(player, v2, player.getLevel(), block, platformRadius, true, true);
        createBall(player, v3, player.getLevel(), block, platformRadius, true, true);
        createBall(player, v4, player.getLevel(), block, platformRadius, true, true);
        for (int angle = 0; angle < 90; angle += angleRotation) {
            Vector3 v5 = SpatialTools.rotateByYAxis(center, v1, angle);
            Vector3 v6 = SpatialTools.rotateByYAxis(center, v2, angle);
            Vector3 v7 = SpatialTools.rotateByYAxis(center, v3, angle);
            Vector3 v8 = SpatialTools.rotateByYAxis(center, v4, angle);
            createBall(player, v5, player.getLevel(), block, platformRadius, true, true);
            createBall(player, v6, player.getLevel(), block, platformRadius, true, true);
            createBall(player, v7, player.getLevel(), block, platformRadius, true, true);
            createBall(player, v8, player.getLevel(), block, platformRadius, true, true);
        }
        /*
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
        System.out.println(v4);
        System.out.println(v5);
        System.out.println(v6);
        System.out.println(v7);
        System.out.println(v8);
         */
    }

    public static boolean isGeneratingLargeBuild() {
        return generatingLargeBuild;
    }

    public static void main(String[] strings) {

        Vector3 center = new Vector3(0, 0, 0);
        Vector3 v1 = center.add(5, 0, 5);
        Vector3 v2 = center.add(5, 0, -5);
        Vector3 v3 = center.add(-5, 0, 5);
        Vector3 v4 = center.add(-5, 0, -5);
        Vector3 v5 = SpatialTools.rotateByYAxis(center, v1, 45);
        Vector3 v6 = SpatialTools.rotateByYAxis(center, v2, 45);
        Vector3 v7 = SpatialTools.rotateByYAxis(center, v3, 45);
        Vector3 v8 = SpatialTools.rotateByYAxis(center, v4, 45);
        System.out.println(v1);
        System.out.println(v2);
        System.out.println(v3);
        System.out.println(v4);
        System.out.println(v5);
        System.out.println(v6);
        System.out.println(v7);
        System.out.println(v8);
    }

    public static void generateSpiralRing(
            Level level,
            int cx, int baseY, int cz,
            int radius,
            int thickness,
            int spiralHeight,
            int layerThickness,   // ★ 新增：螺旋层厚度
            Block block
    ) {
        int outerR = radius;
        int innerR = radius - thickness;

        for (int x = cx - outerR; x <= cx + outerR; x++) {
            for (int z = cz - outerR; z <= cz + outerR; z++) {

                double dx = (x + 0.5) - cx;
                double dz = (z + 0.5) - cz;
                double dist = Math.sqrt(dx * dx + dz * dz);

                // 圆滑的内外半径
                double outer = outerR + 0.5;
                double inner = innerR - 0.5;

                if (dist < inner || dist > outer) {
                    continue;
                }

                // 角度
                double angle = Math.atan2(dz, dx);

                // 映射到 0~1
                double progress = (angle + Math.PI) / (2 * Math.PI);

                // 当前螺旋层的基准高度
                int y = baseY + (int) Math.floor(progress * spiralHeight);

                // ★ 给螺旋“加厚”
                for (int dy = 0; dy < layerThickness; dy++) {
                    level.setBlock(new Vector3(x, y + dy, z), block, true, false);
                }
            }
        }
    }

    public static void buildCylinder(
            Level level,
            Vector3 center,
            int height,
            int radius,
            Block block
    ) {
        Set<Vector3> vector3s = new HashSet<>();

        int cx = center.getFloorX();
        int cy = center.getFloorY();
        int cz = center.getFloorZ();

        for (int y = 0; y < height; y++) {
            int worldY = cy + y;

            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    double dx = x - cx + 0.5;
                    double dz = z - cz + 0.5;
                    if (dx * dx + dz * dz <= radius * radius) {
                        vector3s.add(new Vector3(x, worldY, z));
                    }
                }
            }
        }
        BlockFillTask fillTask = new BlockFillTask(level, block, vector3s);
        GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
    }

    /**
     * 极坐标连续螺旋面
     */
    public static void createPolarSpiral(Position center, double minRadius, double maxRadius,
                                         double height, double turns, Block block) {

        Level level = center.level;

        // 角度和半径的双重循环
        int angleSteps = (int)(turns * 36); // 角度细分
        int radiusSteps = 5; // 半径细分

        for (int a = 0; a <= angleSteps; a++) {
            double angle = 2 * Math.PI * turns * a / angleSteps;

            for (int r = 0; r <= radiusSteps; r++) {
                double radius = minRadius + (maxRadius - minRadius) * r / radiusSteps;
                double y = center.y + height * a / angleSteps;

                // 让高度也随半径变化一点，形成扭曲效果
                y += radius * 0.1 * Math.sin(angle * 2);

                double x = center.x + radius * Math.cos(angle);
                double z = center.z + radius * Math.sin(angle);

                level.setBlock(new Vector3(
                        (int)Math.round(x),
                        (int)Math.round(y),
                        (int)Math.round(z)
                ), block);
            }
        }
    }

    /**
     * 简单螺旋上升生成器
     * @param center 中心位置
     * @param innerRadius 内圈半径
     * @param outerRadius 外圈半径
     * @param height 高度
     * @param turns 圈数
     * @param block 方块
     */
    public static void createSimpleSpiral(Position center, double innerRadius, double outerRadius,
                                          double height, double turns, Block block) {

        Level level = center.level;

        // 自动计算：内圈转得快，外圈转得慢
        double innerTurns = turns * 1.5;  // 内圈多转50%
        double outerTurns = turns * 0.75; // 外圈少转25%

        // 总点数
        int points = 100;

        Set<Vector3> vector3s = new HashSet<>();

        for (int i = 0; i <= points; i++) {
            double progress = (double)i / points;

            // 当前高度
            double y = center.y + height * progress;

            // 计算内外圈角度
            double innerAngle = 2 * Math.PI * innerTurns * progress;
            double outerAngle = 2 * Math.PI * outerTurns * progress;

            // 内外圈坐标
            double innerX = center.x + innerRadius * Math.cos(innerAngle);
            double innerZ = center.z + innerRadius * Math.sin(innerAngle);

            double outerX = center.x + outerRadius * Math.cos(outerAngle);
            double outerZ = center.z + outerRadius * Math.sin(outerAngle);

            // 放置方块
            vector3s.add(new Vector3((int)Math.round(innerX), (int)Math.round(y),
                    (int)Math.round(innerZ)));
            vector3s.add(new Vector3((int)Math.round(outerX), (int)Math.round(y),
                    (int)Math.round(outerZ)));
        }

        BlockFillTask fillTask = new BlockFillTask(level, block, vector3s);
        GameAPI.WORLDEDIT_THREAD_POOL_EXECUTOR.invoke(fillTask);
    }
}

package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import gameapi.GameAPI;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.utils.BlockMinVersionMap;
import gameapi.utils.NukkitTypeUtils;
import gameapi.utils.PosSet;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldEditScanNewBlockCommand extends EasySubCommand {

    private static final String REFERENCE_VERSION = "1.21.50";

    public WorldEditScanNewBlockCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!commandSender.isPlayer()) {
            return false;
        }
        Player player = asPlayer(commandSender);
        if (player == null) {
            return false;
        }
        if (WorldEditCommand.isTwoPosHasUndefined(player)) {
            return false;
        }
        PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
        CompletableFuture.runAsync(() -> {
            try {
                AxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                AtomicInteger total = new AtomicInteger();
                Map<String, Integer> blockCounts = new LinkedHashMap<>();
                Map<String, List<String>> blockPositions = new LinkedHashMap<>();

                bb.forEach((x, y, z) -> {
                    total.addAndGet(1);
                    Block block = player.getLevel().getBlock(x, y, z, true);
                    if (block == null) return;
                    String identifier = getBlockIdentifier(block);
                    if (identifier == null) return;

                    String namespaceId = identifier.contains(":") ? identifier.split(":")[1] : identifier;

                    String version = BlockMinVersionMap.VERSION_BY_BLOCK.get(namespaceId);
                    if (version != null && compareVersion(version, REFERENCE_VERSION) >= 0) {
                        blockCounts.merge(namespaceId, 1, Integer::sum);
                        blockPositions.computeIfAbsent(namespaceId, k -> new ArrayList<>())
                                .add(x + ":" + y + ":" + z);
                    }
                });

                Map<String, List<String>> byVersion = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : blockCounts.entrySet()) {
                    String version = BlockMinVersionMap.VERSION_BY_BLOCK.get(entry.getKey());
                    byVersion.computeIfAbsent(version == null ? "unknown" : version, k -> new ArrayList<>())
                            .add(entry.getKey() + " x" + entry.getValue());
                }

                StringBuilder msg = new StringBuilder();
                msg.append(TextFormat.GOLD).append("=== Block Scan Result ===\n");
                msg.append(TextFormat.WHITE).append("Total scanned: ").append(total.get()).append(" blocks\n");
                msg.append(TextFormat.WHITE).append("New blocks found (>= ").append(REFERENCE_VERSION).append("): ").append(blockCounts.values().stream().mapToInt(Integer::intValue).sum()).append("\n");

                if (blockCounts.isEmpty()) {
                    msg.append(TextFormat.GREEN).append("No blocks from ").append(REFERENCE_VERSION).append("+ found in this region.\n");
                } else {
                    msg.append(TextFormat.AQUA).append("--- By version ---\n");
                    for (Map.Entry<String, List<String>> versionEntry : byVersion.entrySet()) {
                        msg.append(TextFormat.YELLOW).append("[").append(versionEntry.getKey()).append("]\n");
                        for (String line : versionEntry.getValue()) {
                            msg.append(TextFormat.GRAY).append("  ").append(line).append("\n");
                        }
                    }
                }
                player.sendMessage(msg.toString());

                StringBuilder log = new StringBuilder();
                log.append("=== Block Scan Report ===\n");
                log.append("Player: ").append(player.getName()).append("\n");
                log.append("World: ").append(player.getLevel().getName()).append("\n");
                log.append("Region: pos1=").append(posSet.getPos1()).append(", pos2=").append(posSet.getPos2()).append("\n");
                log.append("Total scanned: ").append(total.get()).append(" blocks\n");
                log.append("Reference version: >= ").append(REFERENCE_VERSION).append("\n");
                log.append("New blocks found: ").append(blockCounts.values().stream().mapToInt(Integer::intValue).sum()).append("\n");
                log.append("Scan time: ").append(System.currentTimeMillis()).append("\n\n");

                if (!blockCounts.isEmpty()) {
                    log.append("--- Details by version ---\n");
                    for (Map.Entry<String, List<String>> versionEntry : byVersion.entrySet()) {
                        log.append("[").append(versionEntry.getKey()).append("]\n");
                        for (String summary : versionEntry.getValue()) {
                            log.append("  ").append(summary).append("\n");
                        }
                        log.append("\n");
                    }

                    log.append("--- Detailed coordinates ---\n");
                    for (Map.Entry<String, List<String>> posEntry : blockPositions.entrySet()) {
                        log.append("[").append(posEntry.getKey()).append("] (").append(posEntry.getValue().size()).append(" total)\n");
                        for (String coord : posEntry.getValue()) {
                            log.append("  ").append(coord).append("\n");
                        }
                        log.append("\n");
                    }
                } else {
                    log.append("No new blocks found.\n");
                }

                File dir = new File(GameAPI.getPath() + "/scan_block_logs/");
                dir.mkdirs();
                String fileName = player.getName() + "_" + System.currentTimeMillis();
                File logFile = dir.toPath().resolve(fileName + ".txt").toFile();
                Utils.writeFile(logFile, log.toString());
                player.sendMessage(TextFormat.GREEN + "Log saved: " + logFile.getAbsolutePath());

            } catch (Throwable t) {
                GameAPI.getGameDebugManager().printError(t);
                player.sendMessage(TextFormat.RED + "Error during scan: " + t.getMessage());
            }
        });
        return true;
    }

    private static int compareVersion(String a, String b) {
        String[] partsA = a.split("\\.");
        String[] partsB = b.split("\\.");
        int len = Math.max(partsA.length, partsB.length);
        for (int i = 0; i < len; i++) {
            int numA = i < partsA.length ? Integer.parseInt(partsA[i]) : 0;
            int numB = i < partsB.length ? Integer.parseInt(partsB[i]) : 0;
            if (numA != numB) return Integer.compare(numA, numB);
        }
        return 0;
    }

    private String getBlockIdentifier(Block block) {
        switch (NukkitTypeUtils.getNukkitType()) {
            case MOT:
            case POWER_NUKKIT_X_2:
                try {
                    return block.toItem().getNamespaceId();
                } catch (Exception e) {
                    return String.valueOf(block.getId());
                }
            default:
                return String.valueOf(block.getId());
        }
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}

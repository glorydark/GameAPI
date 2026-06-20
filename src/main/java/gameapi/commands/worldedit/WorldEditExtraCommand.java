package gameapi.commands.worldedit;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowCustom;
import gameapi.form.element.ResponsiveElementInput;

import java.util.Map;

public class WorldEditExtraCommand extends EasySubCommand {

    public WorldEditExtraCommand(String name) {
        super(name);
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = asPlayer(commandSender);
        if (player == null) return false;

        if (args.length == 0) {
            sendHelp(player);
            return false;
        }

        switch (args[0]) {
            case "add" -> handleAdd(player, args);
            case "remove" -> handleRemove(player, args);
            case "list" -> handleList(player);
            case "clear" -> handleClear(player);
            case "gui" -> handleGui(player);
            default -> sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(TextFormat.GOLD + "====== ExtraTag 管理 ======");
        player.sendMessage(TextFormat.YELLOW + "/worldedit extra add <key>" + TextFormat.GRAY + " 用当前位置添加标记点");
        player.sendMessage(TextFormat.YELLOW + "/worldedit extra remove <key>" + TextFormat.GRAY + " 删除标记点");
        player.sendMessage(TextFormat.YELLOW + "/worldedit extra list" + TextFormat.GRAY + " 列出所有标记点");
        player.sendMessage(TextFormat.YELLOW + "/worldedit extra clear" + TextFormat.GRAY + " 清空所有标记点");
        player.sendMessage(TextFormat.YELLOW + "/worldedit extra gui" + TextFormat.GRAY + " 打开图形界面");
    }

    private void handleAdd(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TextFormat.RED + "用法: /worldedit extra add <key>");
            return;
        }
        String key = args[1];
        Vector3 pos = player.floor();
        WorldEditCommand.extraTagCache.computeIfAbsent(player, k -> new java.util.LinkedHashMap<>()).put(key, pos);
        player.sendMessage(TextFormat.GREEN + "标记点 " + TextFormat.AQUA + key
                + TextFormat.GREEN + " 已添加 -> " + pos.getFloorX() + ", " + pos.getFloorY() + ", " + pos.getFloorZ());
    }

    private void handleRemove(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(TextFormat.RED + "用法: /worldedit extra remove <key>");
            return;
        }
        Map<String, Vector3> points = WorldEditCommand.extraTagCache.get(player);
        if (points == null || points.remove(args[1]) == null) {
            player.sendMessage(TextFormat.RED + "未找到标记点: " + args[1]);
        } else {
            player.sendMessage(TextFormat.GREEN + "已删除标记点: " + args[1]);
        }
    }

    private void handleList(Player player) {
        Map<String, Vector3> points = WorldEditCommand.extraTagCache.get(player);
        if (points == null || points.isEmpty()) {
            player.sendMessage(TextFormat.YELLOW + "暂无标记点");
            return;
        }
        player.sendMessage(TextFormat.GOLD + "====== 当前标记点 (" + points.size() + ") ======");
        for (Map.Entry<String, Vector3> entry : points.entrySet()) {
            Vector3 pos = entry.getValue();
            player.sendMessage(TextFormat.AQUA + entry.getKey()
                    + TextFormat.GRAY + " -> "
                    + TextFormat.WHITE + pos.getFloorX() + ", " + pos.getFloorY() + ", " + pos.getFloorZ());
        }
    }

    private void handleClear(Player player) {
        WorldEditCommand.clearExtraTagCache(player);
        player.sendMessage(TextFormat.GREEN + "已清空所有标记点");
    }

    private void handleGui(Player player) {
        Map<String, Vector3> points = WorldEditCommand.extraTagCache.get(player);

        Vector3 lapisPos = WorldEditCommand.pendingExtraPosition.remove(player);
        Vector3 recordPos = lapisPos != null ? lapisPos : player.floor();

        AdvancedFormWindowCustom form = new AdvancedFormWindowCustom("添加标记点");
        form.addElement(new ElementLabel("记录坐标: "
                + recordPos.getFloorX() + ", " + recordPos.getFloorY() + ", " + recordPos.getFloorZ()
                + (lapisPos != null ? " (青金石方块)" : " (你站的位置)")));

        ResponsiveElementInput keyInput = new ResponsiveElementInput("标记名称 (key)");
        form.addElement(keyInput);

        if (points != null && !points.isEmpty()) {
            StringBuilder sb = new StringBuilder("已有标记:\n");
            for (Map.Entry<String, Vector3> e : points.entrySet()) {
                sb.append(TextFormat.AQUA).append(e.getKey())
                        .append(TextFormat.GRAY).append(" -> ")
                        .append(e.getValue().getFloorX()).append(", ")
                        .append(e.getValue().getFloorY()).append(", ")
                        .append(e.getValue().getFloorZ()).append("\n");
            }
            form.addElement(new ElementLabel(sb.toString()));
        }

        Vector3 finalRecordPos = recordPos;
        keyInput.onRespond((p, key) -> {
            if (key == null || key.trim().isEmpty()) {
                p.sendMessage(TextFormat.RED + "标记名称不能为空");
                return;
            }
            WorldEditCommand.extraTagCache.computeIfAbsent(p, k -> new java.util.LinkedHashMap<>())
                    .put(key.trim(), finalRecordPos);
            p.sendMessage(TextFormat.GREEN + "标记点 " + TextFormat.AQUA + key.trim()
                    + TextFormat.GREEN + " 已添加");
        });

        form.onClose(p ->
                p.sendMessage(TextFormat.GRAY + "标记点管理已关闭，使用 /worldedit extra gui 重新打开")
        );

        form.showToPlayer(player);
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}

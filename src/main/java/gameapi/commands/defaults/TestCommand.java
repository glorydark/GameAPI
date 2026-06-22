package gameapi.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.ddui.CustomForm;
import cn.nukkit.ddui.DataDrivenScreen;
import cn.nukkit.ddui.Observable;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.WorldEditCommand;
import gameapi.commands.base.EasySubCommand;
import gameapi.extensions.projectileGun.ProjectileGunManager;
import gameapi.tools.StructureSplicer;
import gameapi.utils.PosSet;
import gameapi.utils.RotationType;

import java.io.File;

public class TestCommand extends EasySubCommand {

    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();

        switch (args[0]) {
            case "gun":
                player.getInventory().setItem(0, ProjectileGunManager.getProjectileGun("test").toWeaponItem(player));
                break;
            case "ddui":
                Observable<String> observable = new Observable<>("剩余时间: 10 s");
                CustomForm form = new CustomForm("答题界面")
                        .label(observable)
                        .label("test")
                        .button("1", p -> {})
                        .button("2", p -> {})
                        .button("3", p -> {})
                        .button("4", p -> {})
                        .button("close", player1 -> {
                            DataDrivenScreen.removeActiveScreen(player1);
                            player1.sendMessage("111");
                        })
                        .closeButton();
                Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.getInstance(), new Task() {
                    int countdown = 10;
                    @Override
                    public void onRun(int i) {
                        countdown--;
                        observable.setValue("剩余时间: " + countdown + " s");
                        if (countdown == 0) {
                            this.cancel();
                            form.close(player);
                        }
                    }
                }, 20);
                form.show(player);
                break;
            case "splice":
                handleSpliceGenerate(player);
                break;
        }
        return false;
    }

    private void handleSpliceGenerate(Player player) {
        PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(player);
        if (posSet == null || posSet.getPos1() == null) {
            player.sendMessage(TextFormat.RED + "请先设置点1 (放红石块)");
            return;
        }
        Vector3 pos1 = posSet.getPos1();

        File testDir = new File(GameAPI.getPath() + "/test");
        if (!testDir.isDirectory()) {
            player.sendMessage(TextFormat.RED + "未找到测试目录: " + testDir.getPath());
            return;
        }
        File hubFolder = new File(testDir, "hub");
        File courseFolder = new File(testDir, "course");
        if (!hubFolder.isDirectory() || !courseFolder.isDirectory()) {
            player.sendMessage(TextFormat.RED + "需要在 test/ 下有 hub/ 和 course/ 文件夹");
            return;
        }

        // 读取连接点 (抽象坐标)
        CompoundTag hubExtra = StructureSplicer.loadExtraData(hubFolder);
        CompoundTag courseExtra = StructureSplicer.loadExtraData(courseFolder);
        if (hubExtra == null || courseExtra == null) {
            player.sendMessage(TextFormat.RED + "读取 extra.nbt 失败");
            return;
        }

        Vector3 entry1 = getExtraPos(hubExtra, "entry1");
        Vector3 entry2 = getExtraPos(hubExtra, "entry2");
        Vector3 startPos = getExtraPos(courseExtra, "start_pos");
        Vector3 endPos = getExtraPos(courseExtra, "end_pos");
        if (entry1 == null || entry2 == null || startPos == null || endPos == null) {
            player.sendMessage(TextFormat.RED + "extra.nbt 缺少连接点");
            return;
        }

        // 1. pos1 是 Hub1 的 entry1 位置，反推 Hub1 原点
        Vector3 hubOrigin = new Vector3(
                pos1.getFloorX() - entry1.getFloorX(),
                pos1.getFloorY() - entry1.getFloorY(),
                pos1.getFloorZ() - entry1.getFloorZ()
        );
        StructureSplicer.placeBuild(player.getLevel(),
                new StructureSplicer.BuildPiece("hub", hubOrigin, 0, RotationType.NONE), hubFolder);
        player.sendMessage(TextFormat.GREEN + "Hub1 entry1=" + posStr(pos1) + "  origin=" + posStr(hubOrigin));

        // 2. Hub1.entry2 → Course.start_pos
        Vector3 entry2World = hubOrigin.add(entry2);
        Vector3 courseOrigin = new Vector3(
                entry2World.getFloorX() - startPos.getFloorX(),
                entry2World.getFloorY() - startPos.getFloorY(),
                entry2World.getFloorZ() - startPos.getFloorZ()
        );
        StructureSplicer.placeBuild(player.getLevel(),
                new StructureSplicer.BuildPiece("course", courseOrigin, 0, RotationType.NONE), courseFolder);
        player.sendMessage(TextFormat.GREEN + "Course 已放置于 " + posStr(courseOrigin));

        // 3. Course.end_pos → Hub2.entry1
        Vector3 endWorld = courseOrigin.add(endPos);
        Vector3 hub2Origin = new Vector3(
                endWorld.getFloorX() - entry1.getFloorX(),
                endWorld.getFloorY() - entry1.getFloorY(),
                endWorld.getFloorZ() - entry1.getFloorZ()
        );
        StructureSplicer.placeBuild(player.getLevel(),
                new StructureSplicer.BuildPiece("hub", hub2Origin, 0, RotationType.NONE), hubFolder);
        player.sendMessage(TextFormat.GREEN + "Hub2 已放置于 " + posStr(hub2Origin));

        // 验证
        player.sendMessage("  hub1 entry1→world: " + posStr(hubOrigin.add(entry1)) + " (应=pos1)");
        player.sendMessage("  entry2→start_pos: " + posStr(entry2World) + " → " + posStr(courseOrigin.add(startPos)));
        player.sendMessage("  end_pos→entry1:   " + posStr(endWorld) + " → " + posStr(hub2Origin.add(entry1)));
    }

    private Vector3 getExtraPos(CompoundTag extra, String key) {
        if (extra == null || !extra.contains(key)) return null;
        ListTag<IntTag> list = extra.getList(key, IntTag.class);
        if (list.size() < 3) return null;
        return new Vector3(list.get(0).getData(), list.get(1).getData(), list.get(2).getData());
    }

    private String posStr(Vector3 v) {
        if (v == null) return "null";
        return v.getFloorX() + "," + v.getFloorY() + "," + v.getFloorZ();
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
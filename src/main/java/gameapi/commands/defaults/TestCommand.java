package gameapi.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.ddui.CustomForm;
import cn.nukkit.ddui.DataDrivenScreen;
import cn.nukkit.ddui.Observable;
import cn.nukkit.scheduler.Task;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.extensions.projectileGun.ProjectileGunManager;

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
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
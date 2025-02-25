package gameapi.commands.defaults.manage;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.tools.RandomTools;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 */
public class SudoActionCommand extends EasySubCommand {

    public SudoActionCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = Server.getInstance().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(TextFormat.RED + "找不到玩家！");
            return false;
        }
        if (commandSender.isPlayer()) {
            AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("快捷整蛊");
            simple.addButton(
                    new ResponsiveElementButton("假装/取消卡顿")
                            .onRespond(player1 -> player.setImmobile(!player.isImmobile()))
            );
            simple.addButton(
                    new ResponsiveElementButton("闪电降临")
                            .onRespond(player1 -> {
                                Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.getInstance(), new Task() {

                                    int missTimes = 0;

                                    @Override
                                    public void onRun(int i) {
                                        EntityLightning entityLightning = new EntityLightning(player.getChunk(), Entity.getDefaultNBT(player));
                                        if (this.missTimes >= 4) {
                                            boolean hit = ThreadLocalRandom.current().nextBoolean();
                                            if (!hit) {
                                                this.missTimes += 1;
                                                entityLightning.setPosition(player.add(RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5)));
                                            } else {
                                                this.cancel();
                                            }
                                        } else {
                                            this.missTimes += 1;
                                            entityLightning.setPosition(player.add(RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5)));
                                        }
                                        entityLightning.spawnToAll();
                                    }
                                }, 40);
                            })
            );
            simple.addButton(
                    new ResponsiveElementButton("着火").onRespond(player1 -> player.setOnFire(3))
            );
            simple.addButton(
                    new ResponsiveElementButton("疾跑").onRespond(player1 -> player.setSprinting(true))
            );
            simple.addButton(
                    new ResponsiveElementButton("蹲下").onRespond(player1 -> player.setSneaking(true))
            );
            simple.showToPlayer(commandSender.asPlayer());
        } else {
            switch (args[1]) {
                case "lag":
                    player.setImmobile(!player.isImmobile());
                    break;
                case "lightning":
                    Server.getInstance().getScheduler().scheduleRepeatingTask(GameAPI.getInstance(), new Task() {

                        int missTimes = 0;

                        @Override
                        public void onRun(int i) {
                            EntityLightning entityLightning = new EntityLightning(player.getChunk(), Entity.getDefaultNBT(player));
                            if (this.missTimes >= 4) {
                                boolean hit = ThreadLocalRandom.current().nextBoolean();
                                if (!hit) {
                                    this.missTimes += 1;
                                    entityLightning.setPosition(player.add(RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5)));
                                } else {
                                    this.cancel();
                                }
                            } else {
                                this.missTimes += 1;
                                entityLightning.setPosition(player.add(RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5), RandomTools.getRandom(-5, 5)));
                            }
                            entityLightning.spawnToAll();
                        }
                    }, 40);
                    break;
                case "fire":
                    player.setOnFire(3);
                    break;
                case "sprint":
                    player.setSprinting(true);
                    break;
                case "sneak":
                    player.setSneaking(true);
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

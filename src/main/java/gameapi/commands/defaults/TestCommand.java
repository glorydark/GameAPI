package gameapi.commands.defaults;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.extension.ParticleGunManager;

/**
 * @author glorydark
 */
public class TestCommand extends EasySubCommand {

    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        /*
        Player player = commandSender.asPlayer();
        AnimatePacket pk = new AnimatePacket();
        pk.eid = player.getId();
        pk.action = AnimatePacket.Action.SWING_ARM;
        pk.rowingTime = 0;
        Server.broadcastPacket(player.getViewers().values(), pk);
         */
        commandSender.asPlayer().getInventory().addItem(ParticleGunManager.getParticleGun("gun:ak47").getItem());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

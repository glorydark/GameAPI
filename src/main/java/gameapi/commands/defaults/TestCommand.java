package gameapi.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.ClientboundDataDrivenUIShowScreenPacket;
import gameapi.commands.base.EasySubCommand;
import gameapi.extensions.projectileGun.ProjectileGunManager;

/**
 * @author glorydark
 */
public class TestCommand extends EasySubCommand {

    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        /*
        AnimatePacket pk = new AnimatePacket();
        pk.eid = player.getId();
        pk.action = AnimatePacket.Action.SWING_ARM;
        pk.rowingTime = 0;
        Server.broadcastPacket(player.getViewers().values(), pk);
         */
        // PlayerTools.showOnScreenTextureAnimation(commandSender.asPlayer(), Integer.parseInt(args[0]));
        // Player player = commandSender.asPlayer();
        // player.setItemCoolDown(Integer.parseInt(args[1]), args[0]);

        // commandSender.asPlayer().getInventory().addItem(ParticleGunManager.getParticleGun("gun:ak47").getItem());

        switch (args[0]) {
            case "gun":
                player.getInventory().setItem(0, ProjectileGunManager.getProjectileGun("test").toWeaponItem(player));
                break;
            case "ddui":
                // @since v26.10
                ClientboundDataDrivenUIShowScreenPacket pk = new ClientboundDataDrivenUIShowScreenPacket();
                pk.screenId = args[1];
                player.dataPacket(pk);
                break;
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

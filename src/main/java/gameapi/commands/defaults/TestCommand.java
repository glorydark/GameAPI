package gameapi.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import gameapi.commands.base.EasySubCommand;
import gameapi.extensions.projectileGun.ProjectileGun;
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

        ProjectileGunManager.registerProjectileGun(
                new ProjectileGun("test", "test", 10, 1.5, 60, 1, 2f) {

                    @Override
                    public Item getItem(Player player) {
                        return Item.get(ItemID.IRON_SWORD);
                    }
        });

        player.getInventory().setItem(0, ProjectileGunManager.getProjectileGun("test").toWeaponItem(player));
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

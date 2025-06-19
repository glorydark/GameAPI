package gameapi.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.extensions.particleGun.WeaponManager;

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
        player.getInventory().addItem(WeaponManager.REGISTERED_WEAPONS.get("test").getItem());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

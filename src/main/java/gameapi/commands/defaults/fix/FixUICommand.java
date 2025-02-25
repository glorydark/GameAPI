package gameapi.commands.defaults.fix;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class FixUICommand extends EasySubCommand {

    public FixUICommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (player != null) {
            player.addWindow(player.getInventory(), 0, true, true);
            player.addWindow(player.getUIInventory(), 124, true);
            player.addWindow(player.getOffhandInventory(), 119, true, true);
            player.setCraftingGrid(player.getUIInventory().getCraftingGrid());
            player.addWindow(player.getCraftingGrid(), -1);
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}

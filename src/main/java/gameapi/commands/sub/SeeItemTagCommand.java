package gameapi.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class SeeItemTagCommand extends EasySubCommand {

    public SeeItemTagCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        Item item = player.getInventory().getItemInHand();
        commandSender.sendMessage("物品Tag如下\n" + item.getOrCreateNamedTag().toString());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp() && commandSender.isPlayer();
    }
}

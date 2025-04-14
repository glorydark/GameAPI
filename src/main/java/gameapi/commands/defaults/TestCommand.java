package gameapi.commands.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedChestForm;
import gameapi.form.AdvancedDoubleChestForm;
import gameapi.form.element.ResponsiveElementSlotItem;

/**
 * @author glorydark
 */
public class TestCommand extends EasySubCommand {

    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        AdvancedDoubleChestForm chestForm = new AdvancedDoubleChestForm("233");
        chestForm.addItem(1, new ResponsiveElementSlotItem(Item.get(10)));
        chestForm.showToPlayer(commandSender.asPlayer());
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

package gameapi.commands.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.property.BooleanEntityProperty;
import cn.nukkit.entity.data.property.EntityProperty;
import gameapi.annotation.Experimental;
import gameapi.commands.base.EasySubCommand;

@Experimental
public class ImprovedEntityPropertyCommand extends EasySubCommand {

    public ImprovedEntityPropertyCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender.isPlayer()) {
            sender.asPlayer().setBooleanEntityProperty("minecraft:armor_transparent", Boolean.parseBoolean(args[0]));
        } else {
            EntityProperty.register(
                    "minecraft:player",
                    new BooleanEntityProperty("minecraft:armor_transparent", true)
            );
            EntityProperty.buildPacket();
            EntityProperty.buildPlayerProperty();
            sender.sendMessage(EntityProperty.getPacketCache().toString());
            sender.sendMessage(EntityProperty.getPlayerPropertyCache().toString());
        }
        return true;
    }
}
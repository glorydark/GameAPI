package gameapi.commands.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Utils;
import gameapi.Test;
import gameapi.commands.base.EasySubCommand;
import gameapi.extensions.particleGun.WeaponManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author glorydark
 */
public class TestCommand extends EasySubCommand {

    public TestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        List<String> strings = new ArrayList<>();
        for (Map.Entry<String, Supplier<Item>> stringSupplierEntry : Item.NAMESPACED_ID_ITEM.entrySet()) {
            String id = stringSupplierEntry.getKey();
            if (id.startsWith("monsterworld")) {
                continue;
            }
            strings.add(id);
        }
        try {
            Utils.writeFile(new File("E:\\item2icon\\string_item.txt"), String.join("\n", strings));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
         Player player = commandSender.asPlayer();
        player.getInventory().addItem(WeaponManager.REGISTERED_WEAPONS.get("test").getItem());
         */
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

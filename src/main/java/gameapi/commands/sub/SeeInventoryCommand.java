package gameapi.commands.sub;

import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.commands.base.EasySubCommand;

import java.util.Map;

/**
 * @author glorydark
 */
public class SeeInventoryCommand extends EasySubCommand {

    public SeeInventoryCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 1) {
            if (commandSender.isOp()) {
                if (Server.getInstance().lookupName(args[0]).isPresent()) {
                    Player player1 = Server.getInstance().getPlayer(args[0]);
                    if (player1 != null) {
                        StringBuilder builder = new StringBuilder();
                        for (Map.Entry<Integer, Item> entry : player1.getInventory().getContents().entrySet()) {
                            Item item = entry.getValue();
                            if (item.getId() != 0) {
                                builder.append("[").append(entry.getKey()).append("]")
                                        .append(item.getName()).append("*").append(item.getCount());
                                if (item.hasCompoundTag() && item.getNamedTag().contains("gems")) {
                                    builder.append(", gems: ").append(item.getNamedTag().getString("gems"));
                                }
                                builder.append("\n");
                            }
                        }
                        commandSender.sendMessage("玩家物品如下: \n" + builder);
                    } else {
                        OfflinePlayer offlinePlayer = (OfflinePlayer) Server.getInstance().getOfflinePlayer(args[0]);
                        if (offlinePlayer != null) {
                            StringBuilder builder = new StringBuilder();
                            CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayer.getUniqueId(), false);
                            ListTag<CompoundTag> inventory = namedTag.getList("Inventory", CompoundTag.class);
                            for (int i = 0; i < inventory.size(); i++) {
                                CompoundTag testTag = inventory.get(i);
                                Item item = NBTIO.getItemHelper(testTag);
                                if (item.getId() != 0) {
                                    builder.append("[").append(i).append("]")
                                            .append(item.getName()).append("*").append(item.getCount());
                                    if (item.hasCompoundTag() && item.getNamedTag().contains("gems")) {
                                        builder.append(", gems: ").append(item.getNamedTag().getString("gems"));
                                    }
                                    builder.append("\n");
                                }
                            }
                            commandSender.sendMessage("玩家物品如下: \n" + builder);
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

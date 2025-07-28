package gameapi.commands.defaults.dev;

import cn.nukkit.OfflinePlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

import java.util.Map;
import java.util.Optional;

/**
 * @author glorydark
 */
public class SeeEnderChestCommand extends EasySubCommand {

    public SeeEnderChestCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 1) {
            Optional<Player> seePlayer = Optional.ofNullable(Server.getInstance().getPlayer(args[0]));
            if (seePlayer.isPresent()) {
                commandSender.sendMessage("该玩家末影箱物品如下: ");
                for (Map.Entry<Integer, Item> entry : seePlayer.get().getEnderChestInventory().slots.entrySet()) {
                    commandSender.sendMessage(entry.getKey() + ": " + entry.getValue().getName() + " * " + entry.getValue().getCount());
                }
            } else {
                OfflinePlayer offlinePlayer = (OfflinePlayer) Server.getInstance().getOfflinePlayer(args[0]);
                if (offlinePlayer != null) {
                    commandSender.sendMessage("该玩家末影箱物品如下: ");
                    CompoundTag namedTag = Server.getInstance().getOfflinePlayerData(offlinePlayer.getUniqueId(), false);
                    ;

                    if (namedTag.contains("EnderItems") && namedTag.get("EnderItems") instanceof ListTag) {
                        ListTag<CompoundTag> inventoryList = namedTag.getList("EnderItems", CompoundTag.class);
                        for (CompoundTag compoundTag : inventoryList.getAll()) {
                            Item item = NBTIO.getItemHelper(compoundTag);
                            commandSender.sendMessage(compoundTag.getByte("Slot") + ":" + item.getName() + "*" + item.getCount());
                        }
                    }
                } else {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_uuid.player_not_found", args[0]));
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

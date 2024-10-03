package gameapi.commands.sub;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

import java.util.Optional;
import java.util.UUID;

/**
 * @author glorydark
 */
public class SeeUUIDCommand extends EasySubCommand {

    public SeeUUIDCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length == 1) {
            String playerName = args[0];
            Player seePlayer = Server.getInstance().getPlayer(playerName);
            if (seePlayer != null) {
                GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("command.see_uuid.success", playerName, seePlayer.getUniqueId().toString()));
            } else {
                Optional<UUID> offlineUUID = Server.getInstance().lookupName(playerName);
                if (offlineUUID.isPresent()) {
                    IPlayer seePlayerOffline = Server.getInstance().getOfflinePlayer(offlineUUID.get());
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_uuid.success", playerName, seePlayerOffline.getUniqueId().toString()));
                } else {
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_uuid.player_not_found", playerName));
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

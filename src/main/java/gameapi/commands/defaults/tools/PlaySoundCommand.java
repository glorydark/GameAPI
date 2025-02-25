package gameapi.commands.defaults.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.SoundTools;

/**
 * @author glorydark
 */
public class PlaySoundCommand extends EasySubCommand {

    public PlaySoundCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length > 1) {
            Player player = Server.getInstance().getPlayer(args[0]);
            if (player != null) {
                float volume = 1;
                float pitch = 1;
                if (args.length > 2) {
                    volume = Float.parseFloat(args[2]);
                    if (args.length > 3) {
                        pitch = Float.parseFloat(args[3]);
                    }
                }
                SoundTools.addSoundToPlayer(player, args[1], volume, pitch);
            } else {
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_not_found", args[0]));
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;

/**
 * @author Glorydark
 * For in-game test
 */
public class PlayerCommands extends Command {
    public PlayerCommands(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isPlayer()) {
            if (strings.length > 0) {
                switch (strings[0].toLowerCase()) {
                    case "setlang":
                        if(strings.length == 2) {
                            GameAPI.getLanguage().setPlayerPreferLanguage((Player) commandSender, strings[1]);
                        }
                        break;
                }
            }
        }
        return true;
    }
}

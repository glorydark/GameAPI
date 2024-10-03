package gameapi.commands.sub;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * @author glorydark
 */
public class PlayerEverCommand extends EasySubCommand {

    public PlayerEverCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        CompletableFuture.runAsync(() -> {
            int count = 0;
            try {
                File[] files = new File(Server.getInstance().getDataPath() + "players/").listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        if (name.endsWith(".dat") && !name.endsWith(".bak.dat")) {
                            count++;
                        }
                    }
                }
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.player_ever.success", count));
            } catch (Exception ignore) {
                commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.player_ever.no_access"));
            }
        });
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

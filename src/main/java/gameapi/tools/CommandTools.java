package gameapi.tools;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;

import java.util.Arrays;

/**
 * @author glorydark
 */
public class CommandTools {

    /**
     * This aims at sending command through server's primary thread to avoid executing it with async errors.
     */
    public static void dispatchCommand(CommandSender sender, String cmd) {
        try {
            if (Server.getInstance().isPrimaryThread()) {
                Server.getInstance().dispatchCommand(sender, cmd);
            } else {
                Server.getInstance().getScheduler().scheduleTask(GameAPI.getInstance(), () -> Server.getInstance().dispatchCommand(sender, cmd), false);
            }
        } catch (Throwable t) {
            GameAPI.getGameDebugManager().printError(t);
        }
    }
}

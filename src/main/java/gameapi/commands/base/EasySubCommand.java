package gameapi.commands.base;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import gameapi.utils.NukkitTypeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public abstract class EasySubCommand {

    public final Map<String, CommandParameter[]> commandParameters = new LinkedHashMap<>();
    private final String name;

    public EasySubCommand(String name) {
        this.name = name;
    }

    public abstract boolean execute(CommandSender commandSender, String s, String[] args);

    public boolean hasPermission(CommandSender commandSender) {
        return true;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, CommandParameter[]> getCommandParameters() {
        return commandParameters;
    }

    @Nullable
    public Player asPlayer(CommandSender commandSender) {
        Player player;
        if (NukkitTypeUtils.getNukkitType() == NukkitTypeUtils.NukkitType.MOT) {
            player = commandSender.asPlayer();
        } else {
            if (commandSender.isPlayer()) {
                player = (Player) commandSender;
            } else {
                player = null;
            }
        }
        return player;
    }
}
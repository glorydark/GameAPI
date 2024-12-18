package gameapi.commands.base;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public abstract class EasySubCommand {

    private final String name;

    public final Map<String, CommandParameter[]> commandParameters = new LinkedHashMap<>();

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
}

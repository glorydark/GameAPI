package gameapi.commands.base;

import cn.nukkit.command.CommandSender;

/**
 * @author glorydark
 */
public abstract class EasySubCommand {

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
}

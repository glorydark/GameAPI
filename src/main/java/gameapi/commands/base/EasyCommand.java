package gameapi.commands.base;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.*;

/**
 * @author glorydark
 */
public class EasyCommand extends Command {

    protected Map<String, EasySubCommand> easySubCommandMap = new LinkedHashMap<>();

    protected EasySubCommand baseCommand = null;

    public EasyCommand(String name) {
        super(name);
    }

    public void registerCommand(EasySubCommand easySubCommand) {
        this.easySubCommandMap.put(easySubCommand.getName(), easySubCommand);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length == 0) {
            if (this.baseCommand != null) {
                this.baseCommand.execute(commandSender, s, strings);
            }
        } else {
            String subCommand = strings[0];
            EasySubCommand easySubCommand = this.easySubCommandMap.get(subCommand);
            if (easySubCommand != null && easySubCommand.hasPermission(commandSender)) {
                List<String> params = new ArrayList<>(Arrays.asList(strings));
                params.remove(0);
                return easySubCommand.execute(commandSender, s, params.toArray(new String[0]));
            }
        }
        return false;
    }
}
package gameapi.commands.base;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author glorydark
 */
public class EasyCommand extends Command {

    protected Map<String, EasySubCommand> easySubCommandMap = new LinkedHashMap<>();

    public EasyCommand(String name) {
        super(name);
        this.getCommandParameters().clear();
    }

    public void registerCommand(EasySubCommand easySubCommand) {
        this.easySubCommandMap.put(easySubCommand.getName(), easySubCommand);
        for (Map.Entry<String, CommandParameter[]> entry : easySubCommand.getCommandParameters().entrySet()) {
            String name = this.getName() + "-" + easySubCommand.getName();
            List<CommandParameter> commandParameterList = Arrays.stream(entry.getValue()).collect(Collectors.toList());
            commandParameterList.add(0, CommandParameter.newEnum(easySubCommand.getName(), false, new String[]{easySubCommand.getName()}));
            this.getCommandParameters().put(name, commandParameterList.toArray(new CommandParameter[0]));
        }
    }

    public boolean preExecute(CommandSender commandSender, String s, String[] strings) {
        return true;
    }

    public void onExecuteDefault(CommandSender commandSender) {

    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!this.hasPermission(commandSender)) {
            return false;
        }
        if (!this.preExecute(commandSender, s, strings)) {
            return false;
        }
        if (strings.length == 0) {
            this.onExecuteDefault(commandSender);
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

    public boolean hasPermission(CommandSender commandSender) {
        return true;
    }

    @Override
    public boolean testPermission(CommandSender target) {
        return this.hasPermission(target) && super.testPermission(target);
    }

    @Override
    public boolean testPermissionSilent(CommandSender target) {
        return this.hasPermission(target) && super.testPermissionSilent(target);
    }
}

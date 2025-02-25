package gameapi.commands.defaults.ranking;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.data.PlayerGameDataManager;

/**
 * @author glorydark
 */
public class RankingRemoveAllCommand extends EasySubCommand {

    public RankingRemoveAllCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        String name = args[0];
        if (Server.getInstance().lookupName(name).isPresent()) {
            PlayerGameDataManager.removeAllGameData(name);
            commandSender.sendMessage(TextFormat.GREEN + "已成功移除玩家 " + name + " 所有的游戏记录！");
        } else {
            commandSender.sendMessage(TextFormat.RED + "Cannot find player!");
        }
        return false;
    }
}

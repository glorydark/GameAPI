package gameapi.commands.sub.ranking;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.manager.data.PlayerGameDataManager;
import gameapi.manager.tools.GameEntityManager;
import gameapi.ranking.Ranking;
import gameapi.ranking.simple.SimpleRanking;

import java.util.*;

/**
 * @author glorydark
 */
public class FunctionRankingRemoveAllCommand extends EasySubCommand {

    public FunctionRankingRemoveAllCommand(String name) {
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

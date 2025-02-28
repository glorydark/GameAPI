package gameapi.commands.defaults.ranking;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

import java.util.Map;

/**
 * @author glorydark
 */
public class RankingInfoCommand extends EasySubCommand {

    public RankingInfoCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        for (Map.Entry<String, Ranking> entry : RankingManager.getRankingFactory().entrySet()) {
            Ranking ranking = entry.getValue();
            ranking.refreshRankingData();
            commandSender.sendMessage(entry.getKey());
            commandSender.sendMessage(ranking.getDisplayContent(true) + "\n");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

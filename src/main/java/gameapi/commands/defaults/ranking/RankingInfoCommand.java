package gameapi.commands.defaults.ranking;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

/**
 * @author glorydark
 */
public class RankingInfoCommand extends EasySubCommand {

    public RankingInfoCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        for (Ranking ranking : RankingManager.getRankingFactory().values()) {
            commandSender.sendMessage(ranking.getDisplayContent() + "\n");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

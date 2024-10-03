package gameapi.commands.sub;

import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.tools.GameEntityManager;
import gameapi.ranking.Ranking;

/**
 * @author glorydark
 */
public class RankInfoCommand extends EasySubCommand {

    public RankInfoCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        for (Ranking ranking : GameEntityManager.rankingFactory.values()) {
            commandSender.sendMessage(ranking.getDisplayContent() + "\n");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

package gameapi.commands.defaults.ranking;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

/**
 * @author glorydark
 */
public class RankingAddCommand extends EasySubCommand {

    public RankingAddCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (args.length >= 5) {
            Player player = (Player) commandSender;
            RankingManager.addRankingList(player, args[0], args[1], args[2], args[3], Ranking.getRankingSortSequence(args[4]));
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer() && commandSender.isOp();
    }
}

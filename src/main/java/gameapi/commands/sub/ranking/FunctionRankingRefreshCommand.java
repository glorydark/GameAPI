package gameapi.commands.sub.ranking;

import cn.nukkit.command.CommandSender;
import gameapi.GameAPI;
import gameapi.commands.base.EasySubCommand;
import gameapi.manager.tools.GameEntityManager;

/**
 * @author glorydark
 */
public class FunctionRankingRefreshCommand extends EasySubCommand {

    public FunctionRankingRefreshCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        GameEntityManager.closeAll();
        GameAPI.getInstance().loadAllPlayerGameData();
        GameAPI.getInstance().loadRanking();
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}
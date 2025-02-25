package gameapi.commands.defaults.ranking;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import gameapi.commands.base.EasySubCommand;
import gameapi.form.AdvancedFormWindowSimple;
import gameapi.form.element.ResponsiveElementButton;
import gameapi.manager.data.RankingManager;
import gameapi.ranking.Ranking;

import java.util.Map;

/**
 * @author glorydark
 */
public class RankingUICommand extends EasySubCommand {

    public RankingUICommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (args.length == 0) {
            AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple("排行榜列表");
            for (Map.Entry<String, Ranking> entry : RankingManager.getRankingFactory().entrySet()) {
                simple.addButton(
                        new ResponsiveElementButton(entry.getValue().getTitle())
                                .onRespond(player1 -> Server.getInstance().dispatchCommand(player1, "gameapi rankui " + entry.getKey()))
                );
            }
            simple.showToPlayer(player);
        } else {
            Ranking ranking = RankingManager.getRankingFactory().get(args[0]);
            if (ranking != null) {
                ranking.refreshRankingData();
                AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(ranking.getTitle());
                simple.setContent(ranking.getDisplayContent(true));
                simple.showToPlayer(player);
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}

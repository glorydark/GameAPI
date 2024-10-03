package gameapi.commands.sub.ranking;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import gameapi.commands.base.EasySubCommand;
import gameapi.entity.TextEntity;
import gameapi.manager.tools.GameEntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author glorydark
 */
public class FunctionRankingCheckCommand extends EasySubCommand {

    public FunctionRankingCheckCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        int existCount = GameEntityManager.textEntityDataList.size();
        commandSender.sendMessage(existCount + " floating text are in the list");
        for (Level level : Server.getInstance().getLevels().values()) {
            int count = 0;
            for (Entity entity : level.getEntities()) {
                if (entity instanceof TextEntity) {
                    List<String> r = new ArrayList<>();
                    for (Player value : entity.getViewers().values()) {
                        r.add(value.getName());
                    }
                    commandSender.sendMessage("Level: " + level.getName() + " | viewers: " +
                            r
                            + " | " + entity.getNameTag().split("\n")[0]);
                    count++;
                }
            }
            commandSender.sendMessage("Level " + level.getName() + " found " + count + " entities.");
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

package gameapi.commands;

import gameapi.commands.base.EasyCommand;
import gameapi.commands.sub.*;
import gameapi.commands.sub.achievement.FunctionAchievementCommand;
import gameapi.commands.sub.ranking.FunctionRankingAddCommand;
import gameapi.commands.sub.ranking.FunctionRankingCheckCommand;
import gameapi.commands.sub.ranking.FunctionRankingRefreshCommand;

/**
 * @author glorydark
 */
public class GameAPICommandMain extends EasyCommand {

    public GameAPICommandMain(String name) {
        super(name);
        // base
        this.registerCommand(new DebugCommand("debug"));
        this.registerCommand(new QuitCommand("quit"));
        this.registerCommand(new RoomKickCommand("kick"));
        this.registerCommand(new RoomSetPwdCommand("setpwd"));
        this.registerCommand(new RoomStartCommand("roomstart"));
        this.registerCommand(new StatusCommand("status"));

        // player tools
        this.registerCommand(new PlayerEverCommand("playerever"));
        this.registerCommand(new SeeNameCommand("seename"));
        this.registerCommand(new SeeUUIDCommand("seeuuid"));

        // smart tools
        this.registerCommand(new GetChestPosCommand("getchestpos"));
        this.registerCommand(new PlaySoundCommand("playsound"));
        this.registerCommand(new SaveSkinCommand("saveskin"));

        // ranking system
        this.registerCommand(new FunctionRankingAddCommand("addrank"));
        this.registerCommand(new FunctionRankingCheckCommand("checkrank"));
        this.registerCommand(new RankInfoCommand("rankinfo"));
        this.registerCommand(new FunctionRankingRefreshCommand("refreshrank"));

        // other functions
        this.registerCommand(new FunctionActivityCommand("activity"));
        this.registerCommand(new FunctionAchievementCommand("achievement"));
    }
}

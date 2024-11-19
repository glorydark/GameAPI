package gameapi.commands;

import gameapi.commands.base.EasyCommand;
import gameapi.commands.sub.*;
import gameapi.commands.sub.achievement.FunctionAchievementCommand;
import gameapi.commands.sub.achievement.FunctionAchievementGiveCommand;
import gameapi.commands.sub.achievement.FunctionAchievementReloadCommand;
import gameapi.commands.sub.ranking.FunctionRankingAddCommand;
import gameapi.commands.sub.ranking.FunctionRankingCheckCommand;
import gameapi.commands.sub.ranking.FunctionRankingInfoCommand;
import gameapi.commands.sub.ranking.FunctionRankingRefreshCommand;
import gameapi.commands.sub.test.FunctionNewActivityCommand;

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
        this.registerCommand(new ResetSpeedCommand("resetspeed"));

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
        this.registerCommand(new FunctionRankingInfoCommand("rankinfo"));
        this.registerCommand(new FunctionRankingRefreshCommand("refreshrank"));

        // other functions
        this.registerCommand(new FunctionActivityCommand("activity"));
        this.registerCommand(new FunctionAchievementCommand("achievement"));
        this.registerCommand(new FunctionAchievementReloadCommand("reloadachievement"));
        this.registerCommand(new FunctionAchievementGiveCommand("giveachievement"));

        this.registerCommand(new FunctionNewActivityCommand("newac"));
        this.registerCommand(new FastCommand("fast"));
        this.registerCommand(new SudoCCommand("sudoc"));
        this.registerCommand(new SudoMCommand("sudom"));
        this.registerCommand(new TeleportIntoRoomCommand("tproomall"));
        this.registerCommand(new TeleportAllCommand("tpall"));
    }
}

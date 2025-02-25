package gameapi.commands;

import gameapi.GameAPI;
import gameapi.commands.base.EasyCommand;
import gameapi.commands.sub.*;
import gameapi.commands.sub.achievement.FunctionAchievementCommand;
import gameapi.commands.sub.achievement.FunctionAchievementGiveCommand;
import gameapi.commands.sub.achievement.FunctionAchievementReloadCommand;
import gameapi.commands.sub.ranking.*;
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

        // player tools
        this.registerCommand(new PlayerEverCommand("playerever"));
        this.registerCommand(new SeeNameCommand("seename"));
        this.registerCommand(new SeeUUIDCommand("seeuuid"));
        this.registerCommand(new MoveDataCommand("movedata"));
        this.registerCommand(new SeeEnderChestCommand("enderchest"));
        this.registerCommand(new SeeInventoryCommand("seeinv"));

        // smart tools
        this.registerCommand(new GetChestPosCommand("getchestpos"));
        this.registerCommand(new PlaySoundCommand("playsound"));
        this.registerCommand(new SaveSkinCommand("saveskin"));

        // ranking system
        this.registerCommand(new FunctionRankingAddCommand("addrank"));
        this.registerCommand(new FunctionRankingCheckCommand("checkrank"));
        this.registerCommand(new FunctionRankingInfoCommand("rankinfo"));
        this.registerCommand(new FunctionRankingRefreshCommand("refreshrank"));
        this.registerCommand(new FunctionRankingRemoveAllCommand("removerank"));

        // other functions
        // this.registerCommand(new FunctionActivityCommand("activity"));
        this.registerCommand(new FunctionAchievementCommand("achievement"));
        this.registerCommand(new FunctionAchievementReloadCommand("reloadachievement"));
        this.registerCommand(new FunctionAchievementGiveCommand("giveachievement"));

        this.registerCommand(new GetFirstPlayedCommand("firstplay"));

        this.registerCommand(new SudoCCommand("sudoc"));
        this.registerCommand(new SudoMCommand("sudom"));
        this.registerCommand(new ResetSpeedCommand("resetspeed"));

        this.registerCommand(new ReloadChunkCommand("rchunk"));

        if (GameAPI.getInstance().isGlorydarkRelatedFeature()) {
            this.registerCommand(new FixUICommand("fixui"));
            this.registerCommand(new FunctionRankingMonthlyConclusionCommand("monthrank"));
            this.registerCommand(new FunctionWardenCommand("shenquan"));
            this.registerCommand(new FastCommand("fast"));
            this.registerCommand(new SudoActionCommand("sudoact"));
            this.registerCommand(new ChangeToSnowBiome("snow"));
            this.registerCommand(new GetBlockPosCommand("blockpos"));
            this.registerCommand(new GetLoginChainCommand("loginchain"));
        }

        this.registerCommand(new FunctionNewActivityCommand("activity"));

        this.registerCommand(new TeleportIntoRoomCommand("tproomall"));
        this.registerCommand(new TeleportAllCommand("tpall"));
        this.registerCommand(new TeleportAllOutOfGameCommand("tpallog"));

        this.registerCommand(new RankingUICommand("rankui"));

        this.registerCommand(new StopMusicCommand("stopmusic"));
        this.registerCommand(new SeeItemTagCommand("seeitemtag"));
    }
}

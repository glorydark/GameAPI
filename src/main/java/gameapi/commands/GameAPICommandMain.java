package gameapi.commands;

import gameapi.GameAPI;
import gameapi.commands.base.EasyCommand;
import gameapi.commands.defaults.DebugCommand;
import gameapi.commands.defaults.achievement.AchievementGiveCommand;
import gameapi.commands.defaults.achievement.AchievementReloadCommand;
import gameapi.commands.defaults.achievement.AchievementUICommand;
import gameapi.commands.defaults.activity.FunctionActivityCommand;
import gameapi.commands.defaults.dev.*;
import gameapi.commands.defaults.fix.FixUICommand;
import gameapi.commands.defaults.fix.ReloadChunkCommand;
import gameapi.commands.defaults.fix.ResetSpeedCommand;
import gameapi.commands.defaults.manage.*;
import gameapi.commands.defaults.ranking.*;
import gameapi.commands.defaults.room.*;
import gameapi.commands.defaults.tools.PlaySoundCommand;
import gameapi.commands.defaults.tools.StopMusicCommand;
import gameapi.commands.defaults.tools.ThunderCommand;

/**
 * @author glorydark
 */
public class GameAPICommandMain extends EasyCommand {

    public GameAPICommandMain(String name) {
        super(name);
        // base
        this.registerCommand(new DebugCommand("debug"));
        this.registerCommand(new RoomQuitCommand("quit"));
        this.registerCommand(new RoomKickCommand("kick"));
        this.registerCommand(new RoomSetPwdCommand("setpwd"));
        this.registerCommand(new RoomStartCommand("roomstart"));
        this.registerCommand(new RoomStatusCommand("status"));
        this.registerCommand(new RoomFastCommand("fast"));

        // dev tools
        this.registerCommand(new PlayerEverCommand("playerever"));
        this.registerCommand(new SeeNameCommand("seename"));
        this.registerCommand(new SeeUUIDCommand("seeuuid"));
        this.registerCommand(new GetFirstPlayedCommand("firstplay"));

        this.registerCommand(new MoveDataCommand("movedata"));
        this.registerCommand(new SeeEnderChestCommand("enderchest"));
        this.registerCommand(new SeeInventoryCommand("seeinv"));

        this.registerCommand(new SeeItemTagCommand("seeitemtag"));

        this.registerCommand(new GetBlockPosCommand("blockpos"));
        this.registerCommand(new GetChestPosCommand("getchestpos"));

        this.registerCommand(new GetLoginChainCommand("loginchain"));
        this.registerCommand(new SaveSkinCommand("saveskin"));

        // ranking system
        this.registerCommand(new RankingAddCommand("addrank"));
        this.registerCommand(new RankingCheckCommand("checkrank"));
        this.registerCommand(new RankingInfoCommand("rankinfo"));
        this.registerCommand(new RankingRefreshCommand("refreshrank"));
        this.registerCommand(new RankingRemoveAllCommand("removerank"));
        this.registerCommand(new RankingUICommand("rankui"));

        // activity & achievement systems
        this.registerCommand(new FunctionActivityCommand("activity"));

        this.registerCommand(new AchievementUICommand("achievement"));
        this.registerCommand(new AchievementReloadCommand("reloadachievement"));
        this.registerCommand(new AchievementGiveCommand("giveachievement"));

        // manage tools
        this.registerCommand(new SudoCmdCommand("sudoc"));
        this.registerCommand(new SudoMsgCommand("sudom"));
        this.registerCommand(new SudoActionCommand("sudoact"));
        this.registerCommand(new TeleportIntoRoomCommand("tproomall"));
        this.registerCommand(new TeleportAllCommand("tpall"));
        this.registerCommand(new TeleportAllOutOfGameCommand("tpallog"));
        this.registerCommand(new RecoverTempDataCommand("recovertempdata"));
        this.registerCommand(new SeeTempDataCommand("seetempdata"));

        // fix
        this.registerCommand(new ResetSpeedCommand("resetspeed"));
        this.registerCommand(new ReloadChunkCommand("rchunk"));
        this.registerCommand(new FixUICommand("fixui"));

        // tools
        this.registerCommand(new PlaySoundCommand("playsound"));
        this.registerCommand(new StopMusicCommand("stopmusic"));
        this.registerCommand(new ThunderCommand("thunder"));

        this.registerCommand(new HideChatCommand("hidechat"));

        // experimental && personal test
        if (GameAPI.getInstance().isGlorydarkRelatedFeature()) {
            this.registerCommand(new RankingMonthlyConclusionCommand("monthrank"));
            this.registerCommand(new ChangeToSnowBiome("snow"));
        }
    }
}

package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.network.protocol.AwardAchievementPacket;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class AwardAchievementCommand extends EasySubCommand {

    public AwardAchievementCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = Server.getInstance().getPlayer(args[0]);
        int achievementId = Integer.parseInt(args[1]);

        AwardAchievementPacket pk = new AwardAchievementPacket();
        pk.achievementId = achievementId;
        player.dataPacket(pk);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

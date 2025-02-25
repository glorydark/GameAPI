package gameapi.commands.defaults.fix;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.NetworkChunkPublisherUpdatePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class ReloadChunkCommand extends EasySubCommand {

    public ReloadChunkCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        if (player.protocol >= ProtocolInfo.v1_8_0) {
            NetworkChunkPublisherUpdatePacket pk0 = new NetworkChunkPublisherUpdatePacket();
            pk0.position = new BlockVector3((int) player.x, (int) player.y, (int) player.z);
            pk0.radius = player.getViewDistance() << 4;
            player.dataPacket(pk0);
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isPlayer();
    }
}

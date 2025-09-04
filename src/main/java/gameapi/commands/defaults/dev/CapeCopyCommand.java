package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.PlayerSkinPacket;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class CapeCopyCommand extends EasySubCommand {

    public CapeCopyCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = Server.getInstance().getPlayer(args[0]);
        Player from = Server.getInstance().getPlayer(args[1]);
        player.getSkin().setCapeData(from.getSkin().getCapeData());
        player.getSkin().setCapeId(from.getSkin().getCapeId());
        player.getSkin().setCapeOnClassic(from.getSkin().isCapeOnClassic());

        player.setSkin(player.getSkin());
        PlayerSkinPacket pk = new PlayerSkinPacket();
        pk.skin = player.getSkin();
        pk.uuid = player.getUniqueId();
        pk.newSkinName = "new_skin";
        pk.oldSkinName = "";
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), pk);
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

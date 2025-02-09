package gameapi.commands.vanilla;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.types.camera.CameraEase;
import gameapi.annotation.Experimental;
import gameapi.commands.base.EasySubCommand;

import java.util.Arrays;

@Experimental
public class ImprovedSendFakeBlockCommand extends EasySubCommand {

    public static final String[] EASE_TYPES = Arrays.stream(CameraEase.values()).map(CameraEase::getSerializeName).toArray(String[]::new);

    public ImprovedSendFakeBlockCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Player player = Server.getInstance().getPlayer(args[0]);
        String[] blockString = args[1].split(":");
        Position position = new Position(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
        Block block = Block.get(Integer.parseInt(blockString[0]), blockString.length > 1? Integer.parseInt(blockString[1]): 0);
        UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
        updateBlockPacket.x = position.getFloorX();
        updateBlockPacket.y = position.getFloorY();
        updateBlockPacket.z = position.getFloorZ();
        updateBlockPacket.blockId = block.getId();
        updateBlockPacket.blockData = block.getDamage();
        updateBlockPacket.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(player.protocol, block.getId(), block.getDamage());
        updateBlockPacket.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
        player.dataPacket(updateBlockPacket);
        player.sendMessage(updateBlockPacket.toString());
        return true;
    }
}
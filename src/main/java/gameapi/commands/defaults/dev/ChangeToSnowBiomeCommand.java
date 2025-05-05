package gameapi.commands.defaults.dev;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import gameapi.commands.base.EasySubCommand;

/**
 * @author glorydark
 */
public class ChangeToSnowBiomeCommand extends EasySubCommand {

    public ChangeToSnowBiomeCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = commandSender.asPlayer();
        FullChunk chunk = player.getChunk();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBiome(x, z, Biome.getBiome(EnumBiome.ICE_PLAINS.id));
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(CommandSender commandSender) {
        return commandSender.isOp();
    }
}

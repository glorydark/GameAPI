package gameapi.commands.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.types.camera.CameraEase;
import gameapi.commands.base.EasySubCommand;
import gameapi.tools.SpatialTools;
import gameapi.utils.AdvancedLocation;

import java.util.Arrays;

public class ImprovedThunderCommand extends EasySubCommand {

    public static final String[] EASE_TYPES = Arrays.stream(CameraEase.values()).map(CameraEase::getSerializeName).toArray(String[]::new);

    public ImprovedThunderCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 4) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                str.append(args[i]).append(":");
            }
            AdvancedLocation advancedLocation = SpatialTools.parseLocation(str.toString());
            Location location;
            if (advancedLocation != null) {
                location = advancedLocation.getLocation();
                EntityLightning entityLightning = new EntityLightning(location.getChunk(), EntityLightning.getDefaultNBT(location));
                entityLightning.setEffect(false);
                entityLightning.spawnToAll();
            }
        }
        return true;
    }
}
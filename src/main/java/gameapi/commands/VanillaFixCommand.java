package gameapi.commands;

import gameapi.commands.base.EasyCommand;
import gameapi.commands.vanilla.*;
import gameapi.utils.NukkitTypeUtils;

/**
 * @author glorydark
 */
public class VanillaFixCommand extends EasyCommand {

    public VanillaFixCommand(String name) {
        super(name);
        this.registerCommand(new TeleportCommand("tp"));
        this.registerCommand(new ThunderCommand("thunder"));
        this.registerCommand(new XpCommand("xp"));
        this.registerCommand(new ParticleCommand("particle"));
        
        this.registerCommand(new LoadWorldCommand("loadworld"));
        this.registerCommand(new UnloadWorldCommand("unloadworld"));
        this.registerCommand(new WorldCommand("world"));

        if (NukkitTypeUtils.getNukkitType() == NukkitTypeUtils.NukkitType.MOT) {
            this.registerCommand(new CameraCommand("camera"));
            this.registerCommand(new EffectCommand("effect"));
        }
    }
}

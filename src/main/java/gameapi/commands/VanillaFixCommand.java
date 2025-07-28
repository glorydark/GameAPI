package gameapi.commands;

import gameapi.commands.base.EasyCommand;
import gameapi.commands.vanilla.*;

/**
 * @author glorydark
 */
public class VanillaFixCommand extends EasyCommand {

    public VanillaFixCommand(String name) {
        super(name);
        this.registerCommand(new ImprovedCameraCommand("camera"));
        this.registerCommand(new ImprovedTeleportCommand("tp"));
        this.registerCommand(new ImprovedThunderCommand("thunder"));
        this.registerCommand(new ImprovedXpCommand("xp"));
        this.registerCommand(new ImprovedEffectCommand("effect"));
        this.registerCommand(new ImprovedParticleCommand("particle"));
    }
}

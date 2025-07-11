package gameapi.commands;

import gameapi.commands.base.EasyCommand;
import gameapi.commands.vanilla.ImprovedCameraCommand;
import gameapi.commands.vanilla.ImprovedTeleportCommand;
import gameapi.commands.vanilla.ImprovedThunderCommand;
import gameapi.commands.vanilla.ImprovedXpCommand;

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
    }
}

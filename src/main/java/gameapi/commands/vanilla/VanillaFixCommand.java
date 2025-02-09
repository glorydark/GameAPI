package gameapi.commands.vanilla;

import gameapi.commands.base.EasyCommand;

/**
 * @author glorydark
 */
public class VanillaFixCommand extends EasyCommand {

    public VanillaFixCommand(String name) {
        super(name);
        this.registerCommand(new ImprovedCameraCommand("camera"));
        this.registerCommand(new ImprovedTeleportCommand("tp"));
        this.registerCommand(new ImprovedThunderCommand("thunder"));
        this.registerCommand(new ImprovedSendFakeBlockCommand("sendfakeblock"));
        this.registerCommand(new ImprovedEntityPropertyCommand("synctest"));
    }
}

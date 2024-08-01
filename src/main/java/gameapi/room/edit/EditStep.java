package gameapi.room.edit;

import cn.nukkit.Player;
import cn.nukkit.block.Block;

/**
 * @author glorydark
 */
public abstract class EditStep {

    protected EditProcess editProcess;

    public EditStep(EditProcess editProcess) {
        this.editProcess = editProcess;
    }

    public void onStart(Player player) {

    }

    public void onBreak(Player player, Block block) {

    }

    public void onPlace(Player player, Block block) {

    }

    public void onInteract(Player player, Block block) {

    }

    public void onInteractAir(Player player) {

    }

    public void onEnd(Player player) {

    }

    public EditProcess getEditProcess() {
        return this.editProcess;
    }

    public Player getPlayer() {
        return this.getEditProcess().getPlayer();
    }

    protected void close() {
        this.getEditProcess().nextStep();
    }

    protected int getId() {
        return this.getEditProcess().getSteps().indexOf(this);
    }

    public void onTick() {

    }
}

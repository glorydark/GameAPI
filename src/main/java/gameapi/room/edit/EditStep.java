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

    public void onStart() {

    }

    public void onBreak(Block block) {

    }

    public void onPlace(Block block) {

    }

    public void onInteract() {

    }

    public void onEnd() {

    }

    public EditProcess getEditData() {
        return this.editProcess;
    }

    public Player getPlayer() {
        return this.getEditData().getPlayer();
    }

    protected void close() {
        this.getEditData().nextStep();
    }

    protected int getNumber() {
        return this.getEditData().getSteps().indexOf(this);
    }

    public void onTick() {

    }
}

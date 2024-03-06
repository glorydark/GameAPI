package gameapi.room.edit;

import cn.nukkit.Player;
import cn.nukkit.utils.ConfigSection;
import gameapi.annotation.Future;

/**
 * @author glorydark
 */
@Future
public abstract class EditStep {

    protected ConfigSection stepCache;

    public EditStep() {
        stepCache = new ConfigSection();
    }

    public void onStart(EditData editData) {
        stepCache = new ConfigSection();
    }

    public void onBreak(EditData editData) {

    }

    public void onPlace(EditData editData) {

    }

    public void onInteract(EditData editData) {

    }

    protected void onEnd(EditData editData) {

    }

    public ConfigSection getStepCache() {
        return stepCache;
    }
}

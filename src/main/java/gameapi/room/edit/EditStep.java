package gameapi.room.edit;

import gameapi.annotation.Future;

/**
 * @author glorydark
 */
@Future
public abstract class EditStep {

    protected EditData editData;

    public EditStep(EditData editData) {
        this.editData = editData;
    }

    public void onStart() {

    }

    public void onBreak() {

    }

    public void onPlace() {

    }

    public void onInteract() {

    }

    protected void onEnd() {

    }

    public EditData getEditData() {
        return editData;
    }
}

package gameapi;

import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.network.protocol.ModalFormRequestPacket;

public class FormWindowFactory extends FormWindow {

    private final String type;

    public FormWindowFactory() {
        this.type = "screen";
        
    }

    @Override
    public void setResponse(String s) {

    }

    @Override
    public FormResponse getResponse() {
        return null;
    }
}

package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;
import gameapi.annotation.Future;

@Future
public interface AdvancedForm {

    void dealResponse(Player player, FormResponse response);

}

package gameapi.form;

import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;

/*
 * AdvancedForm was originally from lt-name's MemoriesOfTime-GameCore.
 * I (the author) made scant changes in constructors and so on to be easier to use.
 */
public interface AdvancedForm {
    void dealResponse(Player player, FormResponse response);

    void showToPlayer(Player player);
}
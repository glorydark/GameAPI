package gameapi.form.response;

import cn.nukkit.item.Item;
import gameapi.form.AdvancedChestFormBase;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class ChestResponse {

    private AdvancedChestFormBase advancedChestFormBase;

    private int slot;

    private Item item;
}

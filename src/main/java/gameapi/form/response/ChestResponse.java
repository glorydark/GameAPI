package gameapi.form.response;

import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class ChestResponse {

    private int slot;

    private Item item;
}

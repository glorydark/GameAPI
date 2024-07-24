package gameapi.form.response;

import cn.nukkit.item.Item;
import gameapi.form.AdvancedFakeBlockContainerFormBase;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author glorydark
 */
@Data
@AllArgsConstructor
public class ChestResponse {

    private AdvancedFakeBlockContainerFormBase advancedFakeBlockContainerFormBase;

    private int slot;

    private Item item;
}

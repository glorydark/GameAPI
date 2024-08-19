package gameapi.form.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.profession.Profession;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.inventory.TradeInventory;
import gameapi.listener.AdvancedFormListener;

/**
 * @author glorydark
 */
public class ResponsiveTradeForm extends TradeInventory {

    public String name;

    public Entity targetEntity;

    public ResponsiveTradeForm() {
        super(null);
    }

    public void showToPlayer(Player player) {
        AdvancedVillagerEntity entity = new AdvancedVillagerEntity(player.getChunk(), EntityVillagerV2.getDefaultNBT(player.add(0, -2, 0)));
        entity.applyProfession(Profession.getProfession(1));
        entity.setImmobile(true);
        entity.spawnToAll();
        this.holder = entity;
        AdvancedFormListener.showToPlayer(player, this);
        TradeInventory inv = new TradeInventory(entity);
        player.addWindow(inv, 500);
    }

    protected void postCloseExecute(Player player) {
        ((Entity) this.holder).close();
    }

    public void closeForPlayer(Player player) {
        this.postCloseExecute(player);
    }

    public void onClose(Player player) {
        this.postCloseExecute(player);
    }
}

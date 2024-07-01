package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import gameapi.form.AdvancedChestFormBase;
import gameapi.form.AdvancedForm;
import gameapi.form.inventory.FakeInventory;
import gameapi.form.minecart.AdvancedMinecartChestMenu;
import gameapi.form.response.ChestResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedFormListener implements Listener {

    public static final String FORM_ENTITY_TAG = "GameAPIFormEntity";
    protected static Map<Player, LinkedHashMap<Integer, FormWindow>> playerFormWindows = new LinkedHashMap<>();
    protected static Map<Player, AdvancedChestFormBase> chestFormMap = new LinkedHashMap<>();

    public static void showToPlayer(Player player, FormWindow form) {
        AdvancedFormListener.playerFormWindows.computeIfAbsent(player, i -> new LinkedHashMap<>()).put(player.showFormWindow(form), form);
    }

    public static boolean showToPlayer(Player player, AdvancedMinecartChestMenu menu) {
        if (chestFormMap.containsKey(player)) {
            return false;
        }
        chestFormMap.put(player, menu);
        return true;
    }

    public static void removeChestMenuCache(Player player) {
        chestFormMap.remove(player);
    }

    protected void execute(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (playerFormWindows.containsKey(player)) {
            FormWindow window = playerFormWindows.getOrDefault(player, new LinkedHashMap<>()).get(event.getFormID());
            if (window != null) {
                if (window instanceof AdvancedForm) {
                    ((AdvancedForm) window).dealResponse(player, event.getResponse());
                }
            }
        }
    }

    @EventHandler
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event) {
        this.execute(event);
    }

    @EventHandler
    public void InventoryPickupItemEvent(InventoryPickupItemEvent event) {
        Inventory inventory = event.getInventory();
        if (this.isFormInventory(inventory) || this.isChestInventory(inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryMoveItemEvent(InventoryMoveItemEvent event) {
        Inventory inventory = event.getInventory();
        if (this.isFormInventory(inventory) || this.isChestInventory(inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryTransactionEvent(InventoryTransactionEvent event) {
        for (Inventory inventory : event.getTransaction().getInventories()) {
            if (this.isFormInventory(inventory) || this.isChestInventory(inventory)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        Player player = event.getPlayer();
        Inventory inventory = event.getInventory();
        if (isFormInventory(inventory)) {
            if (chestFormMap.containsKey(player)) {
                chestFormMap.get(player).dealResponse(player, new ChestResponse(event.getSlot(), event.getSourceItem()));
            }
            event.setCancelled(true);
        } else if (isChestInventory(inventory)) {
            FakeInventory inv = (FakeInventory) inventory;
            inv.getFormBase().dealResponse(player, new ChestResponse(event.getSlot(), event.getSourceItem()));
        }
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {
        removeChestMenuCache(event.getPlayer());
    }

    @EventHandler
    public void InventoryCloseEvent(InventoryCloseEvent event) {
        Player player = event.getPlayer();
        Inventory inventory = event.getInventory();
        if (isFormInventory(inventory)) {
            if (chestFormMap.containsKey(player)) {
                chestFormMap.get(player).dealResponse(player, null);
                chestFormMap.get(player).close(player);
            }
        } else if (isChestInventory(inventory)) {
            FakeInventory inv = (FakeInventory) inventory;
            inv.getFormBase().dealResponse(player, null);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (this.isFormEntity(event.getEntity())) {
            event.getEntity().close();
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (this.isFormEntity(event.getEntity())) {
            event.getEntity().close();
        }
    }

    @EventHandler
    public void EntityDamageByBlockEvent(EntityDamageByBlockEvent event) {
        if (this.isFormEntity(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void EntityDamageByChildEntityEvent(EntityDamageByChildEntityEvent event) {
        if (this.isFormEntity(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (this.isFormEntity(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    public boolean isFormInventory(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Entity) {
            return isFormEntity((Entity) holder);
        }
        return false;
    }

    public boolean isChestInventory(Inventory inventory) {
        return inventory instanceof FakeInventory;
    }

    public boolean isFormEntity(Entity entity) {
        return entity.namedTag.contains(AdvancedFormListener.FORM_ENTITY_TAG);
    }
}

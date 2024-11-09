package gameapi.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.inventory.*;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.PlayerUIInventory;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import gameapi.form.AdvancedForm;
import gameapi.form.entity.ResponsiveTradeForm;
import gameapi.form.inventory.block.AdvancedBlockFakeBlockInventory;
import gameapi.form.inventory.block.AdvancedBlockFakeBlockInventoryImpl;
import gameapi.form.response.BlockInventoryResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedFormListener implements Listener {

    public static final String VILLAGER_ENTITY_TAG = "gameapi_villager_entity";
    protected static Map<Player, LinkedHashMap<Integer, FormWindow>> playerFormWindows = new LinkedHashMap<>();
    protected static Map<Player, AdvancedBlockFakeBlockInventory> chestFormMap = new LinkedHashMap<>();
    protected static Map<Player, ResponsiveTradeForm> villagerFormMap = new LinkedHashMap<>();

    public static void showToPlayer(Player player, FormWindow form) {
        AdvancedFormListener.playerFormWindows.computeIfAbsent(player, i -> new LinkedHashMap<>()).put(player.showFormWindow(form), form);
    }

    public static void showToPlayer(Player player, ResponsiveTradeForm form) {
        AdvancedFormListener.villagerFormMap.put(player, form);
    }

    public static void addChestMenuCache(Player player, AdvancedBlockFakeBlockInventory inventory) {
        chestFormMap.put(player, inventory);
    }

    public static void removeChestMenuCache(Player player) {
        chestFormMap.remove(player);
    }

    @EventHandler
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event) {
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
    public void InventoryPickupItemEvent(InventoryPickupItemEvent event) {
        Inventory inventory = event.getInventory();
        if (this.isChestInventory(inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void InventoryMoveItemEvent(InventoryMoveItemEvent event) {
        Inventory inventory = event.getInventory();
        if (this.isChestInventory(inventory)) {
            AdvancedBlockFakeBlockInventoryImpl form = (AdvancedBlockFakeBlockInventoryImpl) inventory;
            if (!form.isItemMovable()) {
                event.setCancelled(true);
            } else {
                form.dealOnSlotChangeResponse(event);
            }
        }
    }

    @EventHandler
    public void InventoryTransactionEvent(InventoryTransactionEvent event) {
        for (Inventory inventory : event.getTransaction().getInventories()) {
            if (this.isChestInventory(inventory)) {
                AdvancedBlockFakeBlockInventoryImpl form = (AdvancedBlockFakeBlockInventoryImpl) inventory;
                if (!form.isItemMovable()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {
        Player player = event.getPlayer();
        Inventory inventory = event.getInventory();
        // System.out.println(inventory.getClass() + ":" + inventory.getName() + ":" + inventory.getTitle() + ":" + inventory.getType());
        if (this.isChestInventory(inventory)) {
            AdvancedBlockFakeBlockInventoryImpl form = (AdvancedBlockFakeBlockInventoryImpl) inventory;
            BlockInventoryResponse response = new BlockInventoryResponse(form, event.getSlot(), event.getSourceItem());
            form.dealOnClickResponse(player, response);
            if (!form.isItemMovable()) {
                event.setCancelled(true);
            } else if (response.isCancelled()) {
                event.setCancelled(true);
            }
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
        if (this.isChestInventory(inventory)) {
            AdvancedBlockFakeBlockInventoryImpl inv = (AdvancedBlockFakeBlockInventoryImpl) inventory;
            inv.dealOnClickResponse(player, null);
            inv.closeForPlayer(player);
        }
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {
        if (this.isVillagerEntity(event.getEntity())) {
            event.getEntity().close();
        }
    }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (this.isVillagerEntity(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    public boolean isChestInventory(Inventory inventory) {
        return inventory instanceof AdvancedBlockFakeBlockInventoryImpl;
    }

    public boolean isVillagerEntity(InventoryHolder holder) {
        if (holder instanceof EntityVillagerV2) {
            return this.isVillagerEntity(holder);
        } else {
            return false;
        }
    }

    public boolean isVillagerEntity(Entity entity) {
        return entity.namedTag.contains(AdvancedFormListener.VILLAGER_ENTITY_TAG);
    }

    @EventHandler
    public void DataPacketReceiveEvent(DataPacketReceiveEvent event) {
        Player player = event.getPlayer();
        if (event.getPacket().packetId() == ProtocolInfo.CONTAINER_CLOSE_PACKET) {
            ContainerClosePacket pk = (ContainerClosePacket) event.getPacket();
            if (pk.windowId == -1) {
                if (villagerFormMap.containsKey(player)) {
                    ResponsiveTradeForm form = villagerFormMap.get(player);
                    if (form != null) {
                        form.onClose(player);
                    }
                }
            }
        }
    }
}

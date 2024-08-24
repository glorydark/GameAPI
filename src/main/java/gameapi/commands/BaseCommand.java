package gameapi.commands;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.entity.data.TextEntityData;
import gameapi.form.AdvancedDoubleChestForm;
import gameapi.form.element.ResponsiveElementSlotItem;
import gameapi.manager.RoomManager;
import gameapi.manager.tools.GameEntityManager;
import gameapi.ranking.Ranking;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.tools.*;
import gameapi.utils.PosSet;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author Glorydark
 * For in-game test
 */
public class BaseCommand extends Command {

    public BaseCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length > 0) {
            Player p = commandSender.asPlayer();
            switch (strings[0].toLowerCase()) {
                case "getchestpos":
                    if (p == null) {
                        return false;
                    }
                    PosSet posSet = WorldEditCommand.posSetLinkedHashMap.get(p);
                    if (posSet == null) {
                        p.sendMessage("Pos set is null");
                        return false;
                    }
                    if (posSet.getPos1() == null || posSet.getPos2() == null) {
                        p.sendMessage("You haven't set pos1 or pos2");
                        return false;
                    }
                    SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(posSet.getPos1(), posSet.getPos2());
                    Level l = p.getLevel();
                    bb.forEach((i, i1, i2) -> {
                        if (l.getBlock(i, i1, i2, true).getId() == BlockID.CHEST) {
                            System.out.println(i + ", " + i1 + ", " + i2 + "\n");
                        }
                    });
                    break;
                case "addrank":
                    if (commandSender.isPlayer()) {
                        if (strings.length >= 6) {
                            Player player = (Player) commandSender;
                            GameEntityManager.addRankingList(player, strings[1], strings[2], strings[3], strings[4], Ranking.getRankingSortSequence(strings[5]));
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.use_in_game"));
                    }
                    break;
                case "checkrank":
                    for (TextEntityData entry : GameEntityManager.rankingList) {
                        Entity textEntity = entry.getEntity();
                        commandSender.sendMessage(textEntity.toString());
                    }
                    break;
                case "refreshrank":
                    GameAPI.getInstance().loadAllPlayerGameData();
                    GameAPI.getInstance().loadRanking();
                    break;
                case "test1":
                    AdvancedDoubleChestForm chestForm = new AdvancedDoubleChestForm("测试标题")
                            .onClick((player, c) -> player.sendMessage("Click on " + c.getItem().getName()))
                            .onClose(player -> player.sendMessage("Close"))
                            .item(1, new ResponsiveElementSlotItem("minecraft:iron_sword")
                                    .onRespond((player, chestResponse) -> {
                                        player.sendMessage("进入子菜单");
                                        chestResponse.getInventory().clearAll();
                                        chestResponse.getInventory().addItemToSlot(2,
                                                new ResponsiveElementSlotItem("minecraft:red_flower")
                                                        .onRespond((player1, blockInventoryResponse) -> {
                                                            player1.sendMessage("关闭界面");
                                                            blockInventoryResponse.getInventory().closeForPlayer(player1);
                                                        })
                                        );
                                    })
                            );
                    chestForm.showToPlayer((Player) commandSender);

                    /*
                    AdvancedMinecartChestMenu form = new AdvancedMinecartChestMenu("测试标题")
                            .onClose(player -> player.sendMessage("我关了"))
                            .onClick((player, item) -> player.sendMessage(item.getName()))
                            .item(0,
                                    new ResponsiveElementSlotItem(267)
                                            .onRespond((player, item) -> player.sendMessage("嘤嘤嘤"))
                            );
                    form.showToPlayer((Player) commandSender);
                     */
                    break;
                case "quit":
                    if (commandSender.isPlayer()) {
                        Room room = RoomManager.getRoom((Player) commandSender);
                        if (room != null) {
                            if (room.getPlayers().contains((Player) commandSender)) {
                                room.removePlayer((Player) commandSender);
                            } else {
                                room.removeSpectator((Player) commandSender);
                            }
                        } else {
                            GameAPI.getLanguage().getTranslation("command.error.not_in_game");
                        }
                    }
                    break;
                case "playsound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            float volume = 1;
                            float pitch = 1;
                            if (strings.length > 3) {
                                volume = Float.parseFloat(strings[3]);
                                if (strings.length > 4) {
                                    pitch = Float.parseFloat(strings[4]);
                                }
                            }
                            SoundTools.addSoundToPlayer(player, strings[2], volume, pitch);
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_not_found", strings[1]));
                        }
                    }
                    break;
                case "debug":
                    if (strings.length != 2) {
                        return false;
                    }
                    if (commandSender.isPlayer()) {
                        switch (strings[1]) {
                            case "true":
                                GameAPI.getGameDebugManager().addPlayer((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.on"));
                                break;
                            case "false":
                                GameAPI.getGameDebugManager().removePlayer((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.off"));
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.use_in_game"));
                    }
                    break;
                case "savebattles":
                    // todo: For Tournament Restart Procedures
                    File saveDic = new File(GameAPI.getPath() + "/saves/" + SmartTools.dateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss") + "/");
                    if (saveDic.exists() || saveDic.mkdirs()) {
                        for (Map.Entry<String, List<Room>> entry : RoomManager.getLoadedRooms().entrySet()) {
                            for (Room room : entry.getValue()) {
                                if (room.getRoomStatus().equals(RoomStatus.ROOM_STATUS_START)) {
                                    File file = new File(saveDic.getPath() + "/" + entry.getKey() + "_" + room.getRoomName() + ".json");
                                    Config config = new Config(file, Config.JSON);
                                    LinkedHashMap<String, Object> players = new LinkedHashMap<>();
                                    room.getPlayers().forEach(player -> players.put(player.getName(), getPlayerData(player)));
                                    LinkedHashMap<String, Object> objectMap = new LinkedHashMap<>();
                                    objectMap.put("players", players);
                                    objectMap.put("players_properties", getPropertiesData(room.getPlayerProperties()));
                                    objectMap.put("room_properties", room.getRoomProperties());
                                    objectMap.put("room_datas", new Gson().fromJson(room.toString(), Map.class));
                                    config.setAll(objectMap);
                                    config.save();
                                }
                            }
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.save_battle.folder_created_failed", saveDic.getPath()));
                    }
                    break;
                case "stoproom": // todo
                    if (strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                                RoomManager.getPlayerRoomHashMap().remove(player);
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STOPPED);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "halt": // todo
                    if (strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_START) {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room.not_start_yet"));
                                return true;
                            }
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                            }
                            room.setRoomStatus(RoomStatus.ROOM_HALTED);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "restart": // todo
                    if (strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                if (player.isOnline()) {
                                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                    player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                                } else {
                                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_offline", player.getName()));
                                }
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_START);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "status":
                    StringBuilder builder = new StringBuilder(GameAPI.getLanguage().getTranslation(commandSender, "command.status.getting") + "\n");
                    if (RoomManager.getRoomCount() > 0) {
                        for (Map.Entry<String, List<Room>> game : RoomManager.getLoadedRooms().entrySet()) {
                            builder.append("\n")
                                    .append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.title", game))
                                    .append("\n");
                            List<Room> rooms = game.getValue();
                            if (!rooms.isEmpty()) {
                                for (Room room : rooms) {
                                    if (!room.isAllowedToStart()) {
                                        builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.need_start_pass", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()))
                                                .append("\n");
                                    } else {
                                        builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.common", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()))
                                                .append("\n");
                                    }
                                    Set<String> playerNameList = new HashSet<>();
                                    for (Player player : room.getPlayers()) {
                                        playerNameList.add(player.getName());
                                    }
                                    builder.append("Players: ")
                                            .append(playerNameList.toString().replace("[", "").replace("]", ""))
                                            .append("\n");
                                }
                            } else {
                                builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.no_room_loaded"))
                                        .append("\n");
                            }

                        }
                    } else {
                        builder.append(GameAPI.getLanguage().getTranslation(commandSender, "command.status.no_game_loaded"))
                                .append("\n");
                    }
                    builder.append("\n");
                    commandSender.sendMessage(builder.toString());
                    break;
                case "roomstart":
                    if (commandSender.isOp()) {
                        if (strings.length == 3) {
                            Room room = RoomManager.getRoom(strings[1], strings[2]);
                            if (room != null) {
                                room.setAllowedToStart(true);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.start_pass.endowed", room.getRoomName()));
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found", strings[2]));
                            }
                            return false;
                        }
                    }
                    Room room = RoomManager.getRoom(p);
                    if (room != null) {
                        if (room.getRoomAdmins().contains(commandSender.getName()) || commandSender.isOp()) {
                            room.setAllowedToStart(true);
                            commandSender.sendMessage(TextFormat.GREEN + "您已允许房间开始倒计时！");
                        }
                    }
                    break;
                case "kick":
                    if (commandSender.isPlayer()) {
                        room = RoomManager.getRoom((Player) commandSender);
                        if (commandSender.isOp() || room.getRoomAdmins().contains(commandSender.getName())) {
                            if (room != null) {
                                Player player = Server.getInstance().getPlayer(strings[1]);
                                if (player != null) {
                                    if (room.getPlayers().contains(player)) {
                                        room.removePlayer(player);
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.kick.success"));
                                    } else {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.error.not_in_game.others", player.getName()));
                                    }
                                } else {
                                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_not_found", strings[1]));
                                }
                            } else {
                                GameAPI.getLanguage().getTranslation("command.error.not_in_game");
                            }
                        }
                    }
                case "setpwd":
                    if (strings.length == 4) {
                        room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isAllowedToStart()) {
                                room.setJoinPassword(strings[3]);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.set_password", strings[3]));
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "seeuuid":
                    if (strings.length == 2) {
                        String playerName = strings[1];
                        Player seePlayer = Server.getInstance().getPlayer(playerName);
                        if (seePlayer != null) {
                            GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("command.see_uuid.success", playerName, seePlayer.getUniqueId().toString()));
                        } else {
                            Optional<UUID> offlineUUID = Server.getInstance().lookupName(playerName);
                            if (offlineUUID.isPresent()) {
                                IPlayer seePlayerOffline = Server.getInstance().getOfflinePlayer(offlineUUID.get());
                                GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("command.see_uuid.success", playerName, seePlayerOffline.getUniqueId().toString()));
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_uuid.player_not_found", playerName));
                            }
                        }
                    }
                    break;
                case "seename":
                    if (strings.length == 2) {
                        UUID uuid = UUID.fromString(strings[1]);
                        Optional<Player> seePlayer = Server.getInstance().getPlayer(uuid);
                        if (seePlayer.isPresent()) {
                            GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("command.see_name.success", uuid, seePlayer.get().getName()));
                        } else {
                            IPlayer offlinePlayer = Server.getInstance().getOfflinePlayer(uuid);
                            if (offlinePlayer != null) {
                                GameAPI.getInstance().getLogger().info(GameAPI.getLanguage().getTranslation("command.see_name.success", uuid, offlinePlayer.getName()));
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_name.player_not_found", uuid));
                            }
                        }
                    }
                    break;
                case "playerever":
                    CompletableFuture.runAsync(() -> {
                        int count = 0;
                        try {
                            File[] files = new File(Server.getInstance().getDataPath() + "players/").listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    String name = file.getName();
                                    if (name.endsWith(".dat") && !name.endsWith(".bak.dat")) {
                                        count++;
                                    }
                                }
                            }
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.player_ever.success", count));
                        } catch (Exception ignore) {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.player_ever.no_access"));
                        }
                    });
                    break;
                case "saveskin":
                    if (strings.length == 2) {
                        String pn = strings[1];
                        Player player = Server.getInstance().getPlayerExact(pn);
                        if (player != null) {
                            Skin skin = player.getSkin();
                            String fileName = System.currentTimeMillis() + "";
                            new File(GameAPI.getPath() + "/skin_exports/" + pn + "/").mkdirs();
                            SkinTools.savePlayerJson(skin.getGeometryData(), new File(GameAPI.getPath() + "/skin_exports/" + pn + "/" + fileName + ".json"));
                            SkinTools.parseSerializedImage(skin.getSkinData(), new File(GameAPI.getPath() + "/skin_exports/" + pn + "/" + fileName + "_skin.png"));
                            SkinTools.parseSerializedImage(skin.getCapeData(), new File(GameAPI.getPath() + "/skin_exports/" + pn + "/" + fileName + "_cape.png"));
                            commandSender.sendMessage(TextFormat.GREEN + "Saved in /skin_exports/" + fileName);
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_offline", player.getName()));
                        }
                    }
                    break;
                case "breakblocks":
                    if (strings.length == 8) {
                        Level level = Server.getInstance().getLevelByName(strings[7]);
                        BlockTools.destroyAreaBlocks(new SimpleAxisAlignedBB(
                                new Vector3(
                                        Integer.parseInt(strings[1]),
                                        Integer.parseInt(strings[2]),
                                        Integer.parseInt(strings[3])
                                ),
                                new Vector3(
                                        Integer.parseInt(strings[4]),
                                        Integer.parseInt(strings[5]),
                                        Integer.parseInt(strings[6])
                                )
                        ), level, true);
                    }
                    break;
            }
        }
        return false;
    }

    public LinkedHashMap<String, Object> getPlayerData(Player player) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ() + ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(ItemTools.toString(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for (Item item : player.getInventory().getArmorContents()) {
            armors.add(ItemTools.toString(item));
        }
        maps.put("ArmorContents", armors);
        return maps;
    }

    public LinkedHashMap<String, Object> getPropertiesData(LinkedHashMap<String, LinkedHashMap<String, Object>> playerObjectMap) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        for (String key : playerObjectMap.keySet()) {
            maps.put(key, playerObjectMap.get(key));
        }
        return maps;
    }
}

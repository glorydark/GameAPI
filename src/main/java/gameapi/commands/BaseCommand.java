package gameapi.commands;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.manager.RoomManager;
import gameapi.manager.tools.GameEntityManager;
import gameapi.ranking.RankingSortSequence;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.tools.InventoryTools;
import gameapi.tools.SmartTools;
import gameapi.tools.SoundTools;

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
        this.commandParameters.clear();
        this.commandParameters.put("quit", new CommandParameter[]{
                CommandParameter.newType("quit", CommandParamType.TEXT)
        });
        this.commandParameters.put("debug", new CommandParameter[]{
                CommandParameter.newType("debug", CommandParamType.TEXT),
                CommandParameter.newType("state", CommandParamType.TEXT)
        });
        this.commandParameters.put("playsound", new CommandParameter[]{
                CommandParameter.newType("playsound", CommandParamType.TEXT),
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("identifier", CommandParamType.TEXT),
                CommandParameter.newType("volume", CommandParamType.FLOAT),
                CommandParameter.newType("pitch", CommandParamType.FLOAT),
        });
        this.commandParameters.put("addrank", new CommandParameter[]{
                CommandParameter.newType("game_name", CommandParamType.TEXT),
                CommandParameter.newType("compared_type", CommandParamType.TEXT)
        });
        this.commandParameters.put("status", new CommandParameter[]{
                CommandParameter.newType("status", CommandParamType.TEXT)
        });
        this.commandParameters.put("setpwd", new CommandParameter[]{
                CommandParameter.newType("setpwd", CommandParamType.TEXT),
                CommandParameter.newType("game_name", CommandParamType.TEXT),
                CommandParameter.newType("room_name", CommandParamType.TEXT)
        });
        this.commandParameters.put("roomstart", new CommandParameter[]{
                CommandParameter.newType("game_name", CommandParamType.TEXT),
                CommandParameter.newType("room_name", CommandParamType.TEXT)
        });
        this.commandParameters.put("seeuuid", new CommandParameter[]{
                CommandParameter.newType("seeuuid", CommandParamType.TEXT),
                CommandParameter.newType("player", CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (strings.length > 0) {
            switch (strings[0].toLowerCase()) {
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
                            SoundTools.playResourcePackOggMusic(player, strings[2], volume, pitch);
                        } else {
                            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.player_not_found", strings[1]));
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
                                GameAPI.debug.add((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.on"));
                                break;
                            case "false":
                                GameAPI.debug.remove((Player) commandSender);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.debug.off"));
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.use_in_game"));
                    }
                    break;
                case "savebattles":
                    // todo: For Tournament Restart Procedures
                    File saveDic = new File(GameAPI.path + "/saves/" + SmartTools.dateToString(Calendar.getInstance().getTime(), "yyyyMMdd_HHmmss") + "/");
                    if (saveDic.exists() || saveDic.mkdirs()) {
                        for (String key : RoomManager.loadedRooms.keySet()) {
                            for (Room room : RoomManager.loadedRooms.get(key)) {
                                if (room.getRoomStatus().equals(RoomStatus.ROOM_STATUS_GameStart)) {
                                    File file = new File(saveDic.getPath() + "/" + key + "_" + room.getRoomName() + ".json");
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
                        GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.save_battle.folder_created_failed", saveDic.getPath()));
                    }
                    break;
                case "addrank":
                    if (commandSender.isPlayer()) {
                        if (strings.length == 3) {
                            Player player = (Player) commandSender;
                            GameEntityManager.addRankingList(player, strings[1], strings[2], RankingSortSequence.DESCEND);
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.use_in_game"));
                    }
                    break;
                case "stoproom": // todo
                    if (strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                                RoomManager.playerRoomHashMap.remove(player);
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
                            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
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
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "status":
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.getting"));
                    if (RoomManager.loadedRooms.size() > 0) {
                        for (Map.Entry<String, List<Room>> game : RoomManager.loadedRooms.entrySet()) {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.title", game));
                            List<Room> rooms = game.getValue();
                            if (rooms.size() > 0) {
                                for (Room room : rooms) {
                                    if (room.getRoomRule().isNeedPreStartPass()) {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.need_start_pass", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
                                    } else {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.common", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
                                    }
                                }
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.no_room_loaded"));
                            }
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.no_game_loaded"));
                    }
                    break;
                case "roomstart":
                    if (strings.length == 3) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isPreStartPass()) {
                                room.setPreStartPass(true);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.start_pass.endowed", room.getRoomName()));
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room_not_found"));
                        }
                    }
                    break;
                case "setpwd":
                    if (strings.length == 4) {
                        Room room = RoomManager.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isPreStartPass()) {
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
                            GameAPI.plugin.getLogger().info(GameAPI.getLanguage().getTranslation("command.see_uuid.success", playerName, seePlayer.getUniqueId().toString()));
                        } else {
                            Optional<UUID> offlineUUID = Server.getInstance().lookupName(playerName);
                            if (offlineUUID.isPresent()) {
                                IPlayer seePlayerOffline = Server.getInstance().getOfflinePlayer(offlineUUID.get());
                                GameAPI.plugin.getLogger().info(GameAPI.getLanguage().getTranslation("command.see_uuid.success", playerName, seePlayerOffline.getUniqueId().toString()));
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
                            GameAPI.plugin.getLogger().info(GameAPI.getLanguage().getTranslation("command.see_name.success", uuid, seePlayer.get().getName()));
                        } else {
                            IPlayer offlinePlayer = Server.getInstance().getOfflinePlayer(uuid);
                            if (offlinePlayer != null) {
                                GameAPI.plugin.getLogger().info(GameAPI.getLanguage().getTranslation("command.see_name.success", uuid, offlinePlayer.getName()));
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation("command.see_name.player_not_found", uuid));
                            }
                        }
                    }
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
            }
        }
        return false;
    }

    public LinkedHashMap<String, Object> getPlayerData(Player player) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ() + ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(InventoryTools.toBase64String(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for (Item item : player.getInventory().getArmorContents()) {
            armors.add(InventoryTools.toBase64String(item));
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

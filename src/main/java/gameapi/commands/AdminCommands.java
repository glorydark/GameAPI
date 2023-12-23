package gameapi.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import gameapi.GameAPI;
import gameapi.arena.WorldTools;
import gameapi.entity.EntityTools;
import gameapi.inventory.InventoryTools;
import gameapi.ranking.RankingSortSequence;
import gameapi.room.Room;
import gameapi.room.RoomStatus;
import gameapi.sound.SoundTools;
import gameapi.utils.SmartTools;

import java.io.File;
import java.util.*;

/**
 * @author Glorydark
 * For in-game test
 */
public class AdminCommands extends Command {
    public AdminCommands(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.isOp() && commandSender.isPlayer()) {
            return false;
        }
        if (strings.length > 0) {
            switch (strings[0].toLowerCase()) {
                case "playressound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            SoundTools.playResourcePackOggMusic(player, strings[2]);
                        } else {
                            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.playerNotFound", strings[1]));
                        }
                    }
                    break;
                case "playambientsound":
                    if (strings.length > 2) {
                        Player player = Server.getInstance().getPlayer(strings[1]);
                        if (player != null) {
                            Sound sound = Sound.valueOf(strings[2]);
                            if (sound.getSound() != null) {
                                SoundTools.addAmbientSound(player.level, player, sound);
                            } else {
                                GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.vanillaSoundNotFound", strings[1]));
                            }
                        } else {
                            GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.error.playerNotFound", strings[1]));
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
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.useInGame"));
                    }
                    break;
                case "savebattles": // For Tournament Restart Procedures
                    File saveDic = new File(GameAPI.path + "/saves/" + SmartTools.dateToString(Calendar.getInstance().getTime()) + "/");
                    if (saveDic.exists() || saveDic.mkdirs()) {
                        for (String key : GameAPI.loadedRooms.keySet()) {
                            for (Room room : GameAPI.loadedRooms.get(key)) {
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
                        GameAPI.plugin.getLogger().warning(GameAPI.getLanguage().getTranslation(commandSender, "command.saveBattle.folderCreatedFailed", saveDic.getPath()));
                    }
                    break;
                case "addrank":
                    if (commandSender.isPlayer()) {
                        if (strings.length == 3) {
                            Player player = (Player) commandSender;
                            EntityTools.addRankingList(player, strings[1], strings[2], RankingSortSequence.DESCEND);
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.useInGame"));
                    }
                    break;
                case "stoproom":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                                GameAPI.playerRoomHashMap.remove(player);
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STOPPED);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.stop"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "halt":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.getRoomStatus() != RoomStatus.ROOM_STATUS_GameStart) {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.room.notProcessing"));
                                return true;
                            }
                            for (Player player : room.getPlayers()) {
                                player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation(), null);
                                player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                            }
                            room.setRoomStatus(RoomStatus.ROOM_HALTED);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.halt"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "restart":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            for (Player player : room.getPlayers()) {
                                if (player.isOnline()) {
                                    player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation().getLocation(), null);
                                    player.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                                } else {
                                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.playerOffline", player.getName()));
                                }
                            }
                            room.setRoomStatus(RoomStatus.ROOM_STATUS_GameStart);
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.battle.restart"));
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "status":
                    commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.getting"));
                    if (GameAPI.loadedRooms.size() > 0) {
                        for (Map.Entry<String, List<Room>> game : GameAPI.loadedRooms.entrySet()) {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.title", game));
                            List<Room> rooms = game.getValue();
                            if (rooms.size() > 0) {
                                for (Room room : rooms) {
                                    if (room.getRoomRule().isNeedPreStartPass()) {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.needStartPass", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
                                    } else {
                                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.show.tag.common", room.getRoomName(), room.getRoomStatus().toString(), room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
                                    }
                                }
                            } else {
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.noRoomLoaded"));
                            }
                        }
                    } else {
                        commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.status.noGameLoaded"));
                    }
                    break;
                case "roomstart":
                    if (strings.length == 3) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isPreStartPass()) {
                                room.setPreStartPass(true);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.startPass.endowed", room.getRoomName()));
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "setpwd":
                    if (strings.length == 4) {
                        Room room = Room.getRoom(strings[1], strings[2]);
                        if (room != null) {
                            if (room.isPreStartPass()) {
                                room.setJoinPassword(strings[3]);
                                commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.setPassword", strings[3]));
                            }
                        } else {
                            commandSender.sendMessage(GameAPI.getLanguage().getTranslation(commandSender, "command.error.roomNotFound"));
                        }
                    }
                    break;
                case "test":
                    String name = "test_" + UUID.randomUUID();
                    if (WorldTools.loadLevelFromBackUp(name, "ParkourSquare-Easy")) {
                        GameAPI.plugin.getLogger().alert("加载世界测试完成，准备测试删除世界...");
                        Server.getInstance().getScheduler().scheduleDelayedTask(GameAPI.plugin, () -> {
                            WorldTools.unloadLevel(Server.getInstance().getLevelByName(name), true);
                            if (Server.getInstance().isLevelLoaded(name)) {
                                GameAPI.plugin.getLogger().alert("删除世界失败");
                            } else {
                                GameAPI.plugin.getLogger().alert("删除世界成功");
                            }
                        }, 40, true);
                    }
                    break;
            }
        }
        return true;
    }

    public LinkedHashMap<String, Object> getPlayerData(Player player) {
        LinkedHashMap<String, Object> maps = new LinkedHashMap<>();
        maps.put("Location", player.getX() + ":" + player.getY() + ":" + player.getZ() + ":" + player.getLevel().getName());
        maps.put("Health", player.getHealth());
        List<String> invs = new ArrayList<>();
        player.getInventory().getContents().values().forEach(item -> invs.add(InventoryTools.getItemString(item)));
        maps.put("Inventory", invs);
        List<String> armors = new ArrayList<>();
        for (Item item : player.getInventory().getArmorContents()) {
            armors.add(InventoryTools.getItemString(item));
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

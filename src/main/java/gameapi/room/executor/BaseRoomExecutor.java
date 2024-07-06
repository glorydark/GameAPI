package gameapi.room.executor;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import gameapi.GameAPI;
import gameapi.room.Room;
import gameapi.tools.FireworkTools;
import gameapi.tools.SoundTools;
import gameapi.tools.TipsTools;
import gameapi.utils.AdvancedLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is meant to design the basic operations,
 * including teleporting players to spawns, sending tips.
 */
public class BaseRoomExecutor extends RoomExecutor {

    protected Room room;

    public BaseRoomExecutor(Room room) {
        this.room = room;
    }

    @Override
    public void onWait() {
        List<Player> senders = new ArrayList<>(room.getPlayers());
        senders.addAll(room.getSpectators());
        if (room.getPlayers().size() >= room.getMinPlayer()) {
            if (!room.isAllowedToStart()) {
                for (Player player : senders) {
                    player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.wait.need_start_pass"));
                }
            }
        } else {
            for (Player player : senders) {
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.wait.wait_for_players", room.getPlayers().size(), room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
            }
        }
    }

    @Override
    public void onPreStart() {
        List<Player> senders = new ArrayList<>(room.getPlayers());
        senders.addAll(room.getSpectators());
        for (Player player : senders) {
            int lastSecond = room.getWaitTime() - room.getTime();
            if (lastSecond > 3) {
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.preStart.countdown", lastSecond));
            } else {
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.preStart.countdown.ready", lastSecond));
            }
        }
    }

    @Override
    public void onReadyStart() {
        List<Player> senders = new ArrayList<>(room.getPlayers());
        senders.addAll(room.getSpectators());
        int lastSec = room.getGameWaitTime() - room.getTime();
        for (Player p : senders) {
            if (lastSec > 10) {
                SoundTools.addSoundToPlayer(p, Sound.NOTE_HARP, 1.0f, 1.0f);
                p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown", room.getGameWaitTime() - room.getTime()));
            } else {
                if (lastSec == 1) {
                    SoundTools.addSoundToPlayer(p, Sound.NOTE_FLUTE, 1.0f, 1.0f);
                } else {
                    SoundTools.addSoundToPlayer(p, Sound.NOTE_BASS, 1.0f, 1.0f);
                }
                switch (lastSec) {
                    case 10:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.ten"));
                        break;
                    case 9:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.nine"));
                        break;
                    case 8:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.eight"));
                        break;
                    case 7:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.seven"));
                        break;
                    case 6:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.six"));
                        break;
                    case 5:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.five"));
                        break;
                    case 4:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.four"));
                        break;
                    case 3:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.three"));
                        break;
                    case 2:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.two"));
                        break;
                    case 1:
                        p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown.one"));
                        break;
                }
            }
        }
    }

    @Override
    public void onGameEnd() {
        List<Player> senders = new ArrayList<>(room.getPlayers());
        senders.addAll(room.getSpectators());
        for (Player player : senders) {
            player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.gameEnd", room.getGameEndTime() - room.getTime()));
        }
    }

    @Override
    public void onCeremony() {
        List<Player> senders = new ArrayList<>(room.getPlayers());
        senders.addAll(room.getSpectators());
        for (Player p : senders) {
            p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.ceremony", room.getCeremonyTime() - room.getTime()));
            if (p.getGamemode() == 3) {
                continue;
            }
            FireworkTools.spawnRandomFirework(p.getLocation());
        }
    }

    @Override
    public void onNextRoundPreStart() {
        for (Player p : room.getPlayers()) {
            p.sendActionBar("§l§e下一场游戏开始还剩 §l§6" + (room.getNextRoundPreStartTime() - room.getTime()) + " §l§e秒");
        }
    }

    @Override
    public void beginReadyStart() {
    }

    @Override
    public void beginGameStart() {
        if (GameAPI.tipsEnabled) {
            for (Level playLevel : room.getPlayLevels()) {
                for (Player player : room.getPlayers()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
                for (Player player : room.getSpectators()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
            }
        }
        for (Player p : room.getPlayers()) {
            p.removeAllEffects();
            p.getFoodData().reset();
            p.setGamemode(room.getRoomRule().getGameMode());
        }
        List<Player> senders = new ArrayList<>(room.getPlayers());
        senders.addAll(room.getSpectators());
        for (Player sender : senders) {
            sender.sendTitle(GameAPI.getLanguage().getTranslation(sender, "room.title.start"), GameAPI.getLanguage().getTranslation(sender, "room.subtitle.start"));
            sender.sendActionBar(GameAPI.getLanguage().getTranslation(sender, "room.actionbar.readyStart.countdown.zero"));
        }

        if (!room.getRoomRule().isAutoTeleport()) {
            return;
        }
        List<AdvancedLocation> startSpawns = room.getStartSpawn();
        if (room.getTeams().size() > 0) {
            room.allocatePlayerToTeams();
            room.getPlayers().forEach(room::teleportToSpawn);
            room.getSpectators().forEach(player -> {
                if (room.getSpectatorSpawn().size() != 0) {
                    Random random = new Random(room.getSpectatorSpawn().size());
                    AdvancedLocation location = room.getSpectatorSpawn().get(random.nextInt(room.getSpectatorSpawn().size()));
                    location.teleport(player);
                } else {
                    if (room.getStartSpawn().size() != 0) {
                        Random random = new Random(room.getStartSpawn().size());
                        AdvancedLocation location = room.getStartSpawn().get(random.nextInt(room.getStartSpawn().size()));
                        location.teleport(player);
                    } else {
                        player.teleport(room.getPlayers().get(0).getLocation());
                    }
                }
            });
        } else {
            if (startSpawns.size() > 1) {
                for (Player p : room.getPlayers()) {
                    if (room.getRoomProperty(p.getName(), "spawnIndex") == null) {
                        Random random = new Random(System.currentTimeMillis());
                        AdvancedLocation location = startSpawns.get(random.nextInt(startSpawns.size()));
                        location.teleport(p);
                    } else {
                        AdvancedLocation location = startSpawns.get(room.getPlayerProperty(p.getName(), "spawnIndex", 0));
                        location.teleport(p);
                    }
                }
            } else if (room.getStartSpawn().size() == 1) {
                AdvancedLocation location = startSpawns.get(0);
                for (Player p : room.getPlayers()) {
                    location.teleport(p);
                }
            }
        }
    }

    @Override
    public void beginGameEnd() {
    }

    public Room getRoom() {
        return room;
    }
}

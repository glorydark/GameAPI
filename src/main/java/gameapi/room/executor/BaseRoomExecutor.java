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
        List<Player> senders = new ArrayList<>(this.room.getPlayers());
        senders.addAll(this.room.getSpectators());
        if (this.room.getPlayers().size() >= this.room.getMinPlayer()) {
            if (!this.room.isAllowedToStart()) {
                for (Player player : senders) {
                    player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.wait.need_start_pass"));
                }
            }
        } else {
            for (Player player : senders) {
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.wait.wait_for_players", this.room.getPlayers().size(), this.room.getMinPlayer(), room.getMinPlayer() - room.getPlayers().size()));
            }
        }
    }

    @Override
    public void onPreStart() {
        List<Player> senders = new ArrayList<>(this.room.getPlayers());
        senders.addAll(this.room.getSpectators());
        for (Player player : senders) {
            int lastSecond = this.room.getWaitTime() - this.room.getTime();
            if (lastSecond > 3) {
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.preStart.countdown", lastSecond));
            } else {
                player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.preStart.countdown.ready", lastSecond));
            }
        }
    }

    @Override
    public void onReadyStart() {
        List<Player> senders = new ArrayList<>(this.room.getPlayers());
        senders.addAll(this.room.getSpectators());
        int lastSec = this.room.getGameWaitTime() - this.room.getTime();
        for (Player p : senders) {
            if (lastSec > 10) {
                SoundTools.addSoundToPlayer(p, Sound.NOTE_HARP, 1.0f, 1.0f);
                p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.readyStart.countdown", this.room.getGameWaitTime() - this.room.getTime()));
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
        List<Player> senders = new ArrayList<>(this.room.getPlayers());
        senders.addAll(this.room.getSpectators());
        for (Player player : senders) {
            player.sendActionBar(GameAPI.getLanguage().getTranslation(player, "room.actionbar.gameEnd", this.room.getGameEndTime() - this.room.getTime()));
        }
    }

    @Override
    public void onCeremony() {
        List<Player> senders = new ArrayList<>(this.room.getPlayers());
        senders.addAll(this.room.getSpectators());
        for (Player p : senders) {
            p.sendActionBar(GameAPI.getLanguage().getTranslation(p, "room.actionbar.ceremony", this.room.getCeremonyTime() - this.room.getTime()));
            if (p.getGamemode() == 3) {
                continue;
            }
            FireworkTools.spawnRandomFirework(p.getLocation());
        }
    }

    @Override
    public void onNextRoundPreStart() {
        for (Player p : this.room.getPlayers()) {
            p.sendActionBar("§l§e下一场游戏开始还剩 §l§6" + (this.room.getNextRoundPreStartTime() - this.room.getTime()) + " §l§e秒");
        }
    }

    @Override
    public void beginReadyStart() {
    }

    @Override
    public void beginGameStart() {
        if (GameAPI.getInstance().isTipsEnabled()) {
            for (Level playLevel : this.room.getPlayLevels()) {
                for (Player player : this.room.getPlayers()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
                for (Player player : this.room.getSpectators()) {
                    TipsTools.closeTipsShow(playLevel.getName(), player);
                }
            }
        }
        for (Player p : this.room.getPlayers()) {
            p.removeAllEffects();
            p.getFoodData().reset();
            p.setGamemode(this.room.getRoomRule().getGameMode());
        }
        List<Player> senders = new ArrayList<>(this.room.getPlayers());
        senders.addAll(this.room.getSpectators());
        for (Player sender : senders) {
            sender.sendTitle(GameAPI.getLanguage().getTranslation(sender, "room.title.start"), GameAPI.getLanguage().getTranslation(sender, "room.subtitle.start"));
            sender.sendActionBar(GameAPI.getLanguage().getTranslation(sender, "room.actionbar.readyStart.countdown.zero"));
        }

        if (!this.room.getRoomRule().isAutoStartTeleport()) {
            return;
        }
        List<AdvancedLocation> startSpawns = this.room.getStartSpawn();
        if (this.room.getTeams().size() > 0) {
            if (this.room.getRoomRule().isAutoAllocatePlayerToTeam()) {
                this.room.allocatePlayerToTeams();
            }
            this.room.getPlayers().forEach(room::teleportToSpawn);
        } else {
            if (startSpawns.size() > 1) {
                this.room.getPlayers().forEach(room::teleportToSpawn);
            } else if (this.room.getStartSpawn().size() == 1) {
                AdvancedLocation location = startSpawns.get(0);
                for (Player p : this.room.getPlayers()) {
                    location.teleport(p);
                }
            }
        }
        this.room.getSpectators().forEach(player -> {
            if (this.room.getSpectatorSpawn().size() != 0) {
                Random random = new Random(this.room.getSpectatorSpawn().size());
                AdvancedLocation location = this.room.getSpectatorSpawn().get(random.nextInt(this.room.getSpectatorSpawn().size()));
                location.teleport(player);
            } else {
                if (this.room.getStartSpawn().size() != 0) {
                    Random random = new Random(this.room.getStartSpawn().size());
                    AdvancedLocation location = this.room.getStartSpawn().get(random.nextInt(this.room.getStartSpawn().size()));
                    location.teleport(player);
                } else {
                    player.teleport(this.room.getPlayers().get(0).getLocation());
                }
            }
        });
    }

    @Override
    public void beginGameEnd() {
    }

    public Room getRoom() {
        return this.room;
    }
}

package gameapi.manager.music;

import cn.nukkit.Player;
import gameapi.GameAPI;
import gameapi.manager.music.data.OggMusicData;
import gameapi.room.Room;
import gameapi.tools.SoundTools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author glorydark
 */
public class OggMusicManager {

    protected final List<Player> stopMusicPlayers = new ArrayList<>();
    protected Room room;
    protected Map<String, OggMusicData> songMap;
    protected PlayType playType;
    protected int currentTick = 0;
    protected long lastPlayed = 0L;
    protected boolean stopped = true;
    private String currentSongId = "";

    public OggMusicManager(Room room, PlayType playType) {
        this.room = room;
        this.songMap = new LinkedHashMap<>();
        this.playType = playType;
    }

    public void start() {
        if (!this.stopped) {
            return;
        }
        this.reset();
        this.stopped = false;
    }

    public void onTick() {
        if (this.currentSongId.isEmpty()) {
            if (this.songMap.isEmpty()) {
                this.stopped = true;
                GameAPI.getGameDebugManager().error("Found the empty song list, room: " + this.room.getRoomName());
                return;
            }
            this.currentSongId = new ArrayList<>(this.songMap.entrySet()).get(0).getKey();
            if (this.currentSongId.isEmpty()) {
                this.stopped = true;
                GameAPI.getGameDebugManager().error("Found the song null, stopped playing for the room " + this.room.getRoomName());
                return;
            }
            this.setPlayingSong(this.currentSongId);
            return;
        }
        boolean isFinish = this.currentTick / 20 >= this.songMap.get(this.currentSongId).getLength();
        if (isFinish) {
            if (this.getNextSong() == null) {
                GameAPI.getGameDebugManager().info("Finish playing, stopping music player for the room: " + this.room.getRoomName());
                this.stopped = true;
            } else {
                this.lastPlayed = System.currentTimeMillis();
                this.currentSongId = this.getNextSong();
                this.setPlayingSong(this.currentSongId);
            }
            return;
        }
        this.currentTick++;
    }

    public void onQuit(Player player) {
        SoundTools.stopAllSound(player);
    }

    public void stop() {
        for (Player player : this.room.getPlayers()) {
            SoundTools.stopAllSound(player);
        }
        this.stopped = true;
    }

    public void reset() {
        this.stopped = true;
        this.currentTick = 0;
        this.lastPlayed = 0L;
        if (this.currentSongId.isEmpty()) {
            if (this.songMap.isEmpty()) {
                return;
            }
            this.currentSongId = new ArrayList<>(this.songMap.entrySet()).get(0).getKey();
            this.setPlayingSong(this.currentSongId);
        }
    }

    public void removeSong(String songId) {
        this.songMap.remove(songId);
    }

    public String getNextSong() {
        int currentSongIndex = new ArrayList<>(this.songMap.keySet()).indexOf(this.currentSongId);
        switch (this.playType) {
            case LIST_LOOP:
                if (currentSongIndex + 1 >= this.songMap.size()) {
                    currentSongIndex = 0;
                } else {
                    currentSongIndex += 1;
                }
                return new ArrayList<>(this.songMap.entrySet()).get(currentSongIndex).getKey();
            case SINGLE_TRACK_LOOP:
                return new ArrayList<>(this.songMap.entrySet()).get(currentSongIndex).getKey();
            case SHUFFLE:
                return new ArrayList<>(this.songMap.entrySet()).get(ThreadLocalRandom.current().nextInt(this.songMap.size())).getKey();
            case LIST:
            default:
                if (currentSongIndex + 1 >= this.songMap.size()) {
                    return "";
                }
                return new ArrayList<>(this.songMap.entrySet()).get(currentSongIndex + 1).getKey();
        }
    }

    public void setPlayingSong(String string) {
        if (this.songMap.containsKey(string)) {
            this.currentTick = 0;
            this.currentSongId = string;
            GameAPI.getGameDebugManager().info("[游戏|" + this.room.getGameName() + "] " + this.room.getRoomName() + " 开始播放 [" + new ArrayList<>(this.songMap.keySet()).indexOf(this.currentSongId) + "] " + this.currentSongId);
            OggMusicData songData = this.getSongMap().get(this.currentSongId);
            for (Player player : this.room.getPlayers()) {
                if (this.stopMusicPlayers.contains(player)) {
                    continue;
                }
                SoundTools.stopAllSound(player);
                SoundTools.addSoundToPlayer(player, this.currentSongId, songData.getVolume(), songData.getPitch());
            }
            for (Player player : this.room.getSpectators()) {
                if (this.stopMusicPlayers.contains(player)) {
                    continue;
                }
                SoundTools.stopAllSound(player);
                SoundTools.addSoundToPlayer(player, this.currentSongId, songData.getVolume(), songData.getPitch());
            }
        } else {
            GameAPI.getGameDebugManager().warning("[游戏|" + this.room.getGameName() + "] " + this.room.getRoomName() + " 播放失败，无法找到音乐，音乐名： " + this.currentSongId);
        }
    }

    public List<Player> getStopMusicPlayers() {
        return stopMusicPlayers;
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public Map<String, OggMusicData> getSongMap() {
        return songMap;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public PlayType getPlayType() {
        return playType;
    }

    public void setPlayType(PlayType playType) {
        this.playType = playType;
    }

    public Room getRoom() {
        return room;
    }

    public String getCurrentSongId() {
        return currentSongId;
    }

    public enum PlayType {
        SINGLE_TRACK_LOOP,
        LIST_LOOP,
        SHUFFLE,
        LIST
    }
}

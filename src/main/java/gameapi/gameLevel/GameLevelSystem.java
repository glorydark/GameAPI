package gameapi.gameLevel;

import gameapi.GameAPI;
import gameapi.locker.entry.LockerEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author glorydark
 * @date {2024/1/4} {22:22}
 */
public class GameLevelSystem {

    private final LinkedHashMap<Integer, List<LockerEntry>> levelLockerRewards = new LinkedHashMap<>();
    public HashSet<Integer> experienceSet = new HashSet<>();
    private GameLevelProvider provider;

    public GameLevelSystem(GameLevelProvider provider) {
        this.provider = provider;
    }

    public void addLevelLockerRewards(int level, LockerEntry entry) {
        if (!levelLockerRewards.containsKey(level)) {
            levelLockerRewards.put(level, new ArrayList<>());
        }
        levelLockerRewards.get(level).add(entry);
    }

    public LinkedHashMap<Integer, List<LockerEntry>> getLevelLockerRewards() {
        return levelLockerRewards;
    }

    public GameLevelProvider getProvider() {
        return provider;
    }

    public void setProvider(GameLevelProvider provider) {
        this.provider = provider;
    }

    public void setExperienceSet(HashSet<Integer> experienceSet) {
        this.experienceSet = experienceSet;
    }

    public int getLevelByExperience(int experience) {
        if (provider == null) {
            GameAPI.plugin.getLogger().error("Cannot use GameLevelSystem#getLevelByExperience because the provider is null");
            return Integer.MIN_VALUE;
        }
        if (experienceSet.isEmpty()) {
            return 0;
        }
        int level = 0;
        int tempExp = experience;
        for (Integer integer : experienceSet) {
            if (tempExp >= integer) {
                tempExp = experience - integer;
                level++;
            }
        }
        return level;
    }

    public int getLevelTotalExp(int level) {
        if (provider == null) {
            GameAPI.plugin.getLogger().error("Cannot use GameLevelSystem#getLevelTotalExp because the provider is null");
            return Integer.MIN_VALUE;
        }
        int totalExp = 0;
        int tempLevel = 0;
        for (Integer levelExp : experienceSet) {
            if (tempLevel <= level) {
                totalExp += levelExp;
            }
        }
        return totalExp;
    }

    public int getLevelLackingExp(String player) {
        if (provider == null) {
            GameAPI.plugin.getLogger().error("Cannot use GameLevelSystem#getLevelLackingExp because the provider is null");
            return Integer.MIN_VALUE;
        }
        int level = GameAPI.system.getProvider().getPlayerLevel(player);
        int exp = GameAPI.system.getProvider().getPlayerExp(player);
        return getLevelTotalExp(level + 1) - exp;
    }

    public int getLevelSurplusExp(String player) {
        if (provider == null) {
            GameAPI.plugin.getLogger().error("Cannot use GameLevelSystem#getLevelLackingExp because the provider is null");
            return Integer.MIN_VALUE;
        }
        int exp = GameAPI.system.getProvider().getPlayerExp(player);
        return exp - getLevelTotalExp(GameAPI.system.getProvider().getPlayerLevel(player));
    }
}

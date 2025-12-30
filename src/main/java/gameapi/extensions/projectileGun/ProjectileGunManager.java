package gameapi.extensions.projectileGun;

import cn.nukkit.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author glorydark
 */
public class ProjectileGunManager {

    private static Map<String, ProjectileGun> projectileGuns = new LinkedHashMap<>();

    public static Map<Player, Integer> lastShootTick = new LinkedHashMap<>();

    public static List<Player> reloadPlayers = new ArrayList<>();

    public static void registerProjectileGun(ProjectileGun projectileGun) {
        projectileGuns.put(projectileGun.getIdentifier(), projectileGun);
    }

    @Nullable
    public static ProjectileGun getProjectileGun(String identifier) {
        return projectileGuns.get(identifier);
    }
}

package gameapi.extensions.projectileGun;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author glorydark
 */
public class ProjectileGunManager {

    private static final Map<String, ProjectileGun> projectileGuns = new LinkedHashMap<>();

    public static void registerProjectileGun(ProjectileGun projectileGun) {
        projectileGuns.put(projectileGun.getIdentifier(), projectileGun);
    }

    @Nullable
    public static ProjectileGun getProjectileGun(String identifier) {
        return projectileGuns.get(identifier);
    }
}

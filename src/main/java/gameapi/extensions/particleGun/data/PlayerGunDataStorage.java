package gameapi.extensions.particleGun.data;

/**
 * @author glorydark
 */
public class PlayerGunDataStorage {

    protected boolean shooting;

    protected boolean reloading = false;

    protected long invalidateBeforeMillis = -1L;

    protected long lastChangeStateMillis = -1L;

    public PlayerGunDataStorage() {
        this.shooting = true;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    public long getInvalidateBeforeMillis() {
        return invalidateBeforeMillis;
    }

    public void setInvalidateBeforeMillis(long invalidateBeforeMillis) {
        this.invalidateBeforeMillis = invalidateBeforeMillis;
    }

    public long getLastChangeStateMillis() {
        return lastChangeStateMillis;
    }

    public void setLastChangeStateMillis(long lastChangeStateMillis) {
        this.lastChangeStateMillis = lastChangeStateMillis;
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }
}

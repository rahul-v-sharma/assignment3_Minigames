package au.com.mineauz.minigames.objects;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.scoreboard.ScoreboardMock;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.WeatherType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TestPlayer extends PlayerMock {

    private Long playerTime;
    private Long playerTimeoffset;
    private int foodLevel = 10;
    private float saturation = 10;
    private float exp;
    private int level;
    private float fallDistance;
    private boolean flying;
    private boolean allowFlight;
    private float walkSpeed;
    private float flyspeed;
    private int noDamageTicks;
    private WeatherType playerWeather = WeatherType.CLEAR;
    private final List<PotionEffect> effects = new ArrayList<>();
    private Scoreboard scoreboard = new ScoreboardMock();

    public TestPlayer(ServerMock server, String name, UUID uuid) {
        super(server, name, uuid);
    }

    @Override
    public void setPlayerTime(long time, boolean relative) {
        if (relative) {
            playerTimeoffset = time - this.getWorld().getTime();
        } else playerTime = time;
    }

    @Override
    public long getPlayerTime() {
        return playerTime + playerTimeoffset;
    }

    @Override
    public long getPlayerTimeOffset() {
        return playerTimeoffset;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return playerTimeoffset > 0;
    }

    @Override
    public void resetPlayerTime() {
        playerTime = this.getWorld().getTime();
        playerTimeoffset = 0L;
    }

    @Override
    public WeatherType getPlayerWeather() {
        return playerWeather;
    }

    @Override
    public void setPlayerWeather(@NotNull WeatherType type) {
        playerWeather = type;
    }

    @Override
    public void resetPlayerWeather() {
        playerWeather = WeatherType.CLEAR;
    }

    @Override
    public int getNoDamageTicks() {
        return noDamageTicks;
    }

    @Override
    public void setNoDamageTicks(int ticks) {
        noDamageTicks = ticks;
    }

    @Override
    public @NotNull Collection<PotionEffect> getActivePotionEffects() {
        return effects;
    }

    // Refactored playSound methods

    public static class SoundParams {
        private Location location;
        private String sound;
        private SoundCategory category;
        private float volume;
        private float pitch;

        public SoundParams(Location location, String sound, SoundCategory category, float volume, float pitch) {
            this.location = location;
            this.sound = sound;
            this.category = category;
            this.volume = volume;
            this.pitch = pitch;
        }

        public Location getLocation() {
            return location;
        }

        public String getSound() {
            return sound;
        }

        public SoundCategory getCategory() {
            return category;
        }

        public float getVolume() {
            return volume;
        }

        public float getPitch() {
            return pitch;
        }
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull Sound sound, float volume, float pitch) {
        playSound(new SoundParams(location, sound.name(), null, volume, pitch));
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String sound, float volume, float pitch) {
        playSound(new SoundParams(location, sound, null, volume, pitch));
    }

    @Override
    public void playSound(@NotNull Location location, Sound sound, @Nullable SoundCategory category, float volume, float pitch) {
        playSound(new SoundParams(location, sound.name(), category, volume, pitch));
    }

    @Override
    public void playSound(@NotNull Location location, @NotNull String sound, @Nullable SoundCategory category, float volume, float pitch) {
        playSound(new SoundParams(location, sound, category, volume, pitch));
    }

    private void playSound(SoundParams params) {
        String catString = (params.getCategory() == null) ? "null" : params.getCategory().name();
        System.out.println("Sound played: " + params.getSound().toUpperCase() + " from " + catString + " at Vol " + params.getVolume() + ":" + params.getPitch());
    }

    @Override
    public void updateInventory() {
        System.out.println(getInventory());
    }

    @Override
    public float getSaturation() {
        return saturation;
    }

    @Override
    public void setSaturation(float value) {
        this.saturation = value;
    }

    @Override
    public int getFoodLevel() {
        return foodLevel;
    }

    @Override
    public void setFoodLevel(int value) {
        this.foodLevel = value;
    }

    @Override
    public @NotNull Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setScoreboard(@NotNull Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        this.scoreboard = scoreboard;
    }

    @Override
    public void giveExp(int amount) {
        this.exp = this.exp + amount;
    }

    @Override
    public void giveExpLevels(int amount) {
        super.giveExpLevels(amount);
    }

    @Override
    public float getExp() {
        return exp;
    }

    @Override
    public void setExp(float exp) {
        this.exp = exp;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void setTotalExperience(int exp) {
        super.setTotalExperience(exp);
    }

    @Override
    public void setFlying(boolean value) {
        this.flying = value;
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        this.walkSpeed = value;
    }

    @Override
    public boolean getAllowFlight() {
        return allowFlight;
    }

    @Override
    public void setAllowFlight(boolean flight) {
        this.allowFlight = flight;
    }

    @Override
    public float getFlySpeed() {
        return flyspeed;
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        super.setFlySpeed(value);
    }

    @Override
    public boolean teleport(Location location) {
        System.out.println("Called Teleport on " + this.getName() + " to " + location.toString());
        return super.teleport(location);
    }

    @Override
    public void setFallDistance(float distance) {
        this.fallDistance = distance;
    }

    @Override
    public boolean isDead() {
        return false;
    }
}

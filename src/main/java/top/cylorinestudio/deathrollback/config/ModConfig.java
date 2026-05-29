package top.cylorinestudio.deathrollback.config;

public class ModConfig {
    public float backupThreshold;
    public int backupMessageInterval;

    public ModConfig(float backupThreshold, int backupMessageInterval) {
        this.backupThreshold = backupThreshold;
        this.backupMessageInterval = backupMessageInterval;
    }

    public ModConfig() {
        this(10, 30);
    }
}

package top.cylorinestudio.deathrollback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ModInitializer;
import top.cylorinestudio.deathrollback.config.ModConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeathRollback implements ModInitializer {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final Path CONFIG_PATH = Path.of(".", "config", "deathrollback.json");
    private static DeathRollback instance;

    private ModConfig config;

    @Override
    public void onInitialize() {
        if (Files.exists(CONFIG_PATH)) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
                this.config = GSON.fromJson(reader, new TypeToken<>() {
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            this.config = new ModConfig();
            saveConfig();
        }

        instance = this;
    }

    public ModConfig getConfig() {
        return this.config;
    }

    public void saveConfig() {
        try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(this.config, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DeathRollback getInstance() {
        return instance;
    }
}

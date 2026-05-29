package top.cylorinestudio.deathrollback.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;
import top.cylorinestudio.deathrollback.DeathRollback;
import top.cylorinestudio.deathrollback.config.ModConfig;

public class DeathRollbackModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ModConfig config = DeathRollback.getInstance().getConfig();
            ModConfig defaultConfig = new ModConfig();
            ConfigBuilder builder = ConfigBuilder.create();
            builder.setTitle(Text.translatable("config.title"));
            builder.setParentScreen(parent);

            ConfigCategory category = builder.getOrCreateCategory(Text.translatable("config.category.general"));
            category.addEntry(
                    builder.entryBuilder().startFloatField(Text.translatable("config.backup_threshold"), config.backupThreshold)
                            .setTooltip(Text.translatable("config.backup_threshold.description"))
                            .setMin(0.1F)
                            .setDefaultValue(defaultConfig.backupThreshold)
                            .setSaveConsumer(value -> config.backupThreshold = value)
                            .build()
            );
            category.addEntry(
                    builder.entryBuilder().startIntField(Text.translatable("config.backup_message_interval"), config.backupMessageInterval)
                            .setTooltip(Text.translatable("config.backup_message_interval.description"))
                            .setMin(1)
                            .setDefaultValue(defaultConfig.backupMessageInterval)
                            .setSaveConsumer(value -> config.backupMessageInterval = value)
                            .build()
            );
            builder.setSavingRunnable(DeathRollback.getInstance()::saveConfig);

            return builder.build();
        };
    }
}

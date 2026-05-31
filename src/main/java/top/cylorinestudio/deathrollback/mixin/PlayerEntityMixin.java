package top.cylorinestudio.deathrollback.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.cylorinestudio.deathrollback.DeathRollback;
import top.cylorinestudio.deathrollback.backup.BackupManager;
import top.cylorinestudio.deathrollback.config.ModConfig;
import top.cylorinestudio.deathrollback.screen.BackupActionScreen;

import java.nio.file.Path;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique private static long lastShowBackup = 0;

    @Inject(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;onDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
    private void onDamage(DamageSource source, float amount, CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        float remainingHealth = self.getHealth() - amount;

        MinecraftClient client = MinecraftClient.getInstance();
        Path worldPath = BackupActionScreen.getCurrentWorldPath();
        if (worldPath == null) return;

        ModConfig config = DeathRollback.getInstance().getConfig();
        client.execute(() -> {
            if (remainingHealth <= 0) {
                if (!BackupManager.hasBackup(worldPath.getFileName().toString())) return;
                client.setScreen(BackupActionScreen.rollback(amount));
            } else if (remainingHealth < config.backupThreshold) {
                int backupMessageInterval = config.backupMessageInterval;
                if (System.currentTimeMillis() - lastShowBackup < backupMessageInterval * 1000L) return;
                lastShowBackup = System.currentTimeMillis();
                client.setScreen(BackupActionScreen.create(remainingHealth, config.backupThreshold, config.backupMessageInterval));
            }
        });
    }
}

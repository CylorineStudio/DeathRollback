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
        client.execute(() -> {
            if (client.world == null || client.getServer() == null || client.getServer().isRemote()) return;
            String directoryName = client.getServer().session.getDirectoryName();
            Path worldPath = client.runDirectory.toPath().resolve("saves").resolve(directoryName);

            if (remainingHealth <= 0) {
                if (!BackupManager.hasBackup(directoryName)) return;
                client.setScreen(BackupActionScreen.rollback(b -> {
                    if (!b) return;

                    client.world.disconnect();
                    client.disconnect(new MessageScreen(Text.translatable("message.rolling_back")));

                    try {
                        BackupManager.rollback(worldPath);
                        client.createIntegratedServerLoader().start(directoryName, () -> {
                            client.setScreen(null);
                            client.setScreen(new TitleScreen());
                        });
                    } catch (Exception e) {
                        String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
                        client.getToastManager().add(new SystemToast(
                                SystemToast.Type.WORLD_BACKUP,
                                Text.translatable("selectWorld.edit.backupFailed"),
                                Text.literal(message)
                        ));
                    }
                }, amount));
            } else if (remainingHealth < DeathRollback.getInstance().getConfig().backupThreshold) {
                int backupMessageInterval = DeathRollback.getInstance().getConfig().backupMessageInterval;
                if (System.currentTimeMillis() - lastShowBackup < backupMessageInterval * 1000L) return;
                lastShowBackup = System.currentTimeMillis();
                client.setScreen(BackupActionScreen.create(b -> {
                    if (!b) return;
                    try {
                        client.getServer().execute(() -> client.getServer().saveAll(false, true, false));
                        BackupManager.createBackup(worldPath);
                    } catch (Exception e) {
                        String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
                        client.getToastManager().add(new SystemToast(
                                SystemToast.Type.WORLD_BACKUP,
                                Text.translatable("selectWorld.edit.backupFailed"),
                                Text.literal(message)
                        ));
                    }
                }, remainingHealth, 10, 30));
            }
        });
    }
}

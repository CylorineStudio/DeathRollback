package top.cylorinestudio.deathrollback.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import top.cylorinestudio.deathrollback.backup.BackupManager;

import java.nio.file.Path;
import java.util.Objects;

public class BackupActionScreen extends ConfirmScreen {
    private static final Text CREATE_BACKUP_TITLE = Text.translatable("backup_action.create_backup.title");
    private static final String CREATE_BACKUP_BODY_KEY = "backup_action.create_backup.body";

    private static final Text ROLL_BACK_TITLE = Text.translatable("backup_action.roll_back.title");
    private static final String ROLL_BACK_BODY_KEY = "backup_action.roll_back.body";

    private Screen nextScreen;

    protected BackupActionScreen(BooleanConsumer callback, Text title, Text message) {
        super(callback, title, message);
    }

    public static BackupActionScreen create(float health, float threshold, int interval) {
        return new BackupActionScreen(wrapCallback(b -> {
            if (!b) return;
            backup();
        }), CREATE_BACKUP_TITLE, Text.translatable(CREATE_BACKUP_BODY_KEY, health, threshold, interval));
    }

    public static BackupActionScreen rollback(float amount) {
        return new BackupActionScreen(wrapCallback(b -> {
            if (!b) return;
            rollback();
        }), ROLL_BACK_TITLE, Text.translatable(ROLL_BACK_BODY_KEY, amount));
    }

    private static BooleanConsumer wrapCallback(BooleanConsumer callback) {
        return b -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.currentScreen instanceof BackupActionScreen screen) {
                client.setScreen(null);
                client.setScreen(screen.nextScreen);
            }
            callback.accept(b);
        };
    }

    public void openOnFinish(Screen screen) {
        this.nextScreen = screen;
    }

    @Override
    public void close() {
        super.close();
        if (nextScreen != null && this.client != null) {
            this.client.setScreen(nextScreen);
        }
    }

    @Nullable
    public static Path getCurrentWorldPath() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.getServer() == null || client.getServer().isRemote()) return null;
        return BackupManager.toWorldPath(client.getServer().session.getDirectoryName());
    }

    public static boolean rollback() {
        MinecraftClient client = MinecraftClient.getInstance();
        Path worldPath = getCurrentWorldPath();
        if (worldPath == null) return false;

        Objects.requireNonNull(client.world).disconnect();
        client.disconnect(new MessageScreen(Text.translatable("message.rolling_back")));

        try {
            BackupManager.rollback(worldPath);
            client.createIntegratedServerLoader().start(worldPath.getFileName().toString(), () -> {
                client.setScreen(null);
                client.setScreen(new TitleScreen());
            });
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
            client.getToastManager().add(new SystemToast(
                    SystemToast.Type.WORLD_BACKUP,
                    Text.translatable("toast.rollback_failed"),
                    Text.literal(message)
            ));
            return false;
        }
        return true;
    }

    public static boolean backup() {
        Path worldPath = getCurrentWorldPath();
        if (worldPath == null) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        try {
            Objects.requireNonNull(client.getServer())
                    .execute(() -> client.getServer().saveAll(false, true, false));
            BackupManager.createBackup(worldPath);
        } catch (Exception e) {
            String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
            client.getToastManager().add(new SystemToast(
                    SystemToast.Type.WORLD_BACKUP,
                    Text.translatable("selectWorld.edit.backupFailed"),
                    Text.literal(message)
            ));
            return false;
        }
        return true;
    }
}

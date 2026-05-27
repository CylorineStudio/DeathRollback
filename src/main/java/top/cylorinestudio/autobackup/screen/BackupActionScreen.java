package top.cylorinestudio.autobackup.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BackupActionScreen extends ConfirmScreen {
    private static final Text CREATE_BACKUP_TITLE = Text.translatable("backup_action.create_backup.title");
    private static final String CREATE_BACKUP_BODY_KEY = "backup_action.create_backup.body";

    private static final Text ROLL_BACK_TITLE = Text.translatable("backup_action.roll_back.title");
    private static final String ROLL_BACK_BODY_KEY = "backup_action.roll_back.body";

    private Screen nextScreen;

    protected BackupActionScreen(BooleanConsumer callback, Text title, Text message) {
        super(callback, title, message);
    }

    public static BackupActionScreen create(BooleanConsumer callback, float health, float threshold, int cooldown) {
        return new BackupActionScreen(wrapCallback(callback), CREATE_BACKUP_TITLE, Text.translatable(CREATE_BACKUP_BODY_KEY, health, threshold, cooldown));
    }

    public static BackupActionScreen rollback(BooleanConsumer callback, float amount) {
        return new BackupActionScreen(wrapCallback(callback), ROLL_BACK_TITLE, Text.translatable(ROLL_BACK_BODY_KEY, amount));
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
}

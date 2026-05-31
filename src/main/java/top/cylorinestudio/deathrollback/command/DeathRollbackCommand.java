package top.cylorinestudio.deathrollback.command;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import top.cylorinestudio.deathrollback.backup.BackupManager;
import top.cylorinestudio.deathrollback.screen.BackupActionScreen;

import java.io.IOException;
import java.nio.file.Path;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class DeathRollbackCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) ->
                dispatcher.register(
                        literal("deathrollback")
                                .then(literal("backup")
                                        .executes(DeathRollbackCommand::backup)
                                )
                                .then(literal("rollback")
                                        .executes(DeathRollbackCommand::rollback)
                                )
                                .then(literal("delete")
                                        .executes(DeathRollbackCommand::delete)
                                )
                )));
    }

    private static int backup(CommandContext<FabricClientCommandSource> context) {
        Path worldPath = BackupActionScreen.getCurrentWorldPath();
        if (worldPath == null) {
            context.getSource().sendError(Text.translatable("commands.deathrollback.error.not_in_singleplayer_world"));
            return 0;
        }

        if (BackupActionScreen.backup()) {
            context.getSource().sendFeedback(Text.translatable("commands.deathrollback.success.backup"));
            return 1;
        }
        return 0;
    }

    private static int rollback(CommandContext<FabricClientCommandSource> context) {
        Path worldPath = BackupActionScreen.getCurrentWorldPath();
        if (worldPath == null) {
            context.getSource().sendError(Text.translatable("commands.deathrollback.error.not_in_singleplayer_world"));
            return 0;
        }

        if (!BackupActionScreen.rollback()) {
            context.getSource().sendError(Text.translatable("commands.deathrollback.error.no_backup"));
            return 0;
        }
        return 1;
    }

    private static int delete(CommandContext<FabricClientCommandSource> context) {
        Path worldPath = BackupActionScreen.getCurrentWorldPath();
        if (worldPath == null) {
            context.getSource().sendError(Text.translatable("commands.deathrollback.error.not_in_singleplayer_world"));
            return 0;
        }

        try {
            if (BackupManager.deleteBackup(worldPath.getFileName().toString())) {
                context.getSource().sendFeedback(Text.translatable("commands.deathrollback.success.delete"));
                return 1;
            } else {
                context.getSource().sendError(Text.translatable("commands.deathrollback.error.no_backup"));
                return 0;
            }
        } catch (IOException e) {
            String message = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
            context.getSource().sendError(Text.translatable("commands.deathrollback.error.delete_failed", message));
            return 0;
        }
    }
}

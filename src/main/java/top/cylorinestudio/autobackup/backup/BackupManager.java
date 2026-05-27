package top.cylorinestudio.autobackup.backup;

import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.path.SymlinkValidationException;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static LevelStorage.Session createSession(String directoryName) throws IOException, SymlinkValidationException {
        return MinecraftClient.getInstance().getLevelStorage().createSession(directoryName);
    }

    private static Path getBackupFilePath(String directoryName) {
        return MinecraftClient.getInstance().runDirectory.toPath().resolve("AutoBackup").resolve("backups").resolve(directoryName + ".zip");
    }

    public static void createBackup(Path worldDirectory) throws IOException {
        Path backupFilePath = getBackupFilePath(worldDirectory.getFileName().toString());
        if (Files.exists(backupFilePath)) {
            Files.delete(backupFilePath);
        }
        Files.createDirectories(backupFilePath.getParent());

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(backupFilePath))) {
            Files.walkFileTree(worldDirectory, new SimpleFileVisitor<>() {
                @Override
                public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().equals("session.lock")) return FileVisitResult.CONTINUE;
                    String relativePath = worldDirectory.relativize(file).toString();
                    ZipEntry entry = new ZipEntry(relativePath);
                    zipOutputStream.putNextEntry(entry);
                    try (InputStream fis = Files.newInputStream(file)) {
                        fis.transferTo(zipOutputStream);
                    }
                    zipOutputStream.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static boolean rollback(Path worldDirectory) throws IOException {
        Path backupFilePath = getBackupFilePath(worldDirectory.getFileName().toString());
        if (!Files.exists(worldDirectory) || !hasBackup(worldDirectory.getFileName().toString())) return false;

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(backupFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                Path destination = worldDirectory.resolve(entryName);
                if (!destination.normalize().startsWith(worldDirectory)) {
                    LOGGER.warn("Suspicious zip entry skipped: {}", entryName);
                    zipInputStream.closeEntry();
                    continue;
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(destination);
                } else {
                    Files.createDirectories(destination.getParent());
                    Files.copy(zipInputStream, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        }

        return true;
    }

    public static boolean hasBackup(String worldName) {
        return Files.exists(getBackupFilePath(worldName));
    }
}

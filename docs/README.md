<div align="center">
    <h1>DeathRollback</h1>
    <a href="https://modrinth.com/project/deathrollback"><img src="https://img.shields.io/modrinth/dt/deathrollback?style=flat&color=242629&logo=modrinth" alt="Downloads"></a>
    <img src="https://img.shields.io/github/stars/CylorineStudio/DeathRollback?style=flat&color=yellow" alt="Stars">
    <img src="https://img.shields.io/github/commit-activity/m/CylorineStudio/DeathRollback?style=flat&color=blue" alt="Activity">
    <img src="https://img.shields.io/github/contributors/CylorineStudio/DeathRollback?style=flat&color=green" alt="Contributors">
</div>

<br>

<div align="center">
    <span>English</span>
    <a href="/docs/README_zh_CN.md">简体中文</a>
</div>

**DeathRollback** is a Minecraft Survival/Hardcore utility mod that helps you create backups when your health is low and roll back your world upon death.

> [!NOTE]
> Some text was translated from Simplified Chinese by AI and may contain grammatical errors.

<table>
    <tr>
        <td><img src="/docs/img/backup_message_screen/en_us.png" alt="Backup Reminder Screen"></td>
        <td><img src="/docs/img/rollback/en_us.png" alt="Death Rollback Screen"></td>
    </tr>
</table>

## Features

- **In-Game Backup Reminders**: Triggers a backup prompt when the player's health drops below a configured threshold, allowing players to create backups without leaving the world.
- **Death Rollback**: Pops up a rollback prompt upon death, allowing you to safely revert to the latest backup after confirmation.
- **Highly Configurable**: Supports custom health thresholds for backups, reminder cooldowns, and more.

> [!WARNING]
> Due to backup mechanism limitations, this mod is **Singleplayer only** and will not work in Multiplayer or LAN worlds.

## Compatibility

- **Mod Loader**: Supports **Fabric** only.
- **Game Versions**: Currently guaranteed to be stable on **1.21 / 1.21.1**, with support for more versions planned for future updates.

## Dependencies

### Required
- **Fabric Loader**: `0.16.0` or higher
- **[Fabric API](https://modrinth.com/mod/fabric-api)**: Any version
- **[Cloth Config API](https://modrinth.com/mod/cloth-config)**: `15.0` or higher

### Optional
- **[Mod Menu](https://modrinth.com/mod/modmenu)**: `11.0` or higher
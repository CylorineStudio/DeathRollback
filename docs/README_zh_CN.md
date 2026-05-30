<div align="center">
    <h1>DeathRollback</h1>
    <a href="https://modrinth.com/project/deathrollback"><img src="https://img.shields.io/modrinth/dt/deathrollback?style=flat-square&color=242629&logo=modrinth" alt="Downloads"></a>
    <img src="https://img.shields.io/github/stars/CylorineStudio/DeathRollback?style=flat&color=yellow" alt="Stars">
    <img src="https://img.shields.io/github/commit-activity/m/CylorineStudio/DeathRollback?style=flat&color=blue" alt="Activity">
    <img src="https://img.shields.io/github/contributors/CylorineStudio/DeathRollback?style=flat&color=green" alt="Contributors">
</div>
<br>

**DeathRollback** 是一个 Minecraft 生存/极限辅助 Mod，它可以帮助你在生命值降低时创建备份，并在死亡时回滚。

<table>
    <tr>
        <td><img src="/docs/img/backup_message_screen/zh_cn.png" alt="备份提示页面"></td>
        <td><img src="/docs/img/rollback/zh_cn.png" alt="死亡回滚界面"></td>
    </tr>
</table>

## 功能特性

- **世界内备份提示**：当玩家生命值低于设定阈值时触发备份提示，玩家可以在世界内完成备份。
- **死亡回滚**：在玩家死亡时弹出回滚提示，确认后可以回到上一个备份。
- **高配置性**：支持自定义备份触发的生命值、提示冷却时间等。

## 兼容性

- **Mod 加载器**：仅支持 Fabric。
- **游戏版本**：目前仅能保证在 **1.21 / 1.21.11** 上稳定可用，更多版本的支持已在开发计划中。

## 依赖项

### 必要依赖
- Fabric Loader：`0.16.0` 及以上
- [Fabric API](https://modrinth.com/mod/fabric-api)：任意版本
- [Cloth Config API](https://modrinth.com/mod/cloth-config)：`15.0` 及以上

### 可选依赖
- [Mod Menu](https://modrinth.com/mod/modmenu)：`11.0` 及以上
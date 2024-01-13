# GameAPI

如果刚入门的nk开发者对小游戏有兴趣，可以大致看一下本源码。在我刚刚学习开发插件不久，我研究了一下name的murdermystery（当时还没有gamecore），然后编写成的这个小游戏前置，因此技术水平不会太高，同时也有很多地方需要改进。

当你大致能够理解房间的运作、房间功能的实现、房间事件的触发等内容后，您可以选择阅读SoBadFish的Gamedemo，进行进阶学习！[点我前往](https://github.com/SoBadFish/GameDemo)

非常感谢小窝内大家对本前置的测试，同时也希望本插件的源码对各位有帮助！

## 插件包含内容

- 简单的房间创建方法、支持临时房间、支持队伍创建，便捷设置房间规则、支持房间交流
- BossBarAPI
- 便捷管理、使用计分板（ScoreboardAPI）
- 更加容易管理药水效果（EasyEffect）
- TextEntity支持
- 排行榜支持（GameRecord）
- 支持背包保存、地图还原
- 与nk一样的事件监听器编写（GameListenerRegistry、GameListener、GameEventHandler）
- 支持方块交互效果的快捷编写（AdvancedBlockRegistry）
- 更多内容请自行摸索

## 本插件的目的

本插件不附带部分特殊功能，只简化了创建小游戏的过程。具体的功能需要开发者自行编写入自己的插件，详情可以看SheepWar和DRecknessHero。

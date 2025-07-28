package gameapi.utils;


import cn.nukkit.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Winfxk
 * @author Anders233
 */

public enum ItemIDSunName {
    /**
     * 石头
     */
    STONE("石头", 1, 0, "textures/blocks/stone.png"),
    /**
     * 花岗岩
     */
    STONE_GRANITE("花岗岩", 1, 1, "textures/blocks/stone_granite.png"),
    /**
     * 磨制花岗岩
     */
    STONE_GRANITE_POLISHED("磨制花岗岩", 1, 2, "textures/blocks/stone_granite_smooth.png"),
    /**
     * 闪长岩
     */
    STONE_DIORITE("闪长岩", 1, 3, "textures/blocks/stone_diorite.png"),
    /**
     * 磨制闪长岩
     */
    STONE_DIORITE_POLISHED("磨制闪长岩", 1, 4, "textures/blocks/stone_diorite_smooth.png"),
    /**
     * 安山岩
     */
    STONE_ANDESITE("安山岩", 1, 5, "textures/blocks/stone_andesite.png"),
    /**
     * 磨制安山岩
     */
    STONE_ANDESITE_POLISHED("磨制安山岩", 1, 6, "textures/blocks/stone_andesite_smooth.png"),
    /**
     * 草方块
     */
    GRASS("草方块", 2, 0, "textures/blocks/grass_side_carried.png"),
    /**
     * 泥土
     */
    DIRT("泥土", 3, 0, "textures/blocks/dirt.png"),
    /**
     * 圆石
     */
    COBBLESTONE("圆石", 4, 0, "textures/blocks/cobblestone.png"),
    /**
     * 橡树木板
     */
    PLANKS("橡树木板", 5, 0, "textures/blocks/planks_oak.png"),
    /**
     * 云杉木板
     */
    PLANKS_SPRUCE("云杉木板", 5, 1, "textures/blocks/planks_spruce.png"),
    /**
     * 桦木板
     */
    PLANKS_BIRCH("桦木板", 5, 2, "textures/blocks/planks_birch.png"),
    /**
     * 丛林树木板
     */
    PLANKS_JUNGLE("丛林树木板", 5, 3, "textures/blocks/planks_jungle.png"),
    /**
     * 金合欢木板
     */
    PLANKS_ACACIA("金合欢木板", 5, 4, "textures/blocks/planks_acacia.png"),
    /**
     * 深色橡木木板
     */
    PLANKS_BIG_OAK("深色橡木木板", 5, 5, "textures/blocks/planks_big_oak.png"),
    /**
     * 橡树苗
     */
    SAPLING("橡树苗", 6, 0, "textures/blocks/sapling_oak.png"),
    /**
     * 云杉树苗
     */
    SAPLING_SPRUCE("云杉树苗", 6, 1, "textures/blocks/sapling_spruce.png"),
    /**
     * 桦树苗
     */
    SAPLING_BIRCH("桦树苗", 6, 2, "textures/blocks/sapling_birch.png"),
    /**
     * 丛林树苗
     */
    SAPLING_JUNGLE("丛林树苗", 6, 3, "textures/blocks/sapling_jungle.png"),
    /**
     * 金合欢树苗
     */
    SAPLING_ACACIA("金合欢树苗", 6, 4, "textures/blocks/sapling_acacia.png"),
    /**
     * 深色橡树苗
     */
    SAPLING_ROOFED_OAK("深色橡树苗", 6, 5, "textures/blocks/sapling_roofed_oak.png"),
    /**
     * 基岩
     */
    BEDROCK("基岩", 7, 0, "textures/blocks/bedrock.png"),
    /**
     * 流动的水
     */
    FLOWING_WATER("流动的水", 8, 0, "textures/blocks/water_placeholder.png"),
    /**
     * 水
     */
    WATER("水", 9, 0, "textures/blocks/water_placeholder.png"),
    /**
     * 流动的岩浆
     */
    FLOWING_LAVA("流动的岩浆", 10, 0, "textures/blocks/lava_placeholder.png"),
    /**
     * 岩浆
     */
    LAVA("岩浆", 11, 0, "textures/blocks/lava_placeholder.png"),
    /**
     * 沙子
     */
    SAND("沙子", 12, 0, "textures/blocks/sand.png"),
    /**
     * 红沙
     */
    RED_SAND("红沙", 12, 1, "textures/blocks/red_sand.png"),
    /**
     * 砾石
     */
    GRAVEL("砾石", 13, 0, "textures/blocks/gravel.png"),
    /**
     * 金矿石
     */
    GOLD_ORE("金矿石", 14, 0, "textures/blocks/gold_ore.png"),
    /**
     * 铁矿石
     */
    IRON_ORE("铁矿石", 15, 0, "textures/blocks/iron_ore.png"),
    /**
     * 煤矿石
     */
    COAL_ORE("煤矿石", 16, 0, "textures/blocks/coal_ore.png"),
    /**
     * 橡木
     */
    LOG("橡木", 17, 0, "textures/blocks/log_oak.png"),
    /**
     * 云杉木
     */
    LOG_SPRUCE("云杉木", 17, 1, "textures/blocks/log_spruce.png"),
    /**
     * 桦木
     */
    LOG_BIRCH("桦木", 17, 2, "textures/blocks/log_birch.png"),
    /**
     * 丛林木
     */
    LOG_JUNGLE("丛林木", 17, 3, "textures/blocks/log_jungle.png"),
    /**
     * 橡树叶
     */
    LEAVES("橡树叶", 18, 0, "textures/blocks/leaves_oak_carried.tga"),
    /**
     * 云杉叶
     */
    LEAVES_SPRUCE_CARRIED("云杉叶", 18, 1, "textures/blocks/leaves_spruce_carried.tga"),
    /**
     * 桦树叶
     */
    LEAVES_BIRCH_CARRIED("桦树叶", 18, 2, "textures/blocks/leaves_birch_carried.tga"),
    /**
     * 丛林树叶
     */
    LEAVES_JUNGLE_CARRIED("丛林树叶", 18, 3, "textures/blocks/leaves_jungle_carried.tga"),
    /**
     * 干海绵
     */
    SPONGE("干海绵", 19, 0, "textures/blocks/sponge.png"),
    /**
     * 湿海绵
     */
    SPONGE_WET("湿海绵", 19, 1, "textures/blocks/sponge_wet.png"),
    /**
     * 玻璃
     */
    GLASS("玻璃", 20, 0, "textures/blocks/glass.png"),
    /**
     * 青金石矿
     */
    LAPIS_ORE("青金石矿", 21, 0, "textures/blocks/lapis_ore.png"),
    /**
     * 青金石块
     */
    LAPIS_BLOCK("青金石块", 22, 0, "textures/blocks/lapis_block.png"),
    /**
     * 发射器
     */
    DISPENSER("发射器", 23, 0, "textures/blocks/dispenser_front_horizontal.png"),
    /**
     * 沙石
     */
    SANDSTONE("沙石", 24, 0, "textures/blocks/sandstone_normal.png"),
    /**
     * 錾制沙石
     */
    SANDSTONE_CHISELED("錾制沙石", 24, 1, "textures/blocks/sandstone_carved.png"),
    /**
     * 光滑沙石
     */
    SANDSTONE_SMOOTH("光滑沙石", 24, 2, "textures/blocks/sandstone_smooth.png"),
    /**
     * 音符盒
     */
    NOTEBLOCK("音符盒", 25, 0, "textures/blocks/noteblock.png"),
    /**
     * 方块床
     */
    BED_BLOCK("方块床", 26, 0, "textures/blocks/bed_head_top.png"),
    /**
     * 动力铁轨
     */
    GOLDEN_RAIL("动力铁轨", 27, 0, "textures/blocks/rail_golden.png"),
    /**
     * 探测铁轨
     */
    DETECTOR_RAIL("探测铁轨", 28, 0, "textures/blocks/rail_detector.png"),
    /**
     * 粘性活塞
     */
    STICKY_PISTON("粘性活塞", 29, 0, "textures/blocks/piston_top_sticky.png"),
    /**
     * 蜘蛛网
     */
    WEB("蜘蛛网", 30, 0, "textures/blocks/web.png"),
    /**
     * 高草
     */
    TALLGRASS("高草", 31, 0, "textures/blocks/deadbush.png"),
    /**
     * 草
     */
    TALLGRASS_CARRIED("草", 31, 1, "textures/blocks/tallgrass_carried.tga"),
    /**
     * 蕨
     */
    FERN_CARRIED("蕨", 31, 2, "textures/blocks/fern_carried.tga"),
    /**
     * 枯死的灌木
     */
    DEAD_BUSH("枯死的灌木", 32, 0, "textures/blocks/deadbush.png"),
    /**
     * 活塞
     */
    PISTON("活塞", 33, 0, "textures/blocks/piston_top_normal.png"),
    /**
     * 活塞臂
     */
    PISTON_HEAD("活塞臂", 34, 0, "textures/blocks/piston_top_normal.png"),
    /**
     * 白色羊毛
     */
    WOOL("白色羊毛", 35, 0, "textures/blocks/wool_colored_white.png"),
    /**
     * 橙色羊毛
     */
    WOOL_COLORED_ORANGE("橙色羊毛", 35, 1, "textures/blocks/wool_colored_orange.png"),
    /**
     * 品红色羊毛
     */
    WOOL_COLORED_MAGENTA("品红色羊毛", 35, 2, "textures/blocks/wool_colored_magenta.png"),
    /**
     * 淡蓝色羊毛
     */
    WOOL_COLORED_LIGHT_BLUE("淡蓝色羊毛", 35, 3, "textures/blocks/wool_colored_light_blue.png"),
    /**
     * 黄色羊毛
     */
    WOOL_COLORED_YELLOW("黄色羊毛", 35, 4, "textures/blocks/wool_colored_yellow.png"),
    /**
     * 黄绿色羊毛
     */
    WOOL_COLORED_LIME("黄绿色羊毛", 35, 5, "textures/blocks/wool_colored_lime.png"),
    /**
     * 粉红色羊毛
     */
    WOOL_COLORED_PINK("粉红色羊毛", 35, 6, "textures/blocks/wool_colored_pink.png"),
    /**
     * 灰色羊毛
     */
    WOOL_COLORED_GRAY("灰色羊毛", 35, 7, "textures/blocks/wool_colored_gray.png"),
    /**
     * 淡灰色羊毛
     */
    WOOL_COLORED_SILVER("淡灰色羊毛", 35, 8, "textures/blocks/wool_colored_silver.png"),
    /**
     * 青色羊毛
     */
    WOOL_COLORED_CYAN("青色羊毛", 35, 9, "textures/blocks/wool_colored_cyan.png"),
    /**
     * 紫色羊毛
     */
    WOOL_COLORED_PURPLE("紫色羊毛", 35, 10, "textures/blocks/wool_colored_purple.png"),
    /**
     * 蓝色羊毛
     */
    WOOL_COLORED_BLUE("蓝色羊毛", 35, 11, "textures/blocks/wool_colored_blue.png"),
    /**
     * 棕色羊毛
     */
    WOOL_COLORED_BROWN("棕色羊毛", 35, 12, "textures/blocks/wool_colored_brown.png"),
    /**
     * 绿色羊毛
     */
    WOOL_COLORED_GREEN("绿色羊毛", 35, 13, "textures/blocks/wool_colored_green.png"),
    /**
     * 红色羊毛
     */
    WOOL_COLORED_RED("红色羊毛", 35, 14, "textures/blocks/wool_colored_red.png"),
    /**
     * 黑色羊毛
     */
    WOOL_COLORED_BLACK("黑色羊毛", 35, 15, "textures/blocks/wool_colored_black.png"),
    /**
     * 黄花
     */
    DANDELION("蒲公英", 37, 0, "textures/blocks/flower_dandelion.png"),
    /**
     * 罂粟
     */
    POPPY("虞美人", 38, 0, "textures/blocks/flower_rose.png"),
    /**
     * 蓝色的兰花
     */
    BLUE_ORCHID("兰花", 38, 1, "textures/blocks/flower_blue_orchid.png"),
    /**
     * 绒球葱
     */
    ALLIUM("绒球葱", 38, 2, "textures/blocks/flower_allium.png"),
    /**
     * 茜草花
     */
    AZURE_BLUET("兰花美耳草", 38, 3, "textures/blocks/flower_houstonia.png"),
    /**
     * 红色郁金香
     */
    FLOWER_TULIP_RED("红色郁金香", 38, 4, "textures/blocks/flower_tulip_red.png"),
    /**
     * 橙色郁金香
     */
    FLOWER_TULIP_ORANGE("橙色郁金香", 38, 5, "textures/blocks/flower_tulip_orange.png"),
    /**
     * 白色郁金香
     */
    FLOWER_TULIP_WHITE("白色郁金香", 38, 6, "textures/blocks/flower_tulip_white.png"),
    /**
     * 粉色郁金香
     */
    FLOWER_TULIP_PINK("粉色郁金香", 38, 7, "textures/blocks/flower_tulip_pink.png"),
    /**
     * 滨菊
     */
    FLOWER_OXEYE_DAISY("滨菊", 38, 8, "textures/blocks/flower_oxeye_daisy.png"),
    CORN_FLOWER("矢车菊", 38, 10, "textures/blocks/flower_cornflower.png"),
    LILY_OF_THE_VALLEY("铃兰", 38, 10, "textures/blocks/flower_lily_of_the_valley.png"),
    /**
     * 棕色蘑菇
     */
    BROWN_MUSHROOM("棕色蘑菇", 39, 0, "textures/blocks/mushroom_brown.png"),
    /**
     * 红色蘑菇
     */
    RED_MUSHROOM("红色蘑菇", 40, 0, "textures/blocks/mushroom_red.png"),
    /**
     * 黄金块
     */
    GOLD_BLOCK("黄金块", 41, 0, "textures/blocks/gold_block.png"),
    /**
     * 铁块
     */
    IRON_BLOCK("铁块", 42, 0, "textures/blocks/iron_block.png"),
    /**
     * 双石台阶
     */
    DOUBLE_STONE_SLAB("双石台阶", 43, 0, "textures/blocks/stone_slab_side.png"),
    /**
     * 双沙石台阶
     */
    SANDSTONE_BOTTOM("双沙石台阶", 43, 1, "textures/blocks/sandstone_bottom.png"),
    /**
     * 双橡木台阶
     */
    PLANKS_OAK("双橡木台阶", 43, 2, "textures/blocks/planks_oak.png"),
    /**
     * 双圆石台阶
     */
    DOUBLE_PEBBLE_STEPS("双圆石台阶", 43, 3, "textures/blocks/cobblestone.png"),
    /**
     * 双砖台阶
     */
    DOUBLE_BRICK_STEPS("双砖台阶", 43, 4, "textures/blocks/brick.png"),
    /**
     * 双石砖台阶
     */
    DOUBLE_STONE_BRICK_STEPS("双石砖台阶", 43, 5, "textures/blocks/stonebrick.png"),
    /**
     * 双石英台阶
     */
    DOUBLE_QUARTZ_STEPS("双石英台阶", 43, 6, "textures/blocks/nether_brick.png"),
    /**
     * 双地狱砖台阶
     */
    DOUBLE_HELL_BRICK_STEPS("双地狱砖台阶", 43, 7, "textures/blocks/quartz_block_top.png"),
    /**
     * 石台阶
     */
    STONE_SLAB("石台阶", 44, 0, "textures/blocks/stone_slab_top.png"),
    /**
     * 沙石台阶
     */
    SANDSTONE_TOP("沙石台阶", 44, 1, "textures/blocks/sandstone_top.png"),
    /**
     * 圆石台阶
     */
    COBBLESTONE_STEPS("圆石台阶", 44, 3, "textures/blocks/cobblestone.png"),
    /**
     * 砖台阶
     */
    BRICK_STEPS("砖台阶", 44, 4, "textures/blocks/brick.png"),
    /**
     * 石砖台阶
     */
    STONEBRICK_STEPS("石砖台阶", 44, 5, "textures/blocks/stonebrick.png"),
    /**
     * 石英台阶
     */
    NETHER_BRICK_STEPS("石英台阶", 44, 6, "textures/blocks/nether_brick.png"),
    /**
     * 地狱砖台阶
     */
    QUARTZ_BLOCK_TOP_STEPS("地狱砖台阶", 44, 7, "textures/blocks/quartz_block_top.png"),
    /**
     * 砖
     */
    BRICK("砖", 45, 0, "textures/blocks/brick.png"),
    /**
     * TNT
     */
    TNT("TNT", 46, 0, "textures/blocks/tnt_side.png"),
    /**
     * 书架
     */
    BOOKSHELF("书架", 47, 0, "textures/blocks/bookshelf.png"),
    /**
     * 苔石
     */
    MOSSY_COBBLESTONE("苔石", 48, 0, "textures/blocks/cobblestone_mossy.png"),
    /**
     * 黑曜石
     */
    OBSIDIAN("黑曜石", 49, 0, "textures/blocks/obsidian.png"),
    /**
     * 火把
     */
    TORCH("火把", 50, 0, "textures/blocks/torch_on.png"),
    /**
     * 火
     */
    FIRE("火", 51, 0, "textures/blocks/fire_0_placeholder.png"),
    /**
     * 刷怪笼
     */
    MOB_SPAWNER("刷怪笼", 52, 0, "textures/blocks/mob_spawner.png"),
    /**
     * 橡木阶梯
     */
    OAK_STAIRS("橡木阶梯", 53, 0, "textures/blocks/planks_oak.png"),
    /**
     * 箱子
     */
    CHEST("箱子", 54, 0, "textures/blocks/chest_front.png"),
    /**
     * 红石粉
     */
    REDSTONE_WIRE("红石粉", 55, 0, "textures/blocks/redstone_dust_line.png"),
    /**
     * 钻石矿
     */
    DIAMOND_ORE("钻石矿", 56, 0, "textures/blocks/diamond_ore.png"),
    /**
     * 钻石块
     */
    DIAMOND_BLOCK("钻石块", 57, 0, "textures/blocks/diamond_block.png"),
    /**
     * 工作台
     */
    CRAFTING_TABLE("工作台", 58, 0, "textures/blocks/crafting_table_top.png"),
    /**
     * 农田
     */
    FARMLAND("农田", 60, 0, "textures/blocks/farmland_dry.png"),
    /**
     * 熔炉
     */
    FURNACE("熔炉", 61, 0, "textures/blocks/furnace_front_off.png"),
    /**
     * 熔炉
     */
    @Deprecated
    FURNACE_BURN("熔炉", 62, 0, "textures/blocks/furnace_front_on.png"),
    /**
     * 木牌
     */
    SIGN_POST("木牌", 63, 0, "textures/items/sign_jungle.png"),
    /**
     * 梯子
     */
    LADDER("梯子", 65, 0, "textures/blocks/ladder.png"),
    /**
     * 铁轨
     */
    RAIL("铁轨", 66, 0, "textures/blocks/rail_normal.png"),
    /**
     * 圆石阶梯
     */
    STONE_STAIRS("圆石阶梯", 67, 0, "textures/blocks/cobblestone.png"),
    /**
     * 拉杆
     */
    LEVER("拉杆", 69, 0, "textures/blocks/lever.png"),
    /**
     * 石质压力板
     */
    STONE_PRESSURE_PLATE("石质压力板", 70, 0, "textures/blocks/stone.png"),
    /**
     * 木质压力板
     */
    WOODEN_PRESSURE_PLATE("木质压力板", 72, 0, "textures/blocks/planks_oak.png"),
    /**
     * 红石矿
     */
    REDSTONE_ORE("红石矿", 73, 0, "textures/blocks/redstone_ore.png"),
    /**
     * 发光的红石矿
     */
    LIT_REDSTONE_ORE("发光的红石矿", 74, 0, "textures/blocks/redstone_ore.png"),
    /**
     * 红石火把
     */
    UNLIT_REDSTONE_TORCH("红石火把", 75, 0, "textures/blocks/redstone_torch_off.png"),
    /**
     * 石质按钮
     */
    STONE_BUTTON("石质按钮", 77, 0, "textures/blocks/stone.png"),
    /**
     * 顶层雪
     */
    SNOW_LAYER("顶层雪", 78, 0, "textures/blocks/snow.png"),
    /**
     * 冰
     */
    ICE("冰", 79, 0, "textures/blocks/ice.png"),
    /**
     * 雪
     */
    SNOW("雪", 80, 0, "textures/blocks/snow.png"),
    /**
     * 仙人掌
     */
    CACTUS("仙人掌", 81, 0, "textures/blocks/cactus_side.tga"),
    /**
     * 粘土
     */
    CLAY("粘土", 82, 0, "textures/blocks/clay.png"),
    /**
     * 音乐盒
     */
    JUKEBOX("音乐盒", 84, 0, "textures/blocks/jukebox_top.png"),
    /**
     * 橡木围墙
     */
    FENCE("橡木围墙", 85, 0, "textures/blocks/planks_oak.png"),
    /**
     * 南瓜
     */
    PUMPKIN("南瓜", 86, 0, "textures/blocks/pumpkin_face_off.png"),
    /**
     * 地狱岩
     */
    NETHERRACK("地狱岩", 87, 0, "textures/blocks/netherrack.png"),
    /**
     * 灵魂沙
     */
    SOUL_SAND("灵魂沙", 88, 0, "textures/blocks/soul_sand.png"),
    /**
     * 萤石
     */
    GLOWSTONE("萤石", 89, 0, "textures/blocks/glowstone.png"),
    /**
     * 传送门
     */
    PORTAL("传送门", 90, 0, "textures/blocks/portal_placeholder.png"),
    /**
     * 南瓜灯
     */
    LIT_PUMPKIN("南瓜灯", 91, 0, "textures/blocks/pumpkin_face_on.png"),
    /**
     * 隐形基岩
     */
    STAINED_GLASS("隐形基岩", 95, 0, "textures/blocks/glass_white.png"),
    /**
     * 木质陷阱门
     */
    TRAPDOOR("木质陷阱门", 96, 0, "textures/blocks/trapdoor.png"),
    /**
     * 石头刷怪蛋
     */
    MONSTER_EGG("石头刷怪蛋", 97, 0, "textures/blocks/stone.png"),
    /**
     * 圆石刷怪蛋
     */
    COBBLESTONE_EGG("圆石刷怪蛋", 97, 1, "textures/blocks/Cobblestone.png"),
    /**
     * 石砖刷怪蛋
     */
    STONEBRICK("石砖刷怪蛋", 97, 2, "textures/blocks/stonebrick.png"),
    /**
     * 苔石砖
     */
    STONEBRICK_MOSSY("苔石砖", 98, 1, "textures/blocks/stonebrick_mossy.png"),
    /**
     * 裂石砖
     */
    STONEBRICK_CRACKED("裂石砖", 98, 2, "textures/blocks/stonebrick_cracked.png"),
    /**
     * 錾制石砖
     */
    STONEBRICK_CHISELED("錾制石砖", 98, 3, "textures/blocks/stonebrick_carved.png"),
    /**
     * 棕色蘑菇块
     */
    BROWN_MUSHROOM_BLOCK("棕色蘑菇块", 99, 0, "textures/blocks/mushroom_block_skin_brown.png"),
    /**
     * 红色蘑菇块
     */
    RED_MUSHROOM_BLOCK("红色蘑菇块", 100, 0, "textures/blocks/mushroom_block_skin_red.png"),
    /**
     * 铁栏杆
     */
    IRON_BARS("铁栏杆", 101, 0, "textures/blocks/iron_bars.png"),
    /**
     * 玻璃板
     */
    GLASS_PANE("玻璃板", 102, 0, "textures/blocks/glass_pane_top.png"),
    /**
     * 南瓜梗
     */
    PUMPKIN_STEM("南瓜梗", 104, 0, "textures/blocks/pumpkin_stem_disconnected.png"),
    /**
     * 藤蔓
     */
    VINE("藤蔓", 106, 0, "textures/blocks/vine_carried.png"),
    /**
     * 橡木围墙大门
     */
    FENCE_GATE("橡木围墙大门", 107, 0, "textures/blocks/planks_oak.png"),
    /**
     * 砖块阶梯
     */
    BRICK_STAIRS("砖块阶梯", 108, 0, "textures/blocks/brick.png"),
    /**
     * 石砖阶梯
     */
    STONE_BRICK_STAIRS("石砖阶梯", 109, 0, "textures/blocks/stonebrick.png"),
    /**
     * 菌丝
     */
    MYCELIUM("菌丝", 110, 0, "textures/blocks/mycelium_side.png"),
    /**
     * 睡莲
     */
    WATERLILY("睡莲", 111, 0, "textures/blocks/carried_waterlily.png"),
    /**
     * 地狱砖
     */
    NETHERBRICK("地狱砖", 405, 0, "textures/blocks/nether_brick.png"),
    /**
     * 地狱砖围墙
     */
    NETHER_BRICK_FENCE("地狱砖围墙", 113, 0, "textures/blocks/nether_brick.png"),
    /**
     * 地狱砖阶梯
     */
    NETHER_BRICK_STAIRS("地狱砖阶梯", 114, 0, "textures/blocks/nether_brick.png"),
    /**
     * 附魔台
     */
    ENCHANTING_TABLE("附魔台", 116, 0, "textures/blocks/enchanting_table_side.png"),

    LOG_ACACIA("金合欢木", 162, 0, "textures/blocks/log_acacia.png"),
    LOG_DARK_OAK("深色橡木", 162, 1, "textures/blocks/stripped_dark_oak_log.png"),
    /**
     * 酿造台
     */
    BREWING_STAND("酿造台", 379, 0, "textures/blocks/brewing_stand.png"),
    /**
     * 炼药锅
     */
    CAULDRON("炼药锅", 380, 0, "textures/blocks/cauldron_side.png"),
    /**
     * 末地门
     */
    END_PORTAL("末地门", 119, 0, "textures/blocks/end_portal.png"),
    /**
     * 末地传送门
     */
    END_PORTAL_FRAME("末地传送门", 120, 0, "textures/blocks/end_portal.png"),
    /**
     * 末地石
     */
    END_STONE("末地石", 121, 0, "textures/blocks/end_stone.png"),
    /**
     * 龙蛋
     */
    DRAGON_EGG("龙蛋", 122, 0, "textures/blocks/dragon_egg.png"),
    /**
     * 红石灯
     */
    REDSTONE_LAMP("红石灯", 123, 0, "textures/blocks/redstone_lamp_off.png"),
    /**
     * 沙石阶梯
     */
    SANDSTONE_STAIRS("沙石阶梯", 128, 0, "textures/blocks/sandstone_bottom.png"),
    /**
     * 绿宝石矿石
     */
    EMERALD_ORE("绿宝石矿石", 129, 0, "textures/blocks/emerald_ore.png"),
    /**
     * 末影箱
     */
    ENDER_CHEST("末影箱", 130, 0, "textures/blocks/ender_chest_front.png"),
    /**
     * 拌线钩
     */
    TRIPWIRE_HOOK("拌线钩", 131, 0, "textures/blocks/trip_wire_source.png"),
    /**
     * 拌线
     */
    TRIPWIRE("拌线", 132, 0, "textures/blocks/trip_wire.png"),
    /**
     * 绿宝石块
     */
    EMERALD_BLOCK("绿宝石块", 133, 0, "textures/blocks/emerald_block.png"),
    /**
     * 云杉木阶梯
     */
    SPRUCE_STAIRS("云杉木阶梯", 134, 0, "textures/blocks/planks_spruce.png"),
    /**
     * 桦木阶梯
     */
    BIRCH_STAIRS("桦木阶梯", 135, 0, "textures/blocks/planks_birch.png"),
    /**
     * 丛林木阶梯
     */
    JUNGLE_STAIRS("丛林木阶梯", 136, 0, "textures/blocks/planks_jungle.png"),
    /**
     * 命令方块
     */
    COMMAND_BLOCK("命令方块", 137, 0, "textures/blocks/command_block.png"),
    /**
     * 信标
     */
    BEACON("信标", 138, 0, "textures/blocks/beacon.png"),
    /**
     * 圆石墙
     */
    COBBLESTONE_WALL("圆石墙", 139, 0, "textures/blocks/cobblestone.png"),
    /**
     * 苔石墙
     */
    MOSS_COBBLESTONE_WALL("苔石墙", 139, 1, "textures/blocks/cobblestone_mossy.png"),
    /**
     * 土豆
     */
    POTATOES("土豆", 142, 0, "textures/blocks/potatoes_stage_3.png"),
    /**
     * 木质按钮
     */
    WOODEN_BUTTON("木质按钮", 143, 0, "textures/blocks/planks_oak.png"),
    /**
     * 铁砧
     */
    ANVIL("铁砧", 145, 0, "textures/blocks/anvil_top_damaged_0.png"),
    /**
     * 陷阱箱
     */
    TRAPPED_CHEST("陷阱箱", 146, 0, "textures/blocks/trapped_chest_front.png"),
    /**
     * 重力压力板(轻型)
     */
    LIGHT_WEIGHTED_PRESSURE_PLATE("重力压力板(轻型)", 147, 0, "textures/blocks/gold_block.png"),
    /**
     * 重力压力板(重型)
     */
    HEAVY_WEIGHTED_PRESSURE_PLATE("重力压力板(重型)", 148, 0, "textures/blocks/iron_block.png"),
    /**
     * 阳光传感器
     */
    DAYLIGHT_DETECTOR_INVERTED("阳光传感器", 178, 0, "textures/blocks/daylight_detector_inverted_top.png"),
    /**
     * 红石块
     */
    REDSTONE_BLOCK("红石块", 152, 0, "textures/blocks/redstone_block.png"),
    /**
     * 地狱石英矿石
     */
    QUARTZ_ORE("地狱石英矿石", 153, 0, "textures/blocks/quartz_ore.png"),
    /**
     * 漏斗
     */
    HOPPER("漏斗", 154, 0, "textures/blocks/hopper_top.png"),
    /**
     * 石英块
     */
    QUARTZ_BLOCK("石英块", 155, 0, "textures/blocks/quartz_block_top.png"),
    /**
     * 竖纹石英块
     */
    VERTICAL_GRAIN_QUARTZ_BLOCK("竖纹石英块", 155, 1, "textures/blocks/quartz_block_lines.png"),
    /**
     * 錾制石英块
     */
    QUARTZ_BLOCK_CHISELED("錾制石英块", 155, 2, "textures/blocks/quartz_block_chiseled_top.png"),
    /**
     * 石英阶梯
     */
    QUARTZ_STAIRS("石英阶梯", 156, 0, "textures/blocks/quartz_block_top.png"),
    /**
     * 橡木台阶
     */
    OAK_WOOD_STAIRS("橡木台阶", 158, 0, "textures/blocks/planks_oak.png"),
    /**
     * 白色粘土
     */
    WHITE_STAINED_HARDENED_CLAY("白色粘土", 159, 0, "textures/blocks/hardened_clay_stained_white.png"),
    /**
     * 橙色粘土
     */
    ORANGE_STAINED_HARDENED_CLAY("橙色粘土", 159, 1, "textures/blocks/hardened_clay_stained_orange.png"),
    /**
     * 品红色粘土
     */
    SOLFERINO_STAINED_HARDENED_CLAY("品红色粘土", 159, 2, "textures/blocks/hardened_clay_stained_magenta.png"),
    /**
     * 淡蓝色粘土
     */
    LIGHT_BLUE_STAINED_HARDENED_CLAY("淡蓝色粘土", 159, 3, "textures/blocks/hardened_clay_stained_light_blue.png"),
    /**
     * 黄色粘土
     */
    YELLOW_STAINED_HARDENED_CLAY("黄色粘土", 159, 4, "textures/blocks/hardened_clay_stained_yellow.png"),
    /**
     * 黄绿色粘土
     */
    LIME_STAINED_HARDENED_CLAY("黄绿色粘土", 159, 5, "textures/blocks/hardened_clay_stained_lime.png"),
    /**
     * 粉红色粘土
     */
    PINK_STAINED_HARDENED_CLAY("粉红色粘土", 159, 6, "textures/blocks/hardened_clay_stained_pink.png"),
    /**
     * 灰色粘土
     */
    GRAY_STAINED_HARDENED_CLAY("灰色粘土", 159, 7, "textures/blocks/hardened_clay_stained_gray.png"),
    /**
     * 淡灰色粘土
     */
    LIGHT_GRAY_STAINED_HARDENED_CLAY("淡灰色粘土", 159, 8, "textures/blocks/concrete_gray.png"),
    /**
     * 青色粘土
     */
    CYAN_STAINED_HARDENED_CLAY("青色粘土", 159, 9, "textures/blocks/hardened_clay_stained_lime.png"),
    /**
     * 紫色粘土
     */
    VIOLET_STAINED_HARDENED_CLAY("紫色粘土", 159, 10, "textures/blocks/hardened_clay_stained_purple.png"),
    /**
     * 蓝色粘土
     */
    BLUE_STAINED_HARDENED_CLAY("蓝色粘土", 159, 11, "textures/blocks/hardened_clay_stained_blue.png"),
    /**
     * 棕色粘土
     */
    BROWN_STAINED_HARDENED_CLAY("棕色粘土", 159, 12, "textures/blocks/hardened_clay_stained_brown.png"),
    /**
     * 绿色粘土
     */
    GREEN_STAINED_HARDENED_CLAY("绿色粘土", 159, 13, "textures/blocks/hardened_clay_stained_green.png"),
    /**
     * 红色粘土
     */
    RED_STAINED_HARDENED_CLAY("红色粘土", 159, 14, "textures/blocks/hardened_clay_stained_red.png"),
    /**
     * 黑色粘土
     */
    BLACK_STAINED_HARDENED_CLAY("黑色粘土", 159, 15, "textures/blocks/hardened_clay_stained_black.png"),
    /**
     * 白色玻璃板
     */
    WHITE_STAINED_GLASS_PANE("白色玻璃板", 160, 0, "textures/blocks/glass_pane_top_white.png"),
    /**
     * 橙色玻璃板
     */
    ORANGE_STAINED_GLASS_PANE("橙色玻璃板", 160, 1, "textures/blocks/glass_pane_top_orange.png"),
    /**
     * 品红色玻璃板
     */
    SOLFERINO_STAINED_GLASS_PANE("品红色玻璃板", 160, 2, "textures/blocks/glass_pane_top_magenta.png"),
    /**
     * 淡蓝色玻璃板
     */
    LIGHT_BLUE_STAINED_GLASS_PANE("淡蓝色玻璃板", 160, 3, "textures/blocks/glass_light_blue.png"),
    /**
     * 黄色玻璃板
     */
    YELLOW_STAINED_GLASS_PANE("黄色玻璃板", 160, 4, "textures/blocks/glass_yellow.png"),
    /**
     * 黄绿色玻璃板
     */
    LIME_STAINED_GLASS_PANE("黄绿色玻璃板", 160, 5, "textures/blocks/glass_pane_top_lime.png"),
    /**
     * 粉红色玻璃板
     */
    PINK_STAINED_GLASS_PANE("粉红色玻璃板", 160, 6, "textures/blocks/glass_pane_top_pink.png"),
    /**
     * 灰色玻璃板
     */
    GRAY_STAINED_GLASS_PANE("灰色玻璃板", 160, 7, "textures/blocks/glass_gray.png"),
    /**
     * 淡灰色玻璃板
     */
    LIGHT_GRAY_STAINED_GLASS_PANE("淡灰色玻璃板", 160, 8, "textures/blocks/glass_brown.png"),
    /**
     * 青色玻璃板
     */
    CYAN_STAINED_GLASS_PANE("青色玻璃板", 160, 9, "textures/blocks/glass_pane_top_cyan.png"),
    /**
     * 紫色玻璃板
     */
    VIOLET_STAINED_GLASS_PANE("紫色玻璃板", 160, 10, "textures/blocks/glass_pane_top_purple.png"),
    /**
     * 蓝色玻璃板
     */
    BLUE_STAINED_GLASS_PANE("蓝色玻璃板", 160, 11, "textures/blocks/glass_blue.png"),
    /**
     * 棕色玻璃板
     */
    BROWN_STAINED_GLASS_PANE("棕色玻璃板", 160, 12, "textures/blocks/glass_brown.png"),
    /**
     * 绿色玻璃板
     */
    GREEN_STAINED_GLASS_PANE("绿色玻璃板", 160, 13, "textures/blocks/glass_pane_top_green.png"),
    /**
     * 红色玻璃板
     */
    RED_STAINED_GLASS_PANE("红色玻璃板", 160, 14, "textures/blocks/glass_red.png"),
    /**
     * 黑色玻璃板
     */
    BLACK_STAINED_GLASS_PANE("黑色玻璃板", 160, 15, "textures/blocks/glass_black.png"),
    /**
     * 金合欢叶
     */
    ACACIA_LEAVES("金合欢叶", 161, 0, "textures/blocks/leaves_acacia_opaque.png"),
    /**
     * 深色橡树叶
     */
    DARK_OAK_LEAF("深色橡树叶", 161, 1, "textures/blocks/leaves_big_oak_opaque.png"),
    /**
     * 金合欢木
     */
    ACACIA_WOOD("金合欢木", 162, 0, "textures/blocks/log_acacia.png"),
    /**
     * 深色橡木
     */
    DARK_OAK("深色橡木", 162, 1, "textures/blocks/log_acacia.png"),
    /**
     * 金合欢木阶梯
     */
    ACACIA_STAIRS("金合欢木阶梯", 163, 0, "textures/blocks/planks_acacia.png"),
    /**
     * 深色橡木阶梯
     */
    DARK_OAK_STAIRS("深色橡木阶梯", 164, 0, "textures/blocks/planks_big_oak.png"),
    /**
     * 粘液块
     */
    SLIME("粘液块", 165, 0, "textures/blocks/slime.png"),
    /**
     * 铁门
     */
    IRON_DOOR("铁门", 330, 0, "textures/blocks/door_iron_upper.png"),
    /**
     * 海晶石
     */
    PRISMARINE("海晶石", 168, 0, "textures/blocks/prismarine_dark.png"), // todo: no right icon
    /**
     * 暗海晶石
     */
    DARK_PRISMARINE("暗海晶石", 168, 1, "textures/blocks/prismarine_dark.png"), // todo: no right icon
    /**
     * 海晶石砖
     */
    PRISMARINE_STONE_BRICK("海晶石砖", 168, 2, "textures/blocks/prismarine_bricks.png"), // todo: no right icon
    /**
     * 海晶灯
     */
    SEA_LANTERN("海晶灯", 169, 0, "textures/blocks/sea_lantern.png"),
    /**
     * 干草捆
     */
    HAY_BLOCK("干草捆", 170, 0, "textures/blocks/hay_block_side.png"),
    /**
     * 白色地毯
     */
    WHITE_CARPET("白色地毯", 171, 0, "textures/blocks/wool_colored_white.png"),
    /**
     * 橙色地毯
     */
    ORANGE_CARPET("橙色地毯", 171, 1, "textures/blocks/wool_colored_orange.png"),
    /**
     * 品红色地毯
     */
    SOLFERINO_CARPET("品红色地毯", 171, 2, "textures/blocks/wool_colored_magenta.png"),
    /**
     * 淡蓝色地毯
     */
    LIGHT_BLUE_CARPET("淡蓝色地毯", 171, 3, "textures/blocks/wool_colored_light_blue.png"),
    /**
     * 黄色地毯
     */
    YELLOW_CARPET("黄色地毯", 171, 4, "textures/blocks/wool_colored_yellow.png"),
    /**
     * 黄绿色地毯
     */
    LIME_CARPET("黄绿色地毯", 171, 5, "textures/blocks/wool_colored_lime.png"),
    /**
     * 粉红色地毯
     */
    PINK_CARPET("粉红色地毯", 171, 6, "textures/blocks/wool_colored_pink.png"),
    /**
     * 灰色地毯
     */
    GRAY_CARPET("灰色地毯", 171, 7, "textures/blocks/wool_colored_gray.png"),
    /**
     * 淡灰色地毯
     */
    LIGHT_GRAY_CARPET("淡灰色地毯", 171, 8, "textures/blocks/wool_colored_silver.png"),
    /**
     * 青色地毯
     */
    CYAN_CARPET("青色地毯", 171, 9, "textures/blocks/wool_colored_cyan.png"),
    /**
     * 紫色地毯
     */
    VIOLET_CARPET("紫色地毯", 171, 10, "textures/blocks/wool_colored_purple.png"),
    /**
     * 蓝色地毯
     */
    BLUE_CARPET("蓝色地毯", 171, 11, "textures/blocks/wool_colored_blue.png"),
    /**
     * 棕色地毯
     */
    BROWN_CARPET("棕色地毯", 171, 12, "textures/blocks/wool_colored_brown.png"),
    /**
     * 绿色地毯
     */
    GREEN_CARPET("绿色地毯", 171, 13, "textures/blocks/wool_colored_green.png"),
    /**
     * 红色地毯
     */
    RED_CARPET("红色地毯", 171, 14, "textures/blocks/wool_colored_red.png"),
    /**
     * 黑色地毯
     */
    BLACK_CARPET("黑色地毯", 171, 15, "textures/blocks/wool_colored_black.png"),
    /**
     * 硬化粘土
     */
    HARDENED_CLAY("硬化粘土", 172, 0, "textures/blocks/hardened_clay.png"),
    /**
     * 煤炭块
     */
    COAL_BLOCK("煤炭块", 173, 0, "textures/blocks/coal_block.png"),
    /**
     * 浮冰
     */
    PACKED_ICE("浮冰", 174, 0, "textures/blocks/ice_packed.png"),
    /**
     * 向日葵
     */
    SUNFLOWER("向日葵", 175, 0, "textures/blocks/double_plant_sunflower_front.png"),
    /**
     * 丁香
     */
    LILAC("丁香", 175, 1, "textures/blocks/flower_cornflower.png"),
    /**
     * 高草丛
     */
    TALL_GRASS("高草丛", 175, 2, "textures/blocks/double_plant_grass_carried.png"),
    /**
     * 大型蕨
     */
    LARGE_FERN("大型蕨", 175, 3, "textures/blocks/tallgrass.png"),
    /**
     * 玫瑰丛
     */
    ROSE_BUSH("玫瑰丛", 175, 4, "textures/blocks/sweet_berry_bush_stage3.png"),
    /**
     * 牡丹
     */
    PEONY("牡丹", 175, 5, "textures/blocks/flower_allium.png"),
    /**
     * 旗帜
     */
    STANDING_BANNER("旗帜", 176, 0, "textures/blocks/bone_block_side.png"),
    /**
     * 悬挂的旗帜
     */
    WALL_BANNER("悬挂的旗帜", 177, 0, "textures/blocks/bone_block_side.png"),
    /**
     * 红沙石
     */
    RED_SANDSTONE("红沙石", 179, 0, "textures/blocks/red_sandstone_bottom.png"),
    /**
     * 錾制红沙石
     */
    CHISELED_RED_SANDSTONE("錾制红沙石", 179, 1, "textures/blocks/red_sandstone_carved.png"),
    /**
     * 平滑红沙石
     */
    SMOOTH_RED_SANDSTONE("平滑红沙石", 179, 2, "textures/blocks/red_sandstone_smooth.png"),
    /**
     * 红沙石阶梯
     */
    RED_SANDSTONE_STAIRS("红沙石阶梯", 180, 0, "textures/blocks/red_sandstone_carved.png"),
    /**
     * 红沙石台阶
     */
    STONE_SLAB2("红沙石台阶", 182, 0, "textures/blocks/red_sandstone_smooth.png"),
    /**
     * 云杉围墙大门
     */
    SPRUCE_FENCE_GATE("云杉围墙大门", 183, 0, "textures/blocks/door_spruce_lower.png"),
    /**
     * 桦木围墙大门
     */
    BIRCH_FENCE_GATE("桦木围墙大门", 184, 0, "textures/blocks/door_birch_upper.png"),
    /**
     * 丛林木围墙大门
     */
    JUNGLE_FENCE_GATE("丛林木围墙大门", 185, 0, "textures/blocks/door_acacia_upper.png"),
    /**
     * 深色橡木围墙大门
     */
    DARK_OAK_FENCE_GATE("深色橡木围墙大门", 186, 0, "textures/blocks/door_dark_oak_lower.png"),
    /**
     * 金合欢木围墙大门
     */
    ACACIA_FENCE_GATE("金合欢木围墙大门", 187, 0, "textures/blocks/door_spruce_lower.png"),
    /**
     * 重复命令块
     */
    SPRUCE_FENCE("重复命令块", 188, 0, "textures/blocks/chain_command_block_conditional_mipmap.png"),
    /**
     * 链命令块
     */
    BIRCH_FENCE("链命令块", 189, 0, "textures/blocks/repeating_command_block_back_mipmap.png"),
    /**
     * 桦木门
     */
    BIRCH_DOOR("桦木门", 194, 0, "textures/blocks/door_birch_upper.png"),
    /**
     * 绿茵小道
     */
    END_ROD("绿茵小道", 198, 0, "textures/blocks/end_rod.png"),
    /**
     * 合唱花
     */
    CHORUS_FLOWER("合唱花", 200, 0, "textures/blocks/chorus_flower.png"),
    /**
     * 紫珀方块
     */
    PURPUR_BLOCK("紫珀方块", 201, 0, "textures/blocks/purpur_block.png"),

    PURPUR_PILLAR("紫珀柱子", 201, 2, "textures/blocks/purpur_pillar.png"),
    /**
     * 紫珀阶梯
     */
    PURPUR_STAIRS("紫珀阶梯", 203, 0, "textures/blocks/purpur_block.png"),
    /**
     * 潜影盒
     */
    SHULKER_BOX("潜影盒", 205, 0, "textures/items/shulker_shell.png"),
    /**
     * 末地石砖
     */
    END_BRICKS("末地石砖", 206, 0, "textures/blocks/end_bricks.png"),
    /**
     * 末地棒
     */
    GRASS_PATH("末地棒", 208, 0, "textures/blocks/grass_path_side.png"),
    /**
     * 末地门2
     */
    END_GATEWAY("末地门2", 209, 0, "textures/blocks/end_gateway.png"),
    /**
     * 白色潜影盒
     */
    SHULKER_COLORED_WHITE("白色潜影盒", 218, 0, "textures/blocks/shulker_top_white.png"),
    /**
     * 橙色潜影盒
     */
    SHULKER_COLORED_ORANGE("橙色潜影盒", 218, 1, "textures/blocks/shulker_top_orange.png"),
    /**
     * 品红色潜影盒
     */
    SHULKER_COLORED_MAGENTA("品红色潜影盒", 218, 2, "textures/blocks/shulker_top_magenta.png"),
    /**
     * 淡蓝色潜影盒
     */
    SHULKER_COLORED_LIGHT_BLUE("淡蓝色潜影盒", 218, 3, "textures/blocks/shulker_top_light_blue.png"),
    /**
     * 黄色潜影盒
     */
    SHULKER_COLORED_YELLOW("黄色潜影盒", 218, 4, "textures/blocks/shulker_top_yellow.png"),
    /**
     * 黄绿色潜影盒
     */
    SHULKER_COLORED_LIME("黄绿色潜影盒", 218, 5, "textures/blocks/shulker_top_lime.png"),
    /**
     * 粉红色潜影盒
     */
    SHULKER_COLORED_PINK("粉红色潜影盒", 218, 6, "textures/blocks/shulker_top_pink.png"),
    /**
     * 灰色潜影盒
     */
    SHULKER_COLORED_GRAY("灰色潜影盒", 218, 7, "textures/blocks/shulker_top_gray.png"),
    /**
     * 淡灰色潜影盒
     */
    SHULKER_COLORED_SILVER("淡灰色潜影盒", 218, 8, "textures/blocks/shulker_top_silver.png"),
    /**
     * 青色潜影盒
     */
    SHULKER_COLORED_CYAN("青色潜影盒", 218, 9, "textures/blocks/shulker_top_cyan.png"),
    /**
     * 紫色潜影盒
     */
    SHULKER_COLORED_PURPLE("紫色潜影盒", 218, 10, "textures/blocks/shulker_top_purple.png"),
    /**
     * 蓝色潜影盒
     */
    SHULKER_COLORED_BLUE("蓝色潜影盒", 218, 11, "textures/blocks/shulker_top_blue.png"),
    /**
     * 棕色潜影盒
     */
    SHULKER_COLORED_BROWN("棕色潜影盒", 218, 12, "textures/blocks/shulker_top_brown.png"),
    /**
     * 绿色潜影盒
     */
    SHULKER_COLORED_GREEN("绿色潜影盒", 218, 13, "textures/blocks/shulker_top_green.png"),
    /**
     * 红色潜影盒
     */
    SHULKER_COLORED_RED("红色潜影盒", 218, 14, "textures/blocks/shulker_top_red.png"),
    /**
     * 黑色潜影盒
     */
    SHULKER_COLORED_BLACK("黑色潜影盒", 218, 15, "textures/blocks/shulker_top_black.png"),
    /**
     * 白色玻璃
     */
    WHITE_STAINED_GLASS("白色玻璃", 160, 0, "textures/blocks/glass_white.png"),
    /**
     * 橙色玻璃
     */
    ORANGE_STAINED_GLASS("橙色玻璃", 160, 1, "textures/blocks/glass_orange.png"),
    /**
     * 品红色玻璃
     */
    MAGNETA_STAINED_GLASS("品红色玻璃", 160, 2, "textures/blocks/glass_magenta.png"),
    /**
     * 淡蓝色玻璃
     */
    LIGHT_BLUE_STAINED_GLASS("淡蓝色玻璃", 160, 3, "textures/blocks/glass_light_blue.png"),
    /**
     * 黄色玻璃
     */
    YELLOW_STAINED_GLASS("黄色玻璃", 160, 4, "textures/blocks/glass_yellow.png"),
    /**
     * 黄绿色玻璃
     */
    LIME_STAINED_GLASS("黄绿色玻璃", 160, 5, "textures/blocks/glass_lime.png"),
    /**
     * 粉红色玻璃
     */
    PINK_STAINED_GLASS("粉红色玻璃", 160, 6, "textures/blocks/glass_pink.png"),
    /**
     * 灰色玻璃
     */
    GRAY_STAINED_GLASS("灰色玻璃", 160, 7, "textures/blocks/glass_gray.png"),
    /**
     * 淡灰色玻璃
     */
    LIGHT_GRAY_STAINED_GLASS("淡灰色玻璃", 160, 8, "textures/blocks/glass_silver.png"),
    /**
     * 青色玻璃
     */
    CYAN_STAINED_GLASS("青色玻璃", 160, 9, "textures/blocks/glass_cyan.png"),
    /**
     * 紫色玻璃
     */
    PURPLE_STAINED_GLASS("紫色玻璃", 160, 10, "textures/blocks/glass_purple.png"),
    /**
     * 蓝色玻璃
     */
    BLUE_STAINED_GLASS("蓝色玻璃", 160, 11, "textures/blocks/glass_blue.png"),
    /**
     * 棕色玻璃
     */
    BROWN_STAINED_GLASS("棕色玻璃", 160, 12, "textures/blocks/glass_brown.png"),
    /**
     * 绿色玻璃
     */
    GREEN_STAINED_GLASS("绿色玻璃", 160, 13, "textures/blocks/glass_green.png"),
    /**
     * 红色玻璃
     */
    RED_STAINED_GLASS("红色玻璃", 160, 14, "textures/blocks/glass_red.png"),
    /**
     * 黑色玻璃
     */
    BLACK_STAINED_GLASS("黑色玻璃", 160, 15, "textures/blocks/glass_black.png"),
    /**
     * 白色混凝土
     */
    WHITE_CONCRETE("白色混凝土", 160, 0, "textures/blocks/concrete_white.png"),
    /**
     * 橙色混凝土
     */
    ORANGE_CONCRETE("橙色混凝土", 160, 1, "textures/blocks/concrete_orange.png"),
    /**
     * 品红色混凝土
     */
    MAGENTA_CONCRETE("品红色混凝土", 160, 2, "textures/blocks/concrete_magenta.png"),
    /**
     * 淡蓝色混凝土
     */
    LIGHT_BLUE_CONCRETE("淡蓝色混凝土", 160, 3, "textures/blocks/concrete_light_blue.png"),
    /**
     * 黄色混凝土
     */
    YELLOW_CONCRETE("黄色混凝土", 160, 4, "textures/blocks/concrete_yellow.png"),
    /**
     * 黄绿色混凝土
     */
    LIME_CONCRETE("黄绿色混凝土", 160, 5, "textures/blocks/concrete_lime.png"),
    /**
     * 粉红色混凝土
     */
    PINK_CONCRETE("粉红色混凝土", 160, 6, "textures/blocks/concrete_pink.png"),
    /**
     * 灰色混凝土
     */
    GRAY_CONCRETE("灰色混凝土", 160, 7, "textures/blocks/concrete_gray.png"),
    /**
     * 淡灰色混凝土
     */
    LIGHT_GRAY_CONCRETE("淡灰色混凝土", 160, 8, "textures/blocks/concrete_silver.png"),
    /**
     * 青色混凝土
     */
    CYAN_CONCRETE("青色混凝土", 160, 9, "textures/blocks/concrete_cyan.png"),
    /**
     * 紫色混凝土
     */
    PURPLE_CONCRETE("紫色混凝土", 160, 10, "textures/blocks/concrete_purple.png"),
    /**
     * 蓝色混凝土
     */
    BLUE_CONCRETE("蓝色混凝土", 160, 11, "textures/blocks/concrete_blue.png"),
    /**
     * 棕色混凝土
     */
    BROWN_CONCRETE("棕色混凝土", 160, 12, "textures/blocks/concrete_brown.png"),
    /**
     * 绿色混凝土
     */
    GREEN_CONCRETE("绿色混凝土", 160, 13, "textures/blocks/concrete_green.png"),
    /**
     * 红色混凝土
     */
    RED_CONCRETE("红色混凝土", 160, 14, "textures/blocks/concrete_red.png"),
    /**
     * 黑色混凝土
     */
    BLACK_CONCRETE("黑色混凝土", 160, 15, "textures/blocks/concrete_black.png"),
    /**
     * 铁锹
     */
    IRON_SHOVEL("铁锹", 256, 0, "textures/items/iron_shovel.png"),
    /**
     * 铁镐
     */
    IRON_PICKAXE("铁镐", 257, 0, "textures/items/iron_pickaxe.png"),
    /**
     * 铁斧
     */
    IRON_AXE("铁斧", 258, 0, "textures/items/iron_axe.png"),
    /**
     * 打火石
     */
    FLINT_AND_STEEL("打火石", 259, 0, "textures/items/flint_and_steel.png"),
    /**
     * 苹果
     */
    APPLE("苹果", 260, 0, "textures/items/apple.png"),
    /**
     * 弓
     */
    BOW("弓", 261, 0, "textures/items/bow_standby.png"),
    /**
     * 箭
     */
    ARROW("箭", 262, 0, "textures/items/arrow.png"),
    /**
     * 煤炭
     */
    COAL("煤炭", 263, 0, "textures/items/coal.png"),
    /**
     * 木炭
     */
    CHARCOAL("木炭", 263, 1, "textures/items/charcoal.png"),
    /**
     * 钻石
     */
    DIAMOND("钻石", 264, 0, "textures/items/diamond.png"),
    /**
     * 铁锭
     */
    IRON_INGOT("铁锭", 265, 0, "textures/items/iron_ingot.png"),
    /**
     * 金锭
     */
    GOLD_INGOT("金锭", 266, 0, "textures/items/gold_ingot.png"),
    /**
     * 铁剑
     */
    IRON_SWORD("铁剑", 267, 0, "textures/items/iron_sword.png"),
    /**
     * 木剑
     */
    WOODEN_SWORD("木剑", 268, 0, "textures/items/wood_sword.png"),
    /**
     * 木锹
     */
    WOODEN_SHOVEL("木锹", 269, 0, "textures/items/wood_shovel.png"),
    /**
     * 木镐
     */
    WOODEN_PICKAXE("木镐", 270, 0, "textures/items/wood_pickaxe.png"),
    /**
     * 木斧
     */
    WOODEN_AXE("木斧", 271, 0, "textures/items/wood_axe.png"),
    /**
     * 石剑
     */
    STONE_SWORD("石剑", 272, 0, "textures/items/stone_sword.png"),
    /**
     * 石锹
     */
    STONE_SHOVEL("石锹", 273, 0, "textures/items/stone_shovel.png"),
    /**
     * 石镐
     */
    STONE_PICKAXE("石镐", 274, 0, "textures/items/stone_pickaxe.png"),
    /**
     * 石斧
     */
    STONE_AXE("石斧", 275, 0, "textures/items/stone_axe.png"),
    /**
     * 钻石剑
     */
    DIAMOND_SWORD("钻石剑", 276, 0, "textures/items/diamond_sword.png"),
    /**
     * 钻石锹
     */
    DIAMOND_SHOVEL("钻石锹", 277, 0, "textures/items/diamond_shovel.png"),
    /**
     * 钻石镐
     */
    DIAMOND_PICKAXE("钻石镐", 278, 0, "textures/items/diamond_pickaxe.png"),
    /**
     * 钻石斧
     */
    DIAMOND_AXE("钻石斧", 279, 0, "textures/items/diamond_axe.png"),
    /**
     * 木棍
     */
    STICK("木棍", 280, 0, "textures/items/stick.png"),
    /**
     * 碗
     */
    BOWL("碗", 281, 0, "textures/items/bowl.png"),
    /**
     * 蘑菇煲
     */
    MUSHROOM_STEW("蘑菇煲", 282, 0, "textures/items/mushroom_stew.png"),
    /**
     * 金剑
     */
    GOLDEN_SWORD("金剑", 283, 0, "textures/items/gold_sword.png"),
    /**
     * 金锹
     */
    GOLDEN_SHOVEL("金锹", 284, 0, "textures/items/gold_shovel.png"),
    /**
     * 金镐
     */
    GOLDEN_PICKAXE("金镐", 285, 0, "textures/items/gold_pickaxe.png"),
    /**
     * 金斧
     */
    GOLDEN_AXE("金斧", 286, 0, "textures/items/gold_axe.png"),
    /**
     * 蛛丝
     */
    STRING("蛛丝", 287, 0, "textures/items/string.png"),
    /**
     * 羽毛
     */
    FEATHER("羽毛", 288, 0, "textures/items/feather.png"),
    /**
     * 火药
     */
    GUNPOWDER("火药", 289, 0, "textures/items/gunpowder.png"),
    /**
     * 木锄
     */
    WOODEN_HOE("木锄", 290, 0, "textures/items/wood_hoe.png"),
    /**
     * 石锄
     */
    STONE_HOE("石锄", 291, 0, "textures/items/stone_hoe.png"),
    /**
     * 铁锄
     */
    IRON_HOE("铁锄", 292, 0, "textures/items/iron_hoe.png"),
    /**
     * 钻石锄
     */
    DIAMOND_HOE("钻石锄", 293, 0, "textures/items/diamond_hoe.png"),
    /**
     * 金锄
     */
    GOLDEN_HOE("金锄", 294, 0, "textures/items/gold_hoe.png"),
    /**
     * 种子
     */
    WHEAT_SEEDS("种子", 295, 0, "textures/items/seeds_wheat.png"),
    /**
     * 小麦
     */
    WHEAT("小麦", 296, 0, "textures/items/wheat.png"),
    /**
     * 面包
     */
    BREAD("面包", 297, 0, "textures/items/bread.png"),
    /**
     * 皮革头盔
     */
    LEATHER_HELMET("皮革头盔", 298, 0, "textures/items/leather_helmet.tga"),
    /**
     * 皮革胸甲
     */
    LEATHER_CHESTPLATE("皮革胸甲", 299, 0, "textures/items/leather_chestplate.png"),
    /**
     * 皮革护腿
     */
    LEATHER_LEGGINGS("皮革护腿", 300, 0, "textures/items/leather_leggings.tga"),
    /**
     * 皮革靴子
     */
    LEATHER_BOOTS("皮革靴子", 301, 0, "textures/items/leather_boots.tga"),
    /**
     * 锁链头盔
     */
    CHAINMAIL_HELMET("锁链头盔", 302, 0, "textures/items/chainmail_helmet.png"),
    /**
     * 锁链胸甲
     */
    CHAINMAIL_CHESTPLATE("锁链胸甲", 303, 0, "textures/items/chainmail_chestplate.png"),
    /**
     * 锁链护腿
     */
    CHAINMAIL_LEGGINGS("锁链护腿", 304, 0, "textures/items/chainmail_leggings.png"),
    /**
     * 锁链靴子
     */
    CHAINMAIL_BOOTS("锁链靴子", 305, 0, "textures/items/chainmail_boots.png"),
    /**
     * 铁头盔
     */
    IRON_HELMET("铁头盔", 306, 0, "textures/items/iron_helmet.png"),
    /**
     * 铁胸甲
     */
    IRON_CHESTPLATE("铁胸甲", 307, 0, "textures/items/iron_chestplate.png"),
    /**
     * 铁护腿
     */
    IRON_LEGGINGS("铁护腿", 308, 0, "textures/items/iron_leggings.png"),
    /**
     * 铁靴子
     */
    IRON_BOOTS("铁靴子", 309, 0, "textures/items/iron_boots.png"),
    /**
     * 钻石头盔
     */
    DIAMOND_HELMET("钻石头盔", 310, 0, "textures/items/diamond_helmet.png"),
    /**
     * 钻石胸甲
     */
    DIAMOND_CHESTPLATE("钻石胸甲", 311, 0, "textures/items/diamond_chestplate.png"),
    /**
     * 钻石护腿
     */
    DIAMOND_LEGGINGS("钻石护腿", 312, 0, "textures/items/diamond_leggings.png"),
    /**
     * 钻石靴子
     */
    DIAMOND_BOOTS("钻石靴子", 313, 0, "textures/items/diamond_boots.png"),
    /**
     * 金头盔
     */
    GOLDEN_HELMET("金头盔", 314, 0, "textures/items/gold_helmet.png"),
    /**
     * 金胸甲
     */
    GOLDEN_CHESTPLATE("金胸甲", 315, 0, "textures/items/gold_chestplate.png"),
    /**
     * 金护腿
     */
    GOLDEN_LEGGINGS("金护腿", 316, 0, "textures/items/gold_leggings.png"),
    /**
     * 金靴子
     */
    GOLDEN_BOOTS("金靴子", 317, 0, "textures/items/gold_boots.png"),
    /**
     * 燧石
     */
    FLINT("燧石", 318, 0, "textures/items/flint.png"),
    /**
     * 生猪排
     */
    PORKCHOP("生猪排", 319, 0, "textures/items/porkchop_raw.png"),
    /**
     * 熟猪排
     */
    COOKED_PORKCHOP("熟猪排", 320, 0, "textures/items/porkchop_cooked.png"),
    /**
     * 画
     */
    PAINTING("画", 321, 0, "textures/items/painting.png"),
    /**
     * 金苹果
     */
    GOLDEN_APPLE("金苹果", 322, 0, "textures/items/apple_golden.png"),
    /**
     * 告示牌
     */
    SIGN("告示牌", 323, 0, "textures/items/sign.png"),
    /**
     * 橡木门
     */
    WOODEN_DOOR("橡木门", 324, 0, "textures/items/door_wood.png"),
    /**
     * 桶
     */
    BUCKET("桶", 325, 0, "textures/items/bucket_empty.png"),

    WATER_BUCKET("水桶", 325, 8, "textures/items/bucket_water.png"),
    TROPICAL_FISH_BUCKET("热带鱼桶", 325, 4, "textures/items/bucket_tropical.png"),
    PUFFERFISH_BUCKET("河豚桶", 325, 5, "textures/items/bucket_pufferfish.png"),
    SALMON_BUCKET("鲑鱼桶", 325, 3, "textures/items/bucket_salmon.png"),
    COD_BUCKET("鳕鱼桶", 325, 2, "textures/items/bucket_cod.png"),
    AXOLOTL_BUCKET("美西螈桶", 325, 12, "textures/items/bucket_axolotl.png"),

    /**
     * 矿车
     */
    MINECART("矿车", 328, 0, "textures/items/minecart_normal.png"),
    /**
     * 鞍
     */
    SADDLE("鞍", 329, 0, "textures/items/saddle.png"),
    /**
     * 红石
     */
    REDSTONE("红石", 331, 0, "textures/items/redstone_dust.png"),
    /**
     * 雪球
     */
    SNOWBALL("雪球", 332, 0, "textures/items/snowball.png"),
    /**
     * 橡木船
     */
    BOAT("橡木船", 333, 0, "textures/items/boat.png"),
    /**
     * 皮革
     */
    LEATHER("皮革", 334, 0, "textures/items/leather.png"),
    /**
     * 粘土球
     */
    CLAY_BALL("粘土球", 337, 0, "textures/items/clay_ball.png"),
    /**
     * 甘蔗
     */
    REEDS("甘蔗", 338, 0, "textures/items/reeds.png"),
    /**
     * 纸
     */
    PAPER("纸", 339, 0, "textures/items/paper.png"),
    /**
     * 书
     */
    BOOK("书", 340, 0, "textures/items/book_normal.png"),
    /**
     * 粘液球
     */
    SLIME_BALL("粘液球", 341, 0, "textures/items/slimeball.png"),
    /**
     * 箱子矿车
     */
    CHEST_MINECART("箱子矿车", 342, 0, "textures/items/minecart_chest.png"),
    /**
     * 鸡蛋
     */
    EGG("鸡蛋", 344, 0, "textures/items/egg.png"),
    /**
     * 指南针
     */
    COMPASS("指南针", 345, 0, "textures/items/compass_item.png"),
    /**
     * 鱼竿
     */
    FISHING_ROD("鱼竿", 346, 0, "textures/items/fishing_rod_cast.png"),
    /**
     * 时钟
     */
    CLOCK("时钟", 347, 0, "textures/items/clock_item.png"),
    /**
     * 荧石粉
     */
    GLOWSTONE_DUST("荧石粉", 348, 0, "textures/items/glowstone_dust.png"),
    /**
     * 鱼
     */
    FISH("鱼", 349, 0, "textures/items/fish_raw.png"),
    /**
     * 熟鱼
     */
    COOKED_FISH("熟鱼", 350, 0, "textures/items/fish_cooked.png"),
    /**
     * 墨囊
     */
    DYE("墨囊", 351, 0, "textures/items/dye_powder_black.png"),
    /**
     * 品红色染料
     */
    SOLFERINO_DYE("品红色染料", 351, 1, "textures/items/dye_powder_purple.png"),
    /**
     * 绿色染料
     */
    GREEN_DYE("绿色染料", 351, 2, "textures/items/dye_powder_green.png"),
    /**
     * 可可豆
     */
    COCOA("可可豆", 351, 3, "textures/items/dye_powder_brown.png"),
    /**
     * 蓝色染料
     */
    BLUE_DYE("蓝色染料", 351, 4, "textures/items/dye_powder_blue.png"),
    /**
     * 紫色染料
     */
    VIOLET_DYE("紫色染料", 351, 5, "textures/items/dye_powder_purple.png"),
    /**
     * 青色染料
     */
    CYAN_DYE("青色染料", 351, 6, "textures/items/dye_powder_cyan.png"),
    /**
     * 淡灰色染料
     */
    LIGHT_GRAY_DYE("淡灰色染料", 351, 7, "textures/items/dye_powder_silver.png"),
    /**
     * 灰色染料
     */
    GRAY_DYE("灰色染料", 351, 8, "textures/items/dye_powder_pink.png"),
    /**
     * 粉红色染料
     */
    PINK_DYE("粉红色染料", 351, 9, "textures/items/dye_powder_pink.png"),
    /**
     * 黄绿色染料
     */
    OLIVINE_DYE("黄绿色染料", 351, 10, "textures/items/dye_powder_lime.png"),
    /**
     * 黄色染料
     */
    YELLOW_DYE("黄色染料", 351, 11, "textures/items/dye_powder_yellow.png"),
    /**
     * 淡蓝色染料
     */
    LIGHT_BLUE_DYE("淡蓝色染料", 351, 12, "textures/items/dye_powder_light_blue.png"),
    /**
     * 红色染料
     */
    RED_DYE("红色染料", 351, 13, "textures/items/dye_powder_red.png"),
    /**
     * 橙色染料
     */
    ORANGE_DYE("橙色染料", 351, 14, "textures/items/dye_powder_orange.png"),
    /**
     * 骨粉
     */
    WHITE_DYE("骨粉", 351, 15, "textures/items/dye_powder_white.png"),
    /**
     * 骨头
     */
    BONE("骨头", 352, 0, "textures/items/bone.png"),
    /**
     * 糖
     */
    SUGAR("糖", 353, 0, "textures/items/sugar.png"),
    /**
     * 蛋糕
     */
    CAKE("蛋糕", 354, 0, "textures/items/cake.png"),
    /**
     * 床
     */
    BED("床", 355, 0, "textures/items/bed_red.png"),
    /**
     * 中继器
     */
    REPEATER("中继器", 356, 0, "textures/items/repeater.png"),
    /**
     * 曲奇
     */
    COOKIE("曲奇", 357, 0, "textures/items/cookie.png"),
    /**
     * 地图(满)
     */
    FILLED_MAP("地图(满)", 358, 0, "textures/items/map_nautilus.png"),
    /**
     * 剪刀
     */
    SHEARS("剪刀", 359, 0, "textures/items/shears.png"),
    /**
     * 西瓜
     */
    MELON("西瓜", 360, 0, "textures/items/melon.png"),
    PUMPKIN_SEEDS("南瓜种子", 360, 0, "textures/items/seeds_pumpkin.png"),
    /**
     * 南瓜种子
     */
    MELON_SEEDS("南瓜种子", 362, 0, "textures/items/seeds_melon.png"),
    /**
     * 生牛肉
     */
    BEEF("生牛肉", 363, 0, "textures/items/beef_raw.png"),
    /**
     * 熟牛肉
     */
    COOKED_BEEF("熟牛肉", 364, 0, "textures/items/beef_cooked.png"),
    /**
     * 生鸡肉
     */
    CHICKEN("生鸡肉", 365, 0, "textures/items/chicken_raw.png"),
    /**
     * 熟鸡肉
     */
    COOKED_CHICKEN("熟鸡肉", 366, 0, "textures/items/chicken_cooked.png"),
    /**
     * 腐肉
     */
    ROTTEN_FLESH("腐肉", 367, 0, "textures/items/rotten_flesh.png"),
    /**
     * 末影珍珠
     */
    ENDER_PEARL("末影珍珠", 368, 0, "textures/items/ender_pearl.png"),
    /**
     * 烈焰棒
     */
    BLAZE_ROD("烈焰棒", 369, 0, "textures/items/blaze_rod.png"),
    /**
     * 恶魂泪
     */
    GHAST_TEAR("恶魂泪", 370, 0, "textures/items/ghast_tear.png"),
    /**
     * 金粒
     */
    GOLD_NUGGET("金粒", 371, 0, "textures/items/gold_nugget.png"),
    /**
     * 地狱疣
     */
    NETHER_WART("地狱疣", 372, 0, "textures/items/nether_wart.png"),
    /**
     * 水瓶
     */
    POTION("水瓶", 373, 0, "textures/items/potion_bottle_drinkable.png"),
    /**
     * 玻璃瓶
     */
    GLASS_BOTTLE("玻璃瓶", 374, 0, "textures/items/potion_bottle_empty.png"),
    /**
     * 蜘蛛眼
     */
    SPIDER_EYE("蜘蛛眼", 375, 0, "textures/items/spider_eye.png"),
    /**
     * 发酵蜘蛛眼
     */
    SPIDER_EYE_FERMENTED("发酵蜘蛛眼", 376, 0, "textures/items/spider_eye_fermented.png"),
    /**
     * 烈焰粉
     */
    BLAZE_POWDER("烈焰粉", 377, 0, "textures/items/blaze_powder.png"),
    /**
     * 岩浆膏
     */
    MAGMA_CREAM("岩浆膏", 378, 0, "textures/items/magma_cream.png"),
    /**
     * 末影之眼
     */
    ENDER_EYE("末影之眼", 381, 0, "textures/items/ender_eye.png"),
    /**
     * 金西瓜
     */
    SPECKLED_MELON("金西瓜", 382, 0, "textures/items/melon_speckled.png"),
    /**
     * 经验瓶
     */
    EXPERIENCE_BOTTLE("经验瓶", 384, 0, "textures/items/experience_bottle.png"),
    /**
     * 火球
     */
    FIRE_CHARGE("火球", 385, 0, "textures/items/fireball.png"),
    /**
     * 鸡刷怪蛋
     */
    SPAWN_MOB("鸡刷怪蛋", 383, 10, "textures/items/egg_chicken.png"),
    /**
     * 绿宝石
     */
    EMERALD("绿宝石", 388, 0, "textures/items/emerald.png"),
    /**
     * 展示框
     */
    ITEM_FRAME("展示框", 389, 0, "textures/items/item_frame.png"),
    /**
     * 花盆
     */
    FLOWER_POT("花盆", 390, 0, "textures/items/flower_pot.png"),
    /**
     * 胡萝卜
     */
    CARROT("胡萝卜", 391, 0, "textures/items/carrot.png"),
    /**
     * 马铃薯
     */
    POTATO("马铃薯", 392, 0, "textures/items/potato.png"),
    /**
     * 烤马铃薯
     */
    BAKED_POTATO("烤马铃薯", 393, 0, "textures/items/potato_baked.png"),
    /**
     * 毒马铃薯
     */
    POISONOUS_POTATO("毒马铃薯", 394, 0, "textures/items/potato_poisonous.png"),
    /**
     * 空地图
     */
    MAP("空地图", 395, 0, "textures/items/map_empty.png"),
    /**
     * 金胡萝卜
     */
    GOLDEN_CARROT("金胡萝卜", 396, 0, "textures/items/carrot_golden.png"),
    /**
     * 骷髅头
     */
    SKELETON_SKULL("骷髅头", 397, 0, "textures/items/bone.png"),
    /**
     * 凋零骷髅头
     */
    LEIERDA_SKULL("凋零骷髅头", 397, 1, "textures/blocks/observer_front.png"),
    /**
     * 僵尸头
     */
    ZOMBIE_SKULL("僵尸头", 397, 2, "textures/blocks/observer_front.png"),
    /**
     * 史蒂夫头
     */
    STEVE_SKULL("史蒂夫头", 397, 3, "textures/blocks/observer_front.png"),
    /**
     * 苦力怕头
     */
    CREEPER_SKULL("苦力怕头", 397, 4, "textures/blocks/observer_front.png"),
    /**
     * 龙头
     */
    DRAGON_SKULL("龙头", 397, 5, "textures/blocks/observer_front.png"),
    /**
     * 胡萝卜杆
     */
    CARROT_ON_A_STICK("胡萝卜杆", 398, 0, "textures/items/carrot_on_a_stick.png"),
    /**
     * 下届之星
     */
    NETHER_STAR("下届之星", 399, 0, "textures/items/nether_star.png"),
    /**
     * 南瓜派
     */
    PUMPKIN_PIE("南瓜派", 400, 0, "textures/items/pumpkin_pie.png"),
    /**
     * 附魔书
     */
    ENCHANTED_BOOK("附魔书", 403, 0, "textures/items/book_writable.png"),
    /**
     * 比较器
     */
    COMPARATOR("比较器", 404, 0, "textures/items/comparator.png"),
    /**
     * 地狱石英
     */
    QUARTZ("地狱石英", 406, 0, "textures/items/quartz.png"),
    /**
     * tnt矿车
     */
    TNT_MINECART("tnt矿车", 407, 0, "textures/items/minecart_tnt.png"),
    /**
     * 漏斗矿车
     */
    HOPPER_MINECART("漏斗矿车", 408, 0, "textures/items/minecart_hopper.png"),
    /**
     * 海晶碎片
     */
    PRISMARINE_SHARD("海晶碎片", 409, 0, "textures/items/prismarine_shard.png"),
    /**
     * 海晶灯粉
     */
    PRISMARINE_CRYSTALS("海晶灯粉", 410, 0, "textures/items/prismarine_crystals.png"),
    /**
     * 生兔子肉
     */
    RABBIT("生兔子肉", 411, 0, "textures/items/rabbit_raw.png"),
    /**
     * 熟兔子肉
     */
    COOKED_RABBIT("熟兔子肉", 412, 0, "textures/items/rabbit_cooked.png"),
    /**
     * 兔子煲
     */
    RABBIT_STEW("兔子煲", 413, 0, "textures/items/rabbit_stew.png"),
    /**
     * 兔子脚
     */
    RABBIT_FOOT("兔子脚", 414, 0, "textures/items/rabbit_foot.png"),
    /**
     * 兔子皮
     */
    RABBIT_HIDE("兔子皮", 415, 0, "textures/items/rabbit_hide.png"),
    /**
     * 皮革马鞍
     */
    ARMOR_STAND("皮革马鞍", 416, 0, "textures/items/saddle.png"),
    /**
     * 铁马鞍
     */
    IRON_HORSE_ARMOR("铁马鞍", 417, 0, "textures/items/iron_horse_armor.png"),
    /**
     * 金马鞍
     */
    GOLD_HORSE_ARMOR("金马鞍", 418, 0, "textures/items/gold_horse_armor.png"),
    /**
     * 钻石马鞍
     */
    DIAMOND_HORSE_ARMOR("钻石马鞍", 419, 0, "textures/items/diamond_horse_armor.png"),
    /**
     * 栓绳
     */
    LEAD("栓绳", 420, 0, "textures/items/lead.png"),
    /**
     * 命名牌
     */
    NAME_TAG("命名牌", 421, 0, "textures/items/name_tag.png"),
    /**
     * 命令方块矿车
     */
    COMMAND_BLOCK_MINECART("命令方块矿车", 422, 0, "textures/items/minecart_command_block.png"),
    /**
     * 生羊肉
     */
    MUTTON("生羊肉", 423, 0, "textures/items/mutton_raw.png"),
    /**
     * 熟羊肉
     */
    COOKED_MUTTON("熟羊肉", 424, 0, "textures/items/mutton_cooked.png"),
    /**
     * 末影水晶
     */
    END_CRYSTAL("末影水晶", 426, 0, "textures/items/end_crystal.png"),
    /**
     * 云杉木门
     */
    SPRUCE_DOOR("云杉木门", 427, 0, "textures/items/door_jungle.png"),
    /**
     * 桦树木门
     */
    BIRCH_WOOD_DOOR("桦树木门", 428, 0, "textures/items/door_birch.png"),
    /**
     * 丛林木门
     */
    JUNGLE_DOOR("丛林木门", 429, 0, "textures/items/door_spruce.png"),
    /**
     * 金合欢木门
     */
    ACACIA_DOOR("金合欢木门", 430, 0, "textures/items/door_acacia.png"),
    /**
     * 深色橡木门
     */
    DARK_OAK_DOOR("深色橡木门", 431, 0, "textures/items/door_dark_oak.png"),
    /**
     * 共鸣果
     */
    CHORUS_FRUIT("共鸣果", 432, 0, "textures/items/chorus_fruit.png"),
    /**
     * 爆裂共鸣果
     */
    POPPED_CHORUS_FRUIT("爆裂共鸣果", 433, 0, "textures/items/chorus_fruit_popped.png"),
    /**
     * 龙息
     */
    DRAGON_BREATH("龙息", 437, 0, "textures/items/dragons_breath.png"),
    /**
     * 喷溅的水瓶
     */
    SPLASH_POTION("喷溅的水瓶", 438, 0, "textures/items/potion_bottle_splash.png"),
    /**
     * 遗留的水瓶
     */
    LINGERING_POTION("遗留的水瓶", 441, 0, "textures/items/potion_bottle_lingering_waterBreathing.png"),
    /**
     * 翅鞘
     */
    ELYTRA("翅鞘", 444, 0, "textures/items/elytra.png"),
    /**
     * 潜匿之壳
     */
    SHULKER_SHELL("潜匿之壳", 445, 0, "textures/items/shulker_shell.png"),

    // new_items
    // no icon
    BANNER("旗帜", 446, 0, "textures/items/banner_pattern.png"),
    TOTEM("不死图腾", 450, 0, "textures/items/totem.png"),
    NUGGET_IRON("铁粒", 452, 0, "textures/items/iron_nugget.png"),
    TRIDENT("三叉戟", 455, 0, "textures/items/trident.png"),
    BEETROOT("甜菜根", 457, 0, "textures/items/beetroot.png"),
    SEEDS_BEETROOT("甜菜种子", 458, 0, "textures/items/seeds_beetroot.png"),
    BEETROOT_SOUP("甜菜汤", 459, 0, "textures/items/beetroot_soup.png"),
    SALMON("三文鱼", 460, 0, "textures/items/fish_salmon_raw.png"),
    CLOWNFISH("小丑鱼", 461, 0, "textures/items/fish_clownfish_raw.png"),
    PUFFERFISH("河豚", 462, 0, "textures/items/fish_pufferfish_raw.png"),
    SALMON_COOKED("烤三文鱼", 463, 0, "textures/items/fish_salmon_cooked.png"),
    DRIED_KELP("干海藻", 464, 0, "textures/items/dried_kelp.png"),
    NAUTILUS_SHELL("鹦鹉螺壳", 465, 0, "textures/items/nautilus.png"),
    APPLE_GOLD_ENCHANTED("附魔金苹果", 466, 0, "textures/items/apple_golden.png"),
    HEART_OF_THE_SEA("海洋之心", 467, 0, "textures/items/heartofthesea_closed.png"),
    SCUTE("鳞甲", 468, 0, "textures/items/turtle_shell_piece.png"),
    TURTLE_SHELL("海龟鳞甲", 469, 0, "textures/items/turtle_shell_piece.png"),
    PHANTOM_MEMBRANE("幻翼膜", 470, 0, "textures/items/phantom_membrane.png"),
    CROSSBOW("弩", 471, 0, "textures/items/crossbow_standby.png"),
    SIGN_SPRUCE("云杉木牌", 472, 0, "textures/items/sign_spruce.png"),
    SIGN_BIRCH("白桦木牌", 473, 0, "textures/items/sign_birch.png"),
    SIGN_JUNGLE("丛林木牌", 474, 0, "textures/items/sign_jungle.png"),
    SIGN_ACACIA("金合欢木牌", 475, 0, "textures/items/sign_acacia.png"),
    SIGN_DARK_OAK("深橡木牌", 476, 0, "textures/items/sign_darkoak.png"),

    RECORD_CAT("音乐唱片 - Cat", 501, 0, "textures/items/record_cat.png"),
    RECORD_13("音乐唱片 - 13", 500, 0, "textures/items/record_13.png"),
    RECORD_BLOCKS("音乐唱片 - Blocks", 502, 0, "textures/items/record_blocks.png"),
    RECORD_CHIRP("音乐唱片 - Chirp", 503, 0, "textures/items/record_chirp.png"),
    RECORD_FAR("音乐唱片 - Far", 504, 0, "textures/items/record_far.png"),
    RECORD_MALL("音乐唱片 - Mall", 505, 0, "textures/items/record_mall.png"),
    RECORD_MELLOHI("音乐唱片 - Mellohi", 506, 0, "textures/items/record_mellohi.png"),
    RECORD_STAL("音乐唱片 - Stal", 507, 0, "textures/items/record_stal.png"),
    RECORD_STRAD("音乐唱片 - Strad", 508, 0, "textures/items/record_strad.png"),
    RECORD_WARD("音乐唱片 - Ward", 509, 0, "textures/items/record_ward.png"),
    RECORD_11("音乐唱片 - 11", 510, 0, "textures/items/record_11.png"),
    RECORD_WAIT("音乐唱片 - Wait", 511, 0, "textures/items/record_wait.png"),

    SHIELD("盾牌", 513, 0, "textures/items/empty_armor_slot_shield.png"),

    RECORD_5("音乐唱片 5", 636, 0, "textures/items/record_5.png"),
    DISC_FRAGMENT_5("唱片残片 5", 637, 0, "textures/items/disc_fragment_5.png"),
    CHEST_BOAT_OAK("橡木运输船", 638, 0, "textures/items/oak_chest_boat.png"),
    CHEST_BOAT_BIRCH("白桦木运输船", 639, 0, "textures/items/birch_chest_boat.png"),
    CHEST_BOAT_JUNGLE("丛林木运输船", 640, 0, "textures/items/jungle_chest_boat.png"),
    CHEST_BOAT_SPRUCE("云杉木运输船", 641, 0, "textures/items/spruce_chest_boat.png"),
    CHEST_BOAT_ACACIA("金合欢木运输船", 642, 0, "textures/items/acacia_chest_boat.png"),
    CHEST_BOAT_DARK_OAK("深色橡木运输船", 643, 0, "textures/items/dark_oak_chest_boat.png"),
    CHEST_BOAT_MANGROVE("红树木运输船", 644, 0, "textures/items/mangrove_chest_boat.png"),
    CHEST_RAFT_BAMBOO("竹筏运输船", 648, 0, "textures/items/bamboo_chest_raft.png"),
    CHEST_BOAT_CHERRY("樱花木运输船", 649, 0, "textures/items/cherry_chest_boat.png"),

    GLOW_BERRIES("发光浆果", 654, 0, "textures/items/glow_berries.png"),

    RECORD_RELIC("音乐唱片 Relic", 701, 0, "textures/items/music_disc_relic.png"),

    CAMPFIRE("营火", 720, 0, "textures/items/campfire.png"),

    SUSPICIOUS_STEW("可疑的炖汤", 734, 0, "textures/items/suspicious_stew.png"),

    HONEYCOMB("蜜脾", 736, 0, "textures/items/honeycomb.png"),
    HONEY_BOTTLE("蜂蜜瓶", 737, 0, "textures/items/honey_bottle.png"),
    LODESTONE_COMPASS("磁石指针", 741, 0, "textures/items/lodestonecompass_item.png"),
    NETHERITE_INGOT("下界合金锭", 742, 0, "textures/items/netherite_ingot.png"),
    NETHERITE_SWORD("下届合金剑", 743, 0, "textures/items/netherite_sword.png"),
    NETHERITE_SHOVEL("下届合金铲", 744, 0, "textures/items/netherite_shovel.png"),
    NETHERITE_PICKAXE("下届合金镐", 745, 0, "textures/items/netherite_pickaxe.png"),
    NETHERITE_AXE("下届合金斧", 746, 0, "textures/items/netherite_axe.png"),
    NETHERITE_HOE("下届合金锄", 747, 0, "textures/items/netherite_hoe.png"),
    NETHERITE_HELMET("下届合金头盔", 748, 0, "textures/items/netherite_helmet.png"),
    NETHERITE_CHESTPLATE("下届合金盔甲", 749, 0, "textures/items/netherite_chestplate.png"),
    NETHERITE_LEGGINGS("下届合金裤腿", 750, 0, "textures/items/netherite_leggings.png"),
    NETHERITE_BOOTS("下届合金靴子", 751, 0, "textures/items/netherite_boots.png"),
    NETHERITE_SCRAP("下届合金碎片", 752, 0, "textures/items/netherite_scrap.png"),
    CRIMSON_SIGN("绯红木告示牌", 753, 0, "textures/items/sign_crimson.png"),
    WARPED_SIGN("诡异木告示牌", 754, 0, "textures/items/sign_warped.png"),
    CRIMSON_DOOR("绯红木门", 755, 0, "textures/items/crimson_door.png"),
    WARPED_DOOR("诡异木门", 756, 0, "textures/items/warped_door.png"),
    WARPED_FUNGUS_ON_A_STICK("诡异菌钓竿", 757, 0, "textures/items/warped_fungus_on_a_stick.png"),
    CHAIN("锁链", 758, 0, "textures/items/chain.png"),
    RECORD_PIGSTEP("音乐唱片 Pigstep", 759, 0, "textures/items/record_pigstep.png"),
    NETHER_SPROUTS("下界苗", 760, 0, "textures/items/nether_sprouts.png"),

    AMETHYST_SHARD("紫水晶碎片", 771, 0, "textures/items/amethyst_shard.png"),
    SPYGLASS("望远镜", 772, 0, "textures/items/spyglass.png"),
    RECORD_OTHERSIDE("音乐唱片 otherside", 773, 0, "textures/items/record_otherside.png"),

    SOUL_CAMPFIRE("灵魂营火", 801, 0, "textures/items/soul_campfire.png"),
    GLOW_ITEM_FRAME("发光物品展示框", 850, 0, "textures/items/glow_item_frame.png"),

    MANGROVE_SIGN("红树木告示牌", 1005, 0, "textures/items/mangrove_sign.png"),
    BAMBOO_SIGN("竹子告示牌", 1006, 0, "textures/items/bamboo_sign.png"),

    // 照明
    SHROOMLIGHT("菌光体", "minecraft:shroomlight", "textures/blocks/shroomlight.png"),
    LANTERN("灯", "minecraft:lantern", "textures/items/lantern.png"),
    SOUL_LANTERN("灵魂灯", "minecraft:soul_lantern", "textures/items/soul_lantern.png"),
    SOUL_TORCH("灵魂火把", "minecraft:soul_torch", "textures/blocks/soul_torch.png"),

    // 水景
    KELP("海带", "minecraft:kelp", "textures/items/kelp.png"),

    // 材料类
    INGOT_COPPER("铜锭", "minecraft:copper_ingot", "textures/items/copper_ingot.png"),
    RAW_IRON("粗铁", "minecraft:raw_iron", "textures/items/raw_iron.png"),
    RAW_GOLD("粗金", "minecraft:raw_gold", "textures/items/raw_gold.png"),
    RAW_COPPER("粗铜", "minecraft:raw_copper", "textures/items/raw_copper.png"),
    ECHO_SHARD("回响碎片", "minecraft:echo_shard", "textures/items/echo_shard.png"),
    BREEZE_ROD("微风之杖", "minecraft:breeze_rod", "textures/items/breeze_rod.png"),
    WIND_CHARGE("风弹", "minecraft:wind_charge", "textures/items/wind_charge.png"),

    // 工具与装备
    RECOVERY_COMPASS("追溯指针", "minecraft:recovery_compass", "textures/items/recovery_compass_item.png"),
    BRUSH("刷子", "minecraft:brush", "textures/items/brush.png"),
    MACE("钉锤", "minecraft:mace", "textures/items/mace.png"),
    GOAT_HORN("山羊角", "minecraft:goat_horn", "textures/items/goat_horn.png"),

    // 锻造模板
    NETHERITE_UPGRADE_SMITHING_TEMPLATE("下界合金升级锻造模板", "minecraft:netherite_upgrade_smithing_template", "textures/items/netherite_upgrade_smithing_template.png"),
    SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE("哨兵盔甲纹饰锻造模板", "minecraft:sentry_armor_trim_smithing_template", "textures/items/sentry_armor_trim_smithing_template.png"),
    DUNE_ARMOR_TRIM_SMITHING_TEMPLATE("沙丘盔甲纹饰锻造模板", "minecraft:dune_armor_trim_smithing_template", "textures/items/dune_armor_trim_smithing_template.png"),
    COAST_ARMOR_TRIM_SMITHING_TEMPLATE("海岸盔甲纹饰锻造模板", "minecraft:coast_armor_trim_smithing_template", "textures/items/coast_armor_trim_smithing_template.png"),
    WILD_ARMOR_TRIM_SMITHING_TEMPLATE("荒野盔甲纹饰锻造模板", "minecraft:wild_armor_trim_smithing_template", "textures/items/wild_armor_trim_smithing_template.png"),
    WARD_ARMOR_TRIM_SMITHING_TEMPLATE("守卫盔甲纹饰锻造模板", "minecraft:ward_armor_trim_smithing_template", "textures/items/ward_armor_trim_smithing_template.png"),
    EYE_ARMOR_TRIM_SMITHING_TEMPLATE("眼眸盔甲纹饰锻造模板", "minecraft:eye_armor_trim_smithing_template", "textures/items/eye_armor_trim_smithing_template.png"),
    VEX_ARMOR_TRIM_SMITHING_TEMPLATE("恼鬼盔甲纹饰锻造模板", "minecraft:vex_armor_trim_smithing_template", "textures/items/vex_armor_trim_smithing_template.png"),
    TIDE_ARMOR_TRIM_SMITHING_TEMPLATE("潮汐盔甲纹饰锻造模板", "minecraft:tide_armor_trim_smithing_template", "textures/items/tide_armor_trim_smithing_template.png"),
    SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE("猪鼻盔甲纹饰锻造模板", "minecraft:snout_armor_trim_smithing_template", "textures/items/snout_armor_trim_smithing_template.png"),
    RIB_ARMOR_TRIM_SMITHING_TEMPLATE("肋骨盔甲纹饰锻造模板", "minecraft:rib_armor_trim_smithing_template", "textures/items/rib_armor_trim_smithing_template.png"),
    SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE("尖塔盔甲纹饰锻造模板", "minecraft:spire_armor_trim_smithing_template", "textures/items/spire_armor_trim_smithing_template.png"),
    SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE("寂静盔甲纹饰锻造模板", "minecraft:silence_armor_trim_smithing_template", "textures/items/silence_armor_trim_smithing_template.png"),
    WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE("向导盔甲纹饰锻造模板", "minecraft:wayfinder_armor_trim_smithing_template", "textures/items/wayfinder_armor_trim_smithing_template.png"),
    RAISER_ARMOR_TRIM_SMITHING_TEMPLATE("抬升盔甲纹饰锻造模板", "minecraft:raiser_armor_trim_smithing_template", "textures/items/raiser_armor_trim_smithing_template.png"),
    SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE("塑形盔甲纹饰锻造模板", "minecraft:shaper_armor_trim_smithing_template", "textures/items/shaper_armor_trim_smithing_template.png"),
    HOST_ARMOR_TRIM_SMITHING_TEMPLATE("宿主盔甲纹饰锻造模板", "minecraft:host_armor_trim_smithing_template", "textures/items/host_armor_trim_smithing_template.png"),
    SMITHING_TEMPLATE_ARMOR_TRIM_FLOW("流动盔甲纹饰锻造模板", "minecraft:flow_armor_trim_smithing_template", "textures/items/flow_armor_trim_smithing_template.png"),
    SMITHING_TEMPLATE_ARMOR_TRIM_BOLT("闪电盔甲纹饰锻造模板", "minecraft:bolt_armor_trim_smithing_template", "textures/items/bolt_armor_trim_smithing_template.png"),

    // 考古陶片
    ANGLER_POTTERY_SHERD("渔夫陶片", "minecraft:angler_pottery_sherd", "textures/items/angler_pottery_sherd.png"),
    ARCHER_POTTERY_SHERD("弓箭手陶片", "minecraft:archer_pottery_sherd", "textures/items/archer_pottery_sherd.png"),
    ARMS_UP_POTTERY_SHERD("举手陶片", "minecraft:arms_up_pottery_sherd", "textures/items/arms_up_pottery_sherd.png"),
    BLADE_POTTERY_SHERD("利刃陶片", "minecraft:blade_pottery_sherd", "textures/items/blade_pottery_sherd.png"),
    BREWER_POTTERY_SHERD("酿药师陶片", "minecraft:brewer_pottery_sherd", "textures/items/brewer_pottery_sherd.png"),
    BURN_POTTERY_SHERD("燃烧陶片", "minecraft:burn_pottery_sherd", "textures/items/burn_pottery_sherd.png"),
    DANGER_POTTERY_SHERD("危险陶片", "minecraft:danger_pottery_sherd", "textures/items/danger_pottery_sherd.png"),
    EXPLORER_POTTERY_SHERD("探险家陶片", "minecraft:explorer_pottery_sherd", "textures/items/explorer_pottery_sherd.png"),
    FRIEND_POTTERY_SHERD("好友陶片", "minecraft:friend_pottery_sherd", "textures/items/friend_pottery_sherd.png"),
    HEART_POTTERY_SHERD("心陶片", "minecraft:heart_pottery_sherd", "textures/items/heart_pottery_sherd.png"),
    HEARTBREAK_POTTERY_SHERD("心碎陶片", "minecraft:heartbreak_pottery_sherd", "textures/items/heartbreak_pottery_sherd.png"),
    HOWL_POTTERY_SHERD("嚎叫陶片", "minecraft:howl_pottery_sherd", "textures/items/howl_pottery_sherd.png"),
    MINER_POTTERY_SHERD("矿工陶片", "minecraft:miner_pottery_sherd", "textures/items/miner_pottery_sherd.png"),
    MOURNER_POTTERY_SHERD("哀悼者陶片", "minecraft:mourner_pottery_sherd", "textures/items/mourner_pottery_sherd.png"),
    PLENTY_POTTERY_SHERD("丰饶陶片", "minecraft:plenty_pottery_sherd", "textures/items/plenty_pottery_sherd.png"),
    PRIZE_POTTERY_SHERD("奖品陶片", "minecraft:prize_pottery_sherd", "textures/items/prize_pottery_sherd.png"),
    SHEAF_POTTERY_SHERD("束捆陶片", "minecraft:sheaf_pottery_sherd", "textures/items/sheaf_pottery_sherd.png"),
    SHELTER_POTTERY_SHERD("庇护所陶片", "minecraft:shelter_pottery_sherd", "textures/items/shelter_pottery_sherd.png"),
    SKULL_POTTERY_SHERD("头颅陶片", "minecraft:skull_pottery_sherd", "textures/items/skull_pottery_sherd.png"),
    SNORT_POTTERY_SHERD("哼唱陶片", "minecraft:snort_pottery_sherd", "textures/items/snort_pottery_sherd.png"),
    FLOW_POTTERY_SHERD("流动陶片", "minecraft:flow_pottery_sherd", "textures/items/flow_pottery_sherd.png"),
    GUSTER_POTTERY_SHERD("风袭陶片", "minecraft:guster_pottery_sherd", "textures/items/guster_pottery_sherd.png"),
    SCRAPE_POTTERY_SHERD("刮擦陶片", "minecraft:scrape_pottery_sherd", "textures/items/scrape_pottery_sherd.png"),

    // 唱片
    RECORD_CREATOR("音乐唱片 - Creator", "minecraft:music_disc_creator", "textures/items/music_disc_creator.png"),
    RECORD_CREATOR_MUSIC_BOX("唱片机", "minecraft:music_disc_creator_music_box", "textures/items/music_disc_creator_music_box.png"),
    RECORD_PRECIPICE("音乐唱片 - Precipice", "minecraft:music_disc_precipice", "textures/items/music_disc_precipice.png"),

    // 其他
    DOOR_MANGROVE("红树木门", "minecraft:mangrove_door", "textures/items/mangrove_door.png"),
    TRIAL_KEY("试炼钥匙", "minecraft:trial_key", "textures/items/trial_key.png"),
    TRIAL_KEY_OMINOUS("不祥试炼钥匙", "minecraft:ominous_trial_key", "textures/items/ominous_trial_key.png"),
    OMINOUS_BOTTLE("不祥之瓶", "minecraft:ominous_bottle", "textures/items/ominous_bottle.png"),
    BANNER_PATTERN_FLOW("旗帜图案：流动", "minecraft:flow_banner_pattern", "textures/items/flow_banner_pattern.png"),
    BANNER_PATTERN_GUSTER("旗帜图案：风袭", "minecraft:guster_banner_pattern", "textures/items/guster_banner_pattern.png"),
    BLUE_EGG("蓝色蛋", "minecraft:blue_egg", "textures/items/blue_egg.png"),
    BROWN_EGG("棕色蛋", "minecraft:brown_egg", "textures/items/brown_egg.png"),

    STRIPPED_SPRUCE_LOG("去皮云杉原木", "minecraft:stripped_spruce_log", "textures/blocks/stripped_spruce_log.png"),
    STRIPPED_BIRCH_LOG("去皮桦木原木", "minecraft:stripped_birch_log", "textures/blocks/stripped_birch_log.png"),
    STRIPPED_JUNGLE_LOG("去皮丛林木原木", "minecraft:stripped_jungle_log", "textures/blocks/stripped_jungle_log.png"),
    STRIPPED_ACACIA_LOG("去皮金合欢原木", "minecraft:stripped_acacia_log", "textures/blocks/stripped_acacia_log.png"),
    STRIPPED_DARK_OAK_LOG("去皮深色橡木原木", "minecraft:stripped_dark_oak_log", -9, "textures/blocks/stripped_dark_oak_log.png"),
    STRIPPED_OAK_LOG("去皮橡木原木", "minecraft:stripped_oak_log", "textures/blocks/stripped_oak_log.png"),
    BLUE_ICE("蓝冰", "minecraft:blue_ice", "textures/blocks/blue_ice.png"),

    SEAGRASS("海草", "minecraft:seagrass", "textures/blocks/seagrass_carried.png"),
    TUBE_CORAL_BLOCK("管珊瑚", "minecraft:tube_coral", 0, "textures/blocks/coral_plant_blue.png"),
    BRAIN_CORAL_BLOCK("脑纹珊瑚", "minecraft:brain_coral", 1, "textures/blocks/coral_plant_pink.png"),
    BUBBLE_CORAL_BLOCK("气泡珊瑚", "minecraft:bubble_coral", 2, "textures/blocks/coral_plant_purple.png"),
    FIRE_CORAL_BLOCK("火珊瑚", "minecraft:fire_coral", 3, "textures/blocks/coral_plant_red.png"),
    HORN_CORAL_BLOCK("鹿角珊瑚", "minecraft:horn_coral", 4, "textures/blocks/coral_plant_yellow.png"),
    TUBE_CORAL_FAN("管珊瑚扇", "minecraft:coral_fan", 0, "textures/blocks/coral_fan_blue.png"),
    BRAIN_CORAL_FAN("脑纹珊瑚扇", "minecraft:coral_fan", 1, "textures/blocks/coral_fan_pink.png"),
    BUBBLE_CORAL_FAN("气泡珊瑚扇", "minecraft:coral_fan", 2, "textures/blocks/coral_fan_purple.png"),
    FIRE_CORAL_FAN("火珊瑚扇", "minecraft:coral_fan", 3, "textures/blocks/coral_fan_red.png"),
    HORN_CORAL_FAN("鹿角珊瑚扇", "minecraft:coral_fan", 4, "textures/blocks/coral_fan_yellow.png"),
    TUBE_CORAL_FAN_DEAD("失活的管珊瑚扇", "minecraft:dead_coral_fan", 0, "textures/blocks/coral_fan_blue_dead.png"),
    BRAIN_CORAL_FAN_DEAD("失活的脑纹珊瑚扇", "minecraft:dead_coral_fan", 1, "textures/blocks/coral_fan_pink_dead.png"),
    BUBBLE_CORAL_FAN_DEAD("失活的气泡珊瑚扇", "minecraft:dead_coral_fan", 2, "textures/blocks/coral_fan_purple_dead.png"),
    FIRE_CORAL_FAN_DEAD("失活的火珊瑚扇", "minecraft:dead_coral_fan", 3, "textures/blocks/coral_fan_red_dead.png"),
    HORN_CORAL_FAN_DEAD("失活的鹿角珊瑚扇", "minecraft:dead_coral_fan", 4, "textures/blocks/coral_fan_yellow_dead.png"),

    // coral_fan_hang can not be gained from player, skipping...

    BLOCK_KELP("海带块", "minecraft:kelp_block", "textures/items/kelp.png"), // todo: no proper icon
    DRIED_KELP_BLOCK("干海带块", "minecraft:dried_kelp_block", "textures/blocks/dried_kelp_top.png"),
    ACACIA_BUTTON("金合欢按钮", "minecraft:acacia_button", "textures/blocks/planks_acacia.png"), // todo: no icon
    BIRCH_BUTTON("白桦按钮", "minecraft:birch_button", "textures/blocks/planks_birch.png"), // todo: no icon
    DARK_OAK_BUTTON("深色橡木按钮", "minecraft:dark_oak_button", "textures/blocks/planks_big_oak.png"), // todo: no icon
    JUNGLE_BUTTON("丛林木按钮", "minecraft:jungle_button", "textures/blocks/planks_jungle.png"), // todo: no icon
    SPRUCE_BUTTON("云杉按钮", "minecraft:spruce_button", "textures/blocks/planks_spruce.png"), // todo: no icon
    ACACIA_TRAPDOOR("金合欢活板门", "minecraft:acacia_trapdoor", "textures/blocks/acacia_trapdoor.png"),
    BIRCH_TRAPDOOR("白桦活板门", "minecraft:birch_trapdoor", "textures/blocks/birch_trapdoor.png"),
    DARK_OAK_TRAPDOOR("深色橡木活板门", "minecraft:dark_oak_trapdoor", "textures/blocks/dark_oak_trapdoor.png"),
    JUNGLE_TRAPDOOR("丛林木活板门", "minecraft:jungle_trapdoor", "textures/blocks/jungle_trapdoor.png"),
    SPRUCE_TRAPDOOR("云杉活板门", "minecraft:spruce_trapdoor", "textures/blocks/spruce_trapdoor.png"),
    ACACIA_PRESSURE_PLATE("金合欢压力板", "minecraft:acacia_pressure_plate", "textures/blocks/planks_acacia.png"), // todo: no icon
    BIRCH_PRESSURE_PLATE("白桦压力板", "minecraft:birch_pressure_plate", "textures/blocks/planks_birch.png"), // todo: no icon
    DARK_OAK_PRESSURE_PLATE("深色橡木压力板", "minecraft:dark_oak_pressure_plate", "textures/blocks/planks_big_oak.png"), // todo: no icon
    JUNGLE_PRESSURE_PLATE("丛林木压力板", "minecraft:jungle_pressure_plate", "textures/blocks/planks_jungle.png"), // todo: no icon
    SPRUCE_PRESSURE_PLATE("云杉压力板", "minecraft:spruce_pressure_plate", "textures/blocks/planks_spruce.png"), // todo: no icon
    CARVED_PUMPKIN("雕刻过的南瓜", "minecraft:carved_pumpkin", "textures/blocks/pumpkin_face_off.png"),
    SEA_PICKLE("海泡菜", "minecraft:sea_pickle", "textures/blocks/sea_pickle.png"),
    CONDUIT("潮涌核心", "minecraft:conduit", "textures/blocks/conduit_closed.png"),

    TURTLE_EGG("海龟蛋", "minecraft:turtle_egg", "textures/items/turtle_egg.png"),

    // bubble column can not be gained from players, skipping...

    BARRIER("屏障", "minecraft:barrier", "textures/blocks/barrier.png"),

    // todo: no icon start
    END_STONE_BRICK_SLAB("末地石砖半砖", "minecraft:end_stone_brick_slab", 0, "textures/blocks/end_bricks.png"),
    SMOOTH_RED_SANDSTONE_SLAB("平滑红砂岩半砖", "minecraft:smooth_red_sandstone_slab", 1, "textures/blocks/red_sandstone_normal.png"),
    POLISHED_ANDESITE_SLAB("磨制安山岩半砖", "minecraft:polished_andesite_slab", 2, "textures/blocks/stone_andesite_smooth.png"),
    ANDESITE_SLAB("安山岩半砖", "minecraft:andesite_slab", 3, "textures/blocks/stone_andesite.png"),
    DIORITE_SLAB("闪长岩半砖", "minecraft:diorite_slab", 4, "textures/blocks/stone_diorite.png"),
    POLISHED_DIORITE_SLAB("磨制闪长岩半砖", "minecraft:polished_diorite_slab", 5, "textures/blocks/stone_diorite_smooth.png"),
    GRANITE_SLAB("花岗岩半砖", "minecraft:granite_slab", 6, "textures/blocks/stone_granite.png"),
    POLISHED_GRANITE_SLAB("磨制花岗岩半砖", "minecraft:polished_granite_slab", 7, "textures/blocks/stone_granite_smooth.png"),
    // todo: no icon end

    BAMBOO("竹子", "minecraft:bamboo", "textures/items/bamboo.png"),
    BAMBOO_SAPLING("竹笋", "minecraft:bamboo_sapling", "textures/blocks/bamboo_sapling.png"),
    SCAFFOLDING("脚手架", "minecraft:scaffolding", "textures/blocks/scaffolding_top.tga"),

    // todo: no icon start
    MOSSY_STONE_BRICK_SLAB("苔石砖半砖", "minecraft:mossy_stone_brick_slab", 0, "textures/blocks/stonebrick_mossy.png"),
    SMOOTH_QUARTZ_SLAB("平滑石英半砖", "minecraft:smooth_quartz_slab", 1, "textures/blocks/quartz_bricks.png"),
    CUT_SANDSTONE_SLAB("切制砂岩半砖", "minecraft:cut_sandstone_slab", 3, "textures/blocks/sandstone_top.png"),
    CUT_RED_SANDSTONE_SLAB("切制红砂岩半砖", "minecraft:cut_red_sandstone_slab", 4, "textures/blocks/red_sandstone_normal.png"),
    // todo: no icon end

    // double stab can not be gained from players, skipping...
    // todo: no icon start
    GRANITE_STAIRS("花岗岩楼梯", "minecraft:granite_stairs", "textures/blocks/stone_granite.png"),
    DIORITE_STAIRS("闪长岩楼梯", "minecraft:diorite_stairs", "textures/blocks/stone_diorite.png"),
    ANDESITE_STAIRS("安山岩楼梯", "minecraft:andesite_stairs", "textures/blocks/stone_andesite.png"),
    POLISHED_GRANITE_STAIRS("磨制花岗岩楼梯", "minecraft:polished_granite_stairs", "textures/blocks/stone_granite_smooth.png"),
    POLISHED_DIORITE_STAIRS("磨制闪长岩楼梯", "minecraft:polished_diorite_stairs", "textures/blocks/stone_diorite_smooth.png"),
    POLISHED_ANDESITE_STAIRS("磨制安山岩楼梯", "minecraft:polished_andesite_stairs", "textures/blocks/stone_andesite_smooth.png"),
    MOSSY_STONE_BRICK_STAIRS("苔石砖楼梯", "minecraft:mossy_stone_brick_stairs", "textures/blocks/stonebrick_mossy.png"),
    SMOOTH_RED_SANDSTONE_STAIRS("平滑红砂岩楼梯", "minecraft:smooth_red_sandstone_stairs", "textures/blocks/red_sandstone_normal.png"),
    SMOOTH_SANDSTONE_STAIRS("平滑砂岩楼梯", "minecraft:smooth_sandstone_stairs", "textures/blocks/sandstone_smooth.png"),
    END_BRICK_STAIRS("末地石砖楼梯", "minecraft:end_brick_stairs", "textures/blocks/end_bricks.png"),
    MOSSY_COBBLESTONE_STAIRS("苔石楼梯", "minecraft:mossy_cobblestone_stairs", "textures/blocks/stonebrick_mossy.png"),
    NORMAL_STONE_STAIRS("石头楼梯", "minecraft:stone_stairs", "textures/blocks/stone.png"),
    // todo: no icon end

    SPRUCE_STANDING_SIGN("云杉木告示牌", "minecraft:spruce_standing_sign", "textures/items/sign_spruce.png"),
    SPRUCE_WALL_SIGN("云杉木墙告示牌", "minecraft:spruce_wall_sign", "textures/ui/hanging_sign_spruce.png"),

    SMOOTH_STONE("平滑石头", "minecraft:smooth_stone", "textures/blocks/stone.png"), // todo: no icon

    RED_NETHER_BRICK_STAIRS("红色下界砖楼梯", "minecraft:red_nether_brick_stairs", "textures/blocks/nether_brick.png"), // todo: no icon
    SMOOTH_QUARTZ_STAIRS("平滑石英楼梯", "minecraft:smooth_quartz_stairs", "textures/blocks/quartz_bricks.png"), // todo: no icon

    BIRCH_STANDING_SIGN("白桦木告示牌", "minecraft:birch_standing_sign", "textures/items/sign_birch.png"),
    BIRCH_WALL_SIGN("白桦木墙告示牌", "minecraft:birch_wall_sign", "textures/items/birch_hanging_sign.png"),
    JUNGLE_STANDING_SIGN("丛林木告示牌", "minecraft:jungle_standing_sign", "textures/items/sign_jungle.png"),
    JUNGLE_WALL_SIGN("丛林木墙告示牌", "minecraft:jungle_wall_sign", "textures/items/jungle_hanging_sign.png"),
    ACACIA_STANDING_SIGN("金合欢木告示牌", "minecraft:acacia_standing_sign", "textures/items/sign_acacia.png"),
    ACACIA_WALL_SIGN("金合欢木墙告示牌", "minecraft:acacia_wall_sign", "textures/items/acacia_hanging_sign.png"),
    DARKOAK_STANDING_SIGN("深色橡木告示牌", "minecraft:darkoak_standing_sign", "textures/items/sign_darkoak.png"),
    DARKOAK_WALL_SIGN("深色橡木墙告示牌", "minecraft:darkoak_wall_sign", "textures/items/dark_oak_hanging_sign.png"),

    LECTERN("讲台", "minecraft:lectern", "textures/blocks/lectern_base.png"), // todo: no icon
    GRINDSTONE("砂轮", "minecraft:grindstone", "textures/blocks/grindstone_side.tga"),

    // 450 end
    PEARLESCENT_FROGLIGHT("珠光蛙明灯", "minecraft:pearlescent_froglight", "textures/blocks/pearlescent_froglight_side.png"),
    VERDANT_FROGLIGHT("翠绿蛙明灯", "minecraft:verdant_froglight", "textures/blocks/verdant_froglight_side.png"),
    OCHRE_FROGLIGHT("赭黄蛙明灯", "minecraft:ochre_froglight", "textures/blocks/ochre_froglight_side.png"),

    CANDLE("蜡烛", "minecraft:candle", "textures/items/candles/candle.png"),
    WHITE_CANDLE("白色蜡烛", "minecraft:white_candle", "textures/items/candles/white_candle.png"),
    ORANGE_CANDLE("橙色蜡烛", "minecraft:orange_candle", "textures/items/candles/orange_candle.png"),
    MAGENTA_CANDLE("品红色蜡烛", "minecraft:magenta_candle", "textures/items/candles/magenta_candle.png"),
    LIGHT_BLUE_CANDLE("淡蓝色蜡烛", "minecraft:light_blue_candle", "textures/items/candles/light_blue_candle.png"),
    YELLOW_CANDLE("黄色蜡烛", "minecraft:yellow_candle", "textures/items/candles/yellow_candle.png"),
    LIME_CANDLE("黄绿色蜡烛", "minecraft:lime_candle", "textures/items/candles/lime_candle.png"),
    PINK_CANDLE("粉红色蜡烛", "minecraft:pink_candle", "textures/items/candles/pink_candle.png"),
    GRAY_CANDLE("灰色蜡烛", "minecraft:gray_candle", "textures/items/candles/gray_candle.png"),
    LIGHT_GRAY_CANDLE("淡灰色蜡烛", "minecraft:light_gray_candle", "textures/items/candles/light_gray_candle.png"),
    CYAN_CANDLE("青色蜡烛", "minecraft:cyan_candle", "textures/items/candles/cyan_candle.png"),
    PURPLE_CANDLE("紫色蜡烛", "minecraft:purple_candle", "textures/items/candles/purple_candle.png"),
    BLUE_CANDLE("蓝色蜡烛", "minecraft:blue_candle", "textures/items/candles/blue_candle.png"),
    BROWN_CANDLE("棕色蜡烛", "minecraft:brown_candle", "textures/items/candles/brown_candle.png"),
    GREEN_CANDLE("绿色蜡烛", "minecraft:green_candle", "textures/items/candles/green_candle.png"),
    RED_CANDLE("红色蜡烛", "minecraft:red_candle", "textures/items/candles/red_candle.png"),
    BLACK_CANDLE("黑色蜡烛", "minecraft:black_candle", "textures/items/candles/black_candle.png"),

    // 樱桃木系列方块
    CHERRY_LOG("樱桃木原木", "minecraft:cherry_log", "textures/blocks/cherry_log_top.png"),
    CHERRY_PLANKS("樱桃木板", "minecraft:cherry_planks", "textures/blocks/cherry_planks.png"),
    CHERRY_PRESSURE_PLATE("樱桃木压力板", "minecraft:cherry_pressure_plate", "textures/blocks/cherry_planks.png"),
    CHERRY_SLAB("樱桃木半砖", "minecraft:cherry_slab", "textures/blocks/cherry_planks.png"),
    CHERRY_STAIRS("樱桃木楼梯", "minecraft:cherry_stairs", "textures/blocks/cherry_planks.png"),
    CHERRY_STANDING_SIGN("樱桃木立式告示牌", "minecraft:cherry_sign", "textures/items/cherry_sign.png"),  // 实际使用自定义实体渲染
    CHERRY_TRAPDOOR("樱桃木活板门", "minecraft:cherry_trapdoor", "textures/blocks/cherry_trapdoor.png"),
    CHERRY_WALL_SIGN("樱桃木墙式告示牌", "minecraft:cherry_wall_sign", "textures/items/cherry_hanging_sign.png"),  // 实际使用自定义实体渲染
    STRIPPED_CHERRY_WOOD("去皮樱桃木", "minecraft:stripped_cherry_wood", "textures/blocks/stripped_cherry_log_top.png"),
    CHERRY_WOOD("樱桃木", "minecraft:cherry_wood", "textures/blocks/cherry_log_top.png"),  // 与原木纹理相同
    CHERRY_SAPLING("樱桃树苗", "minecraft:cherry_sapling", "textures/blocks/cherry_sapling.png"),
    CHERRY_LEAVES("樱桃树叶", "minecraft:cherry_leaves", "textures/blocks/cherry_leaves.png"),

    WITHER_SKELETON_SKULL("凋灵骷髅头颅", "minecraft:wither_skeleton_skull", "textures/items/spawn_eggs/spawn_egg_wither_skeleton.png"),
    ZOMBIE_HEAD("僵尸头颅", "minecraft:zombie_head", "textures/items/spawn_eggs/spawn_egg_zombie.png"),
    PLAYER_HEAD("玩家头颅", "minecraft:player_head", "textures/ui/icon_steve.png"),  // 实际使用玩家皮肤
    CREEPER_HEAD("苦力怕头颅", "minecraft:creeper_head", "textures/items/spawn_eggs/spawn_egg_creeper.png"),
    DRAGON_HEAD("末影龙头颅", "minecraft:dragon_head", "textures/items/dragon_egg.png"),
    PIGLIN_HEAD("猪灵头颅", "minecraft:piglin_head", "textures/items/spawn_eggs/spawn_egg_piglin.png");

    private static final Map<String, ItemIDSunName> NAME_MAP = new HashMap<>();
    private static final Map<IdDamageKey, ItemIDSunName> ID_DAMAGE_MAP = new HashMap<>();
    private static final Map<StringIdDamageKey, ItemIDSunName> STRING_ID_DAMAGE_MAP = new HashMap<>();

    // 静态块，初始化缓存
    static {
        for (ItemIDSunName item : values()) {
            NAME_MAP.put(item.name, item);
            if (item.id != 255) {
                ID_DAMAGE_MAP.put(new IdDamageKey(item.id, item.damage), item);
            } else {
                STRING_ID_DAMAGE_MAP.put(new StringIdDamageKey(item.stringIdentifier, item.damage), item);
            }
        }
    }

    private final int id, damage;
    private final String stringIdentifier;
    private final String name, path;
    /**
     * @param name   物品名称
     * @param id     物品ID
     * @param damage 物品特殊值
     * @param path   物品贴图路径
     */
    ItemIDSunName(String name, int id, int damage, String path) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.path = path;
        this.stringIdentifier = "";
    }

    /**
     * @param name 物品名称
     * @param path 物品贴图路径
     */
    ItemIDSunName(String name, String stringIdentifier, String path) {
        this(name, stringIdentifier, 0, path);
    }

    /**
     * @param name   物品名称
     * @param damage 物品特殊值
     * @param path   物品贴图路径
     */
    ItemIDSunName(String name, String stringIdentifier, int damage, String path) {
        this.id = 255;
        this.name = name;
        this.damage = damage;
        this.path = path;
        this.stringIdentifier = stringIdentifier;
    }

    /**
     * 根据物品名称获取贴图路径
     *
     * @param name 物品名称
     * @return 贴图路径
     */
    public static String getPathByName(String name) {
        ItemIDSunName item = NAME_MAP.get(name);
        return item != null ? item.path : null;
    }

    /**
     * 根据物品Id与特殊值获取贴图路径
     *
     * @param id     物品id
     * @param damage 物品特殊值
     * @return 贴图路径
     */
    public static String getPathByIdAndDamage(int id, int damage) {
        ItemIDSunName item = ID_DAMAGE_MAP.get(new IdDamageKey(id, damage));
        return item != null ? item.path : "";
    }

    /**
     * 根据物品Id获取贴图路径
     *
     * @param id 物品id
     * @return 贴图路径
     */
    public static String getPathById(int id) {
        return getPathByIdAndDamage(id, 0);
    }

    /**
     * 根据物品获取贴图路径
     *
     * @param item 物品
     * @return 贴图路径
     */
    public static String getPathByItem(Item item) {
        return getPathByIdAndDamage(item.getId(), item.getDamage());
    }

    /**
     * 根据物品获取物品中文名
     *
     * @param item 物品
     * @return 物品中文名
     */
    public static String getNameByItem(Item item) {
        if (item.getId() == 255) {
            ItemIDSunName stringIdDamageKey = STRING_ID_DAMAGE_MAP.get(new StringIdDamageKey(item.getNamespaceId(), item.getDamage()));
            return stringIdDamageKey != null ? stringIdDamageKey.name : "未知";
        } else {
            return getNameByIdAndDamage(item.getId(), item.getDamage());
        }
    }

    /**
     * 根据物品获取物品中文名
     *
     * @param id 物品Id
     * @return 物品中文名
     */
    public static String getNameById(int id) {
        return getNameByIdAndDamage(id, 0);
    }

    /**
     * 根据物品获取物品中文名
     *
     * @param id     物品id
     * @param damage 物品特殊值
     * @return 物品中文名
     */
    public static String getNameByIdAndDamage(int id, int damage) {
        ItemIDSunName item = ID_DAMAGE_MAP.get(new IdDamageKey(id, damage));
        return item != null ? item.name : "未知";
    }

    public int getId() {
        return id;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    /**
     * 联合id与damage
     */
    private static class IdDamageKey {
        private final int id;
        private final int damage;

        IdDamageKey(int id, int damage) {
            this.id = id;
            this.damage = damage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IdDamageKey that = (IdDamageKey) o;
            return id == that.id && damage == that.damage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, damage);
        }
    }

    /**
     * 联合id与damage
     */
    private static class StringIdDamageKey {
        private final String id;
        private final int damage;

        StringIdDamageKey(String id, int damage) {
            this.id = id;
            this.damage = damage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StringIdDamageKey that = (StringIdDamageKey) o;
            return id.equals(that.id) && damage == that.damage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, damage);
        }
    }
}
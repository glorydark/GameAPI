package gameapi.tools.rotation;

import cn.nukkit.block.*;
import cn.nukkit.utils.DyeColor;

import java.util.HashMap;
import java.util.Map;

public class DyeColorBehavior {

    private static final Map<Integer, DyeColor> CANDLE_COLOR_MAP = new HashMap<>();

    static {
        try { CANDLE_COLOR_MAP.put(BlockID.WHITE_CANDLE, DyeColor.WHITE); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.ORANGE_CANDLE, DyeColor.ORANGE); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.MAGENTA_CANDLE, DyeColor.MAGENTA); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.LIGHT_BLUE_CANDLE, DyeColor.LIGHT_BLUE); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.YELLOW_CANDLE, DyeColor.YELLOW); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.LIME_CANDLE, DyeColor.LIME); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.PINK_CANDLE, DyeColor.PINK); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.GRAY_CANDLE, DyeColor.GRAY); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.LIGHT_GRAY_CANDLE, DyeColor.LIGHT_GRAY); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.CYAN_CANDLE, DyeColor.CYAN); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.PURPLE_CANDLE, DyeColor.PURPLE); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.BLUE_CANDLE, DyeColor.BLUE); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.BROWN_CANDLE, DyeColor.BROWN); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.GREEN_CANDLE, DyeColor.GREEN); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.RED_CANDLE, DyeColor.RED); } catch (Throwable ignored) {}
        try { CANDLE_COLOR_MAP.put(BlockID.BLACK_CANDLE, DyeColor.BLACK); } catch (Throwable ignored) {}
    }

    public static DyeColor getDyeColor(int blockId, int damage) {
        DyeColor candleColor = CANDLE_COLOR_MAP.get(blockId);
        if (candleColor != null) return candleColor;
        Block block = Block.get(blockId);
        if (block instanceof BlockShulkerBox && !(block instanceof BlockUndyedShulkerBox)) {
            return ((BlockShulkerBox) block).getDyeColor();
        }
        return DyeColor.getByWoolData(damage & 0x0f);
    }

    public static boolean isDyeColorBlock(int blockId) {
        Block block = Block.get(blockId);
        return block instanceof BlockWool
                || block instanceof BlockGlassStained
                || block instanceof BlockGlassPaneStained
                || block instanceof BlockTerracottaStained
                || block instanceof BlockConcrete
                || block instanceof BlockConcretePowder
                || block instanceof BlockShulkerBox
                || CANDLE_COLOR_MAP.containsKey(blockId)
                || blockId == BlockID.GLASS || blockId == BlockID.GLASS_PANE
                || blockId == BlockID.TINTED_GLASS;
    }

    public static int getCandleBlockId(DyeColor color) {
        for (Map.Entry<Integer, DyeColor> entry : CANDLE_COLOR_MAP.entrySet()) {
            if (entry.getValue() == color) return entry.getKey();
        }
        return BlockID.CANDLE;
    }
}

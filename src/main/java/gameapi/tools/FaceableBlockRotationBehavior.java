package gameapi.tools;

import cn.nukkit.block.*;
import cn.nukkit.nbt.tag.CompoundTag;
import gameapi.GameAPI;
import gameapi.tools.rotation.*;

import java.util.HashMap;
import java.util.Map;

public abstract class FaceableBlockRotationBehavior {

    private static final Map<Class<?>, FaceableBlockRotationBehavior> REGISTRY = new HashMap<>();
    private static final Map<Integer, FaceableBlockRotationBehavior> CACHE = new HashMap<>();

    static {
        try { GameAPI.getInstance().getLogger().info("[FRB] static init"); } catch (Throwable ignored) {}
        try { register(BlockDoor.class, new DoorBehavior()); } catch (Throwable ignored) {}
        try { register(BlockTrapdoor.class, new TrapdoorBehavior()); } catch (Throwable ignored) {}
        try { register(BlockStairs.class, new StairsBehavior()); } catch (Throwable ignored) {}
        try { register(BlockFenceGate.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockWallBanner.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockBanner.class, new SixteenDirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockWallSign.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockSignPost.class, new SixteenDirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockLadder.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockChest.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockFurnaceBurning.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockObserver.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockHopper.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockSkullSkeleton.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
        try { register(BlockDispenser.class, new BlockFaceIndexWithPowerBehavior()); } catch (Throwable ignored) {}
        try { register(BlockDropper.class, new BlockFaceIndexWithPowerBehavior()); } catch (Throwable ignored) {}
        try { register(BlockEndRod.class, new BlockFaceIndexWithPowerBehavior()); } catch (Throwable ignored) {}
        try { register(BlockPistonBase.class, new AllFacingBehavior()); } catch (Throwable ignored) {}
        try { register(BlockPistonHead.class, new AllFacingBehavior()); } catch (Throwable ignored) {}
        try { register(BlockCommandBlock.class, new AllFacingBehavior()); } catch (Throwable ignored) {}
        try { register(BlockPumpkin.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockBed.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockEndPortalFrame.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockCocoa.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockTripWireHook.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockAnvil.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockItemFrame.class, new Simple4DirBehavior()); } catch (Throwable ignored) {}
        try { register(BlockButton.class, new ButtonBehavior()); } catch (Throwable ignored) {}
        try { register(BlockLever.class, new LeverBehavior()); } catch (Throwable ignored) {}
        try { register(BlockTorch.class, new TorchBehavior()); } catch (Throwable ignored) {}
        try { register(BlockVine.class, new VineBehavior()); } catch (Throwable ignored) {}
        try { register(BlockGlowLichen.class, new GlowLichenBehavior()); } catch (Throwable ignored) {}
        try { register(BlockMushroom.class, new MushroomBehavior()); } catch (Throwable ignored) {}
        try { register(BlockRail.class, new RailBehavior()); } catch (Throwable ignored) {}
        try { register(BlockRailPowered.class, new PoweredRailBehavior()); } catch (Throwable ignored) {}
        try { register(BlockRailDetector.class, new PoweredRailBehavior()); } catch (Throwable ignored) {}
        try { register(BlockRailActivator.class, new PoweredRailBehavior()); } catch (Throwable ignored) {}
        try { register(BlockWood.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockWood2.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockWoodBark.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockWoodStripped.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockHayBale.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockQuartz.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockPurpur.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockBone.class, new AxisBehavior()); } catch (Throwable ignored) {}
        try { register(BlockShulkerBox.class, new ShulkerBoxBehavior()); } catch (Throwable ignored) {}
        try { register(BlockEnderChest.class, new BlockFaceIndexBehavior()); } catch (Throwable ignored) {}
    }

    public static void register(Class<? extends Block> clazz, FaceableBlockRotationBehavior behavior) {
        REGISTRY.put(clazz, behavior);
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public static int rotate(int blockId, int damage, int rotationDegree) {
        if (blockId == Block.AIR) return damage;

        FaceableBlockRotationBehavior behavior = CACHE.get(blockId);
        if (behavior == null && !CACHE.containsKey(blockId)) {
            behavior = resolveBehavior(blockId);
            CACHE.put(blockId, behavior);
            log("Cached blockId=" + blockId + " -> " + (behavior != null ? behavior.getClass().getSimpleName() : "null"));
        }
        if (behavior == null) {
            log("No behavior for blockId=" + blockId + " damage=" + damage);
            return damage;
        }
        int result = behavior.rotateDamage(blockId, damage, rotationDegree);
        log("rotate(blockId=" + blockId + " dmg=" + damage + " deg=" + rotationDegree + ") -> " + result + " via " + behavior.getClass().getSimpleName());
        return result;
    }

    public static void rotateNbtData(int blockId, CompoundTag tag, int rotationDegree) {
        FaceableBlockRotationBehavior behavior = CACHE.get(blockId);
        if (behavior == null && !CACHE.containsKey(blockId)) {
            behavior = resolveBehavior(blockId);
            CACHE.put(blockId, behavior);
        }
        if (behavior != null) {
            behavior.modifyNbt(blockId, tag, rotationDegree);
        }
    }

    public void modifyNbt(int blockId, CompoundTag tag, int rotationDegree) {
    }

    private static void log(String msg) {
        try {
            GameAPI.getInstance().getLogger().info("[FRB] " + msg);
        } catch (Throwable ignored) {}
    }

    private static FaceableBlockRotationBehavior resolveBehavior(int blockId) {
        Block block = Block.get(blockId);
        if (block == null || block.getClass() == BlockUnknown.class) return null;
        Class<?> clazz = block.getClass();
        while (clazz != null && clazz != Block.class && clazz != Object.class) {
            FaceableBlockRotationBehavior behavior = REGISTRY.get(clazz);
            if (behavior != null) return behavior;
            clazz = clazz.getSuperclass();
        }
        if (block instanceof BlockStairs) return new StairsBehavior();
        if (block instanceof BlockTrapdoor) return new TrapdoorBehavior();
        if (block instanceof BlockDoor) return new DoorBehavior();
        if (block instanceof BlockFenceGate) return new Simple4DirBehavior();
        return null;
    }

    public abstract int rotateDamage(int blockId, int damage, int rotationDegree);

    protected static int normalizeDegree(int rotationDegree) {
        rotationDegree = rotationDegree % 360;
        if (rotationDegree < 0) rotationDegree += 360;
        return rotationDegree;
    }
}

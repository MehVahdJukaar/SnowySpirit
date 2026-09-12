package net.mehvahdjukaar.snowyspirit.integration.supp;

import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.supplementaries.common.block.tiles.SackBlockTile;
import net.mehvahdjukaar.supplementaries.common.inventories.VariableSizeContainerMenu;
import net.mehvahdjukaar.supplementaries.common.items.CandyItem;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModMenuTypes;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SuppCompat {

    public static boolean isSack(Item i) {
        return i instanceof SackItem;
    }

    public static AbstractContainerMenu createSackMenu(int id, Inventory inventory, Container container) {
        return new VariableSizeContainerMenu(ModMenuTypes.SACK.get(),
                id, inventory, container, SackBlockTile. getUnlockedSlots());
    }

    public static void triggerSweetTooth(Level level, LivingEntity entity) {
        CandyItem.increaseSweetTooth(level, entity, 8 * 20);
    }

    public static boolean isCandy(ItemStack stack) {
        return CommonConfigs.Tools.CANDY_ENABLED.get() ? stack.is(ModRegistry.CANDY_ITEM.get()) : stack.is(Items.PUMPKIN_PIE);
    }

    public static boolean isGlobe(ItemStack stack) {
        return CommonConfigs.Building.GLOBE_SEPIA.get() ? stack.is(ModRegistry.GLOBE_SEPIA.get().asItem()) :
                CommonConfigs.Building.GLOBE_ENABLED.get() ? stack.is(ModRegistry.GLOBE.get().asItem()) : stack.is(Items.BELL);

    }

    public static void openSackMenu(ServerPlayer pPlayer, ContainerHolderEntity containerHolderEntity) {
        VariableSizeContainerMenu.openEntityMenu(pPlayer, containerHolderEntity);
    }

    public static Map<DyeColor, Supplier<Block>> registerRopedGlowLights() {
        return Util.make(() -> {
            var m = new LinkedHashMap<DyeColor, Supplier<Block>>();
            for (DyeColor c : BlocksColorAPI.SORTED_COLORS) {
                m.put(c, regRoped("roped_glow_lights_" + c.getName(), c));
            }
            m.put(null, regRoped("roped_glow_lights_prismatic", null));
            return m;
        });
    }

    private static Supplier<Block> regRoped(String name, DyeColor color) {
        return net.mehvahdjukaar.snowyspirit.reg.ModRegistry.regBlock(name,
                () -> new RopedGlowLightsBlock(color, BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_WOOL)
                        .strength(0.25f)
                        .speedFactor(0.7f)
                        .noOcclusion()
                        .noLootTable()
                        .lightLevel(s -> 6)));
    }

    public static boolean placeOnRope(UseOnContext context, DyeColor color) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!state.is(ModRegistry.ROPE.get())) return false;
        level.setBlockAndUpdate(pos, ropeToGlowLights(state, color));
        level.playSound(context.getPlayer(), pos, SoundEvents.AMETHYST_CLUSTER_HIT, SoundSource.BLOCKS, 1.0F, 0.8F);
        return true;
    }

    public static BlockState ropeToGlowLights(BlockState state, DyeColor color) {
        return net.mehvahdjukaar.snowyspirit.reg.ModRegistry.ROPED_GLOW_LIGHTS.get(color).get()
                .withPropertiesOf(state);
    }

    public static BlockState glowLightsToRope(BlockState state) {
        return ModRegistry.ROPE.get().withPropertiesOf(state);
    }

    public static ItemStack ropeStack() {
        return ModRegistry.ROPE_ITEM.get().getDefaultInstance();
    }
}

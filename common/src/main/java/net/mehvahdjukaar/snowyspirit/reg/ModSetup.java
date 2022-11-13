package net.mehvahdjukaar.snowyspirit.reg;

import com.google.common.collect.HashBiMap;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.snowyspirit.common.ai.WinterVillagerAI;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DispenserBlock;

public class ModSetup {


    public static void setup() {

        ModRegistry.CARPETS = Util.make(HashBiMap.create(), m -> {
            for (DyeColor c : DyeColor.values()) {
                m.put(c, Registry.ITEM.getOptional(new ResourceLocation(c.getName() + "_carpet")).orElse(
                        Registry.ITEM.get(new ResourceLocation("tinted", c.getName() + "_carpet"))));
            }
        });

        NetworkHandler.registerMessages();

        ComposterBlock.COMPOSTABLES.put(ModRegistry.GINGER_FLOWER.get(), 0.3F);
        ComposterBlock.COMPOSTABLES.put(ModRegistry.GINGER.get(), 0.65F);
        ComposterBlock.COMPOSTABLES.put(ModRegistry.GINGER_WILD.get(), 0.65F);


        WinterVillagerAI.setup();

        ModRegistry.SLED_ITEMS.forEach((key, value) ->
                DispenserBlock.registerBehavior(value, new SledDispenserBehavior(key)));

    }


    public static class SledDispenserBehavior extends DefaultDispenseItemBehavior {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
        private final WoodType type;

        public SledDispenserBehavior(WoodType pType) {
            this.type = pType;
        }

        @Override
        public ItemStack execute(BlockSource pSource, ItemStack pStack) {
            Direction direction = pSource.getBlockState().getValue(DispenserBlock.FACING);
            Level level = pSource.getLevel();
            double d0 = pSource.x() + (double) ((float) direction.getStepX() * 1.2F);
            double d1 = pSource.y() + (double) ((float) direction.getStepY() * 1.2F);
            double d2 = pSource.z() + (double) ((float) direction.getStepZ() * 1.2F);
            BlockPos blockpos = pSource.getPos().relative(direction);

            if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos).isEmpty()) {
                return this.defaultDispenseItemBehavior.dispense(pSource, pStack);
            }


            SledEntity sled = new SledEntity(level, d0, d1, d2);
            sled.setWoodType(this.type);
            sled.setYRot(direction.toYRot());
            level.addFreshEntity(sled);
            pStack.shrink(1);
            return pStack;
        }

        @Override
        protected void playSound(BlockSource pSource) {
            pSource.getLevel().levelEvent(1000, pSource.getPos(), 0);
        }
    }
}

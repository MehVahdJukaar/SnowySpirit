package net.mehvahdjukaar.snowyspirit.init;

import net.mehvahdjukaar.selene.util.DispenserHelper;
import net.mehvahdjukaar.snowyspirit.common.ai.WinterVillagerAI;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.common.generation.WorldGenHandler;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NetworkHandler.registerMessages();
            WorldGenHandler.setup(event);
            WinterVillagerAI.init();

            ModRegistry.SLED_ITEMS.forEach((key, value) ->
                    DispenserBlock.registerBehavior(value.get(), new SledDispenserBehavior(key)));

        });
    }


    public static class SledDispenserBehavior extends DefaultDispenseItemBehavior {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
        private final Boat.Type type;

        public SledDispenserBehavior(Boat.Type pType) {
            this.type = pType;
        }

        @Override
        public ItemStack execute(BlockSource pSource, ItemStack pStack) {
            Direction direction = pSource.getBlockState().getValue(DispenserBlock.FACING);
            Level level = pSource.getLevel();
            double d0 = pSource.x() + (double)((float)direction.getStepX() * 1.125F);
            double d1 = pSource.y() + (double)((float)direction.getStepY() * 1.125F);
            double d2 = pSource.z() + (double)((float)direction.getStepZ() * 1.125F);
            BlockPos blockpos = pSource.getPos().relative(direction);

            if (!level.getBlockState(blockpos).isAir() || !level.getFluidState(blockpos.below()).is(FluidTags.WATER)) {
                return this.defaultDispenseItemBehavior.dispense(pSource, pStack);
            }


            SledEntity boat = new SledEntity(level, d0, d1, d2);
            boat.setWoodType(this.type);
            boat.setYRot(direction.toYRot());
            level.addFreshEntity(boat);
            pStack.shrink(1);
            return pStack;
        }

        @Override
        protected void playSound(BlockSource pSource) {
            pSource.getLevel().levelEvent(1000, pSource.getPos(), 0);
        }
    }
}

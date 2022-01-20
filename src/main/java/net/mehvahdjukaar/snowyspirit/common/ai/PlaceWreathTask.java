package net.mehvahdjukaar.snowyspirit.common.ai;


import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.block.WreathBlock;
import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Random;

public class PlaceWreathTask extends Behavior<Villager> {
    private final float speedModifier;
    private BlockPos targetPos;
    private int ticksSinceReached = 0;
    private int cooldown = 20 * 20;

    public PlaceWreathTask(float speed) {
        super(ImmutableMap.of(
                        // MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT,
                        ModRegistry.WREATH_POS.get(), MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                190, 270);
        this.speedModifier = speed;

    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (cooldown-- > 0) return false;
        if (pOwner.isBaby()) return false;
        if(!Christmas.isChristmasSeason(pOwner.level)) return false;
        //doesnt always start and gets put on cooldown
        if (!ForgeEventFactory.getMobGriefingEvent(pLevel, pOwner)) {
            cooldown = 20 * 60;
            return false;
        }

        return true;
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        this.cooldown = 10 * (10 + pLevel.random.nextInt(10)) + pLevel.random.nextInt(20);
        this.ticksSinceReached = 0;
        targetPos = getValidPumpkinPos(pLevel, pEntity);

        if (targetPos != null) {
            pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 1));
            displayAsHeldItem(pEntity, ModRegistry.WREATH_ITEM.get().getDefaultInstance());
        }
    }

    @Override
    protected void stop(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        super.stop(pLevel, pEntity, pGameTime);
        clearHeldItem(pEntity);
        targetPos = null;
    }

    @Override
    protected boolean canStillUse(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        return targetPos != null && isValidPlacementSpot(pLevel, targetPos);
    }

    @Override
    protected void tick(ServerLevel pLevel, Villager pOwner, long pGameTime) {
        if (targetPos != null) {
            //hax
            pOwner.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            pOwner.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 2));

            pOwner.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(targetPos));
            if (targetPos.closerThan(pOwner.position(), 2.3)) {
                this.ticksSinceReached++;
                if (ticksSinceReached > 20) {


                    if (WreathBlock.placeWreathOnDoor(targetPos, pLevel)) {
                        pOwner.getBrain().setMemory(ModRegistry.WREATH_POS.get(), GlobalPos.of(pLevel.dimension(), targetPos));
                    }
                    //so taks ends
                    targetPos = null;
                }
            }
        }
    }

    //check if villager is at correct y level befoire calling this
    @Nullable
    private static BlockPos getValidPumpkinPos(ServerLevel pLevel, LivingEntity pWalker) {
        Random random = pWalker.getRandom();

        BlockPos meeting = pWalker.blockPosition();


        //10 tries
        for (int i = 0; i < 6; ++i) {
            BlockPos pos = meeting.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (isValidPlacementSpot(pLevel, pos)) {
                return pos;
            }
        }

        return null;
    }

    public static boolean isValidPlacementSpot(ServerLevel serverLevel, BlockPos pos) {

        BlockState state = serverLevel.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock) {
            boolean lower = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;

            var c = serverLevel.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
            return c != null && (lower ? !c.hasWreath(pos.above()) : !c.hasWreath(pos));
        }

        return false;
    }

    public static void clearHeldItem(Villager self) {
        self.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        self.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }

    public static void displayAsHeldItem(Villager self, ItemStack stack) {
        self.setItemSlot(EquipmentSlot.MAINHAND, stack);
        self.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }


}

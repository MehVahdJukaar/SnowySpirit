package net.mehvahdjukaar.snowyspirit.common.ai;


import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PresentBlock;
import net.mehvahdjukaar.supplementaries.common.block.tiles.PresentBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Random;

public class PlacePresentTask extends Behavior<Villager> {
    private final float speedModifier;
    private BlockPos targetPos;
    private int ticksSinceReached = 0;
    private int cooldown = 20 * 30;

    public PlacePresentTask(float speed) {
        super(ImmutableMap.of(
                       // MemoryModuleType.INTERACTION_TARGET, MemoryStatus.VALUE_ABSENT,
                        ModRegistry.PLACED_PRESENT.get(), MemoryStatus.VALUE_ABSENT,
                        MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                190, 270);
        this.speedModifier = speed;

    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel pLevel, Villager pOwner) {
        if (cooldown-- > 0) return false;
        if (pOwner.isBaby()) return false;
        //doesnt always start and gets put on cooldown
        if (!ForgeEventFactory.getMobGriefingEvent(pLevel, pOwner) || pLevel.random.nextInt(5)!=0) {
            cooldown = 20 * 60;
            return false;
        }
        var meeting = pOwner.getBrain().getMemory(MemoryModuleType.MEETING_POINT);
        if(meeting.isEmpty() || pOwner.level.dimension() != meeting.get().dimension() || meeting.get().pos().distSqr(pOwner.blockPosition())>15*15){
            cooldown = 20 * 20;
            return false;
        }

        return true;
    }

    @Override
    protected void start(ServerLevel pLevel, Villager pEntity, long pGameTime) {
        this.cooldown = 20 * (10 + pLevel.random.nextInt(10)) + pLevel.random.nextInt(20);
        this.ticksSinceReached = 0;
        targetPos = getValidPumpkinPos(pLevel, pEntity);

        if (targetPos != null) {
            pEntity.getBrain().eraseMemory(MemoryModuleType.INTERACTION_TARGET);
            pEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(targetPos, this.speedModifier, 1));
            displayAsHeldItem(pEntity, getRandomPresent(pLevel.random));
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
                    ItemStack stack = pOwner.getMainHandItem();
                    if(stack.getItem() instanceof BlockItem blockItem){
                        BlockState state = blockItem.getBlock().defaultBlockState().setValue(PresentBlock.PACKED, true);
                        pLevel.setBlockAndUpdate(targetPos, state);
                        SoundType soundtype = state.getSoundType(pLevel, targetPos, null);
                        pLevel.playSound(null, targetPos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        pOwner.getBrain().setMemory(ModRegistry.PLACED_PRESENT.get(), true);
                        if(pLevel.getBlockEntity(targetPos) instanceof PresentBlockTile tile){
                            tile.setLootTable(Christmas.res("chests/present_villager"), pLevel.getRandom().nextLong());
                            tile.setSender(pOwner.getName().getString());
                        }
                    }

                    targetPos = null;
                }
            }
        }
    }

    //check if villager is at correct y level befoire calling this
    @Nullable
    private static BlockPos getValidPumpkinPos(ServerLevel pLevel, LivingEntity pWalker) {
        Random random = pWalker.getRandom();
        var v = pWalker.getBrain().getMemory(MemoryModuleType.MEETING_POINT);
        if(v.isEmpty())return null;
        BlockPos meeting = v.get().pos();


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
        if (serverLevel.canSeeSky(pos) && (double) serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() >= serverLevel.getSeaLevel()-1) {
            BlockState state = serverLevel.getBlockState(pos);
            if (state.getMaterial().isReplaceable() && state.getFluidState().isEmpty()) {

                BlockState below = serverLevel.getBlockState(pos.below());
                return below.isFaceSturdy(serverLevel, pos, Direction.UP);
            }
        }
        return false;
    }

    public ItemStack getRandomPresent(Random random){
        return net.mehvahdjukaar.supplementaries.setup.ModRegistry.PRESENTS_ITEMS
                .get(DyeColor.values()[random.nextInt(DyeColor.values().length)]).get().getDefaultInstance();
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

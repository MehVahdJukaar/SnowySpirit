package net.mehvahdjukaar.snowyspirit.common.wreath;

import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;

public class WreathHandler {

    public static boolean isDoor(BlockState state) {
        return state.hasProperty(BlockStateProperties.DOOR_HINGE) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING);
    }

    public static boolean canHoldWreath(BlockState state) {
        return isDoor(state) || state.getBlock() instanceof TrapDoorBlock;
    }

    public static BlockPos getWreathPos(BlockState door, BlockPos pos) {
        if (door.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            return door.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
        }
        // dramatic doors tall doors. goes in the middle one
        Property<?> third = door.getBlock().getStateDefinition().getProperty("third");

        if (third != null) {
            return switch (getValueName(door, third)) {
                case "lower" -> pos.above();
                case "upper" -> pos.below();
                default -> pos;
            };
        }
        return pos;
    }


    private static <T extends Comparable<T>> String getValueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    public static boolean placeWreathOnDoor(BlockPos pos, Level level) {
        BlockState door = level.getBlockState(pos);
        //if (!isDoor(door)) return false;
        if (!canHoldWreath(door)) return false;

        BlockPos p = getWreathPos(door, pos);
        WreathData wreathData = WreathData.getOrCreateAt(level, pos);
        if (wreathData.hasWreathAt(p)) return false;

        if (level instanceof ServerLevel sl) {
            BlockState state = ModRegistry.WREATH.get().defaultBlockState();

            wreathData.setWreath(p, level);
            wreathData.markDirtyAndSync(p, sl);
            SoundType soundtype = state.getSoundType();
            level.playSound(null, p, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        }
        return true;
    }


    public static boolean shearWreathOffDoor(BlockPos pos, Level level) {
        BlockState door = level.getBlockState(pos);
        if (!canHoldWreath(door)) return false;

        BlockPos p = getWreathPos(door, pos);
        WreathData wreathData = WreathData.getAt(level, p);
        if (wreathData == null || !wreathData.hasWreathAt(p)) return false;
        if (level instanceof ServerLevel sl) {
            wreathData.removeWreath(p, level, true);
            wreathData.markDirtyAndSync(p, sl);
            level.playSound(null, p, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return true;
    }

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos) {
        if (!player.mayBuild()) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(ModRegistry.WREATH.get().asItem())) {

            if (placeWreathOnDoor(pos, level)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else if (stack.getItem() instanceof ShearsItem && shearWreathOffDoor(pos, level)) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }


    public static void removeWreathAt(BlockPos pos, ServerLevel world) {
        ChunkAccess chunk = world.getChunkAt(pos);
        WreathData c = ModRegistry.WREATH_CHUNK_DATA.getOrNull(chunk);
        if (c != null) {
            if(c.removeWreath(pos, world, true)) {
                c.markDirtyAndSync(pos, world);
            }
        }
    }
}

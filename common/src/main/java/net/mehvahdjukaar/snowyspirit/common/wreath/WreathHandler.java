package net.mehvahdjukaar.snowyspirit.common.wreath;

import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;

public class WreathHandler {

    public static boolean placeWreathOnDoor(BlockPos pos, Level level) {
        BlockState door = level.getBlockState(pos);
        if (!(door.getBlock() instanceof DoorBlock)) return false;

        boolean lower = door.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
        BlockPos p = lower ? pos.above() : pos;
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


    public static InteractionResult onRightClickBlock(Player player, Level level, ItemStack stack, BlockPos pos) {
        if (!player.mayBuild()) return InteractionResult.PASS;

        if (stack.is(ModRegistry.WREATH.get().asItem())) {

            if (placeWreathOnDoor(pos, level)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
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

package net.mehvahdjukaar.snowyspirit.common.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WreathBlock extends HorizontalDirectionalBlock {

    private static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);


    public WreathBlock(Properties properties) {
        super(properties);
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)){
            default -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getClickedFace();
        if(direction.getAxis().isVertical()) {
            for(Direction d : Direction.Plane.HORIZONTAL) {
                BlockState blockstate = this.defaultBlockState().setValue(FACING, d);
                if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                    return blockstate;
                }
            }
        }else{
            BlockState blockstate = this.defaultBlockState().setValue(FACING, direction);
            if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
                return blockstate;
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        BlockPos blockpos = pPos.relative(pState.getValue(FACING).getOpposite());
        return pLevel.getBlockState(blockpos).isRedstoneConductor(pLevel,pPos);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pState.getValue(FACING).getOpposite() == pFacing && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }


    //TODO: readd
//    public static boolean placeWreathOnDoor(BlockPos pos, Level level){
//        var c = level.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
//        if (c != null  && !c.hasWreath(pos)) {
//            BlockState door = level.getBlockState(pos);
//            if (door.getBlock() instanceof DoorBlock) {
//
//                if(level instanceof ServerLevel serverLevel) {
//                    BlockState state = ModRegistry.WREATH.get().defaultBlockState();
//                    boolean lower = door.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER;
//                    BlockPos p = lower ? pos.above() : pos;
//                    c.updateWeathBlock(p, level);
//                    //pLevel.setBlockAndUpdate(targetPos, state);
//                    SoundType soundtype = state.getSoundType(level, p, null);
//                    level.playSound(null, p, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//                    //send packet to clients
//                    NetworkHandler.sendToAllInRangeClients(p, serverLevel, Integer.MAX_VALUE,
//                            new ClientBoundSyncWreath(p, true,
//                                    door.getValue(DoorBlock.FACING), door.getValue(DoorBlock.OPEN)));
//
//                }
//                return true;
//            }
//        }
//        return false;
//    }
}

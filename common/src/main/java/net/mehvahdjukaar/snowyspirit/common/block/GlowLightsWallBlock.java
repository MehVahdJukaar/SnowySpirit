package net.mehvahdjukaar.snowyspirit.common.block;

import net.mehvahdjukaar.moonlight.api.block.IColored;
import net.mehvahdjukaar.moonlight.api.block.IWashable;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class GlowLightsWallBlock extends Block implements IColored, IWashable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<Direction, VoxelShape> SHAPES = Util.make(() -> {
        Map<Direction, VoxelShape> m = new EnumMap<>(Direction.class);
        m.put(Direction.NORTH, box(0, 0, 15, 16, 16, 16));
        m.put(Direction.SOUTH, box(0, 0, 0, 16, 16, 1));
        m.put(Direction.WEST, box(15, 0, 0, 16, 16, 16));
        m.put(Direction.EAST, box(0, 0, 0, 1, 16, 16));
        return m;
    });

    public final DyeColor color;

    public GlowLightsWallBlock(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos behind = pos.relative(facing.getOpposite());
        return level.getBlockState(behind).isFaceSturdy(level, behind, facing);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ?
                Blocks.AIR.defaultBlockState() : state;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();
        for (Direction dir : context.getNearestLookingDirections()) {
            if (dir.getAxis().isHorizontal()) {
                state = state.setValue(FACING, dir.getOpposite());
                if (state.canSurvive(context.getLevel(), context.getClickedPos())) return state;
            }
        }
        return null;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public boolean supportsBlankColor() {
        return true;
    }

    @Override
    public boolean tryWash(Level level, BlockPos pos, BlockState state, Vec3 hitPos) {
        if (this.color == DyeColor.WHITE) return false;
        level.setBlockAndUpdate(pos, ModRegistry.GLOW_LIGHTS_WALL_BLOCKS.get(DyeColor.WHITE).get()
                .withPropertiesOf(state));
        return true;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ModRegistry.GLOW_LIGHTS_ITEMS.get(this.color).get().getDefaultInstance();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() > 0.4f) return;
        Direction facing = state.getValue(FACING);
        Vec3 center = Vec3.atCenterOf(pos).relative(facing, -0.43);
        double x = center.x + (facing.getAxis() == Direction.Axis.X ? 0 : Mth.nextDouble(random, -0.5, 0.5));
        double y = center.y + Mth.nextDouble(random, -0.5, 0.5);
        double z = center.z + (facing.getAxis() == Direction.Axis.Z ? 0 : Mth.nextDouble(random, -0.5, 0.5));
        var c = ClientDynamicResourcesHandler.getGlowLightColor(color, random);
        level.addParticle(ModRegistry.GLOW_LIGHT_PARTICLE.get(), x, y, z, c[0], c[1], c[2]);
    }
}

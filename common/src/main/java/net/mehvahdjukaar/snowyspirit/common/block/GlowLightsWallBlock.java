package net.mehvahdjukaar.snowyspirit.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mehvahdjukaar.moonlight.api.block.IColored;
import net.mehvahdjukaar.moonlight.api.block.IWashable;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GlowLightsWallBlock extends MultifaceBlock implements IColored, IWashable {

    public static final MapCodec<GlowLightsWallBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            DyeColor.CODEC.optionalFieldOf("color").forGetter(b -> Optional.ofNullable(b.color)),
            propertiesCodec()
    ).apply(i, (c, p) -> new GlowLightsWallBlock(c.orElse(null), p)));

    public final DyeColor color;
    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    public GlowLightsWallBlock(DyeColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    @Override
    protected MapCodec<GlowLightsWallBlock> codec() {
        return CODEC;
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return spreader;
    }

    @Override
    protected boolean isFaceSupported(Direction face) {
        return face.getAxis().isHorizontal();
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return context.getItemInHand().is(this.asItem()) && super.canBeReplaced(state, context);
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
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!hasFace(state, dir) || random.nextFloat() > 0.15f) continue;
            Vec3 v = Vec3.atCenterOf(pos).relative(dir, 0.43);
            double x = v.x + (dir.getAxis() == Direction.Axis.X ? 0 : Mth.nextDouble(random, -0.5, 0.5));
            double y = v.y + Mth.nextDouble(random, -0.5, 0.5);
            double z = v.z + (dir.getAxis() == Direction.Axis.Z ? 0 : Mth.nextDouble(random, -0.5, 0.5));
            var c = ClientDynamicResourcesHandler.getGlowLightColor(color, random);
            level.addParticle(ModRegistry.GLOW_LIGHT_PARTICLE.get(), x, y, z, c[0], c[1], c[2]);
        }
    }
}

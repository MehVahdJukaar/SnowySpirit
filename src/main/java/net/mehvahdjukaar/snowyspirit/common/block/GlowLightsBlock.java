package net.mehvahdjukaar.snowyspirit.common.block;

import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GlowLightsBlock extends Block implements IForgeBlock, EntityBlock, IForgeShearable {

    public final DyeColor color;

    public GlowLightsBlock(DyeColor color) {
        super(Properties.copy(Blocks.OAK_LEAVES).lightLevel(s->12));
        this.color = color;
    }


    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof GlowLightsBlockTile tile) {
            //checks again if the content itself can be mined
            BlockState heldState = tile.mimic;
            if (builder.getParameter(LootContextParams.THIS_ENTITY) instanceof ServerPlayer player) {
                if (!heldState.canHarvestBlock(builder.getLevel(), new BlockPos(builder.getParameter(LootContextParams.ORIGIN)), player)) {
                    return drops;
                }
            }
            List<ItemStack> newDrops = heldState.getDrops(builder);
            drops.addAll(newDrops);

        }
        return drops;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new GlowLightsBlockTile(pPos, pState);
    }



    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.empty();
    }

    @Override
    public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
        if (pLevel.isRainingAt(pPos.above())) {
            if (pRandom.nextInt(15) == 1) {
                BlockPos blockpos = pPos.below();
                BlockState blockstate = pLevel.getBlockState(blockpos);
                if (!blockstate.canOcclude() || !blockstate.isFaceSturdy(pLevel, blockpos, Direction.UP)) {
                    double d0 = (double)pPos.getX() + pRandom.nextDouble();
                    double d1 = (double)pPos.getY() - 0.05D;
                    double d2 = (double)pPos.getZ() + pRandom.nextDouble();
                    pLevel.addParticle(ParticleTypes.DRIPPING_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if(stack.is(Tags.Items.SHEARS)){
            var drops = this.onSheared(pPlayer, stack, level, pos, 0);
            drops.forEach(d -> {
                ItemEntity ent = new ItemEntity(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, d);
                ent.setDefaultPickUpDelay();
                level.addFreshEntity(ent);
                Random r = level.random;
                ent.setDeltaMovement(ent.getDeltaMovement().add((r.nextFloat() - r.nextFloat()) * 0.1F, r.nextFloat() * 0.05F, (r.nextFloat() - r.nextFloat()) * 0.1F));
            });
            stack.hurtAndBreak(1, pPlayer, e -> e.broadcastBreakEvent(pHand));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(pState, level, pos, pPlayer, pHand, pHit);
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
        if(world.getBlockEntity(pos)  instanceof GlowLightsBlockTile tile) {
            world.playSound(player, pos, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!world.isClientSide()) {
                world.setBlockAndUpdate(pos, tile.mimic);
                return Collections.singletonList(ModRegistry.GLOW_LIGHTS_ITEMS.get(color).get().getDefaultInstance());
            }
        }
        return Collections.emptyList();
    }
}

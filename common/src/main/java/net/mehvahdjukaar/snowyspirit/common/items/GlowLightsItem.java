package net.mehvahdjukaar.snowyspirit.common.items;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.block.GlowLightsBlock;
import net.mehvahdjukaar.snowyspirit.common.block.GlowLightsBlockTile;
import net.mehvahdjukaar.snowyspirit.integration.supp.SuppCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GlowLightsItem extends BlockItem {

    private final Block wallBlock;

    public GlowLightsItem(Block block, Block wallBlock) {
        super(block, new Properties());
        this.wallBlock = wallBlock;
    }

    private DyeColor color() {
        return ((GlowLightsBlock) this.getBlock()).color;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        super.registerBlocks(blockToItemMap, item);
        blockToItemMap.put(this.wallBlock, item);
    }

    public static class SelfPlacementContext extends BlockPlaceContext {
        public SelfPlacementContext(Player player, InteractionHand interactionHand, ItemStack itemStack, BlockHitResult blockHitResult) {
            super(player, interactionHand, itemStack, blockHitResult);
            this.replaceClicked = true;
        }
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state) {
        return SoundEvents.AMETHYST_CLUSTER_HIT;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (SnowySpirit.SUPPLEMENTARIES_INSTALLED && SuppCompat.placeOnRope(context, this.color())) {
            Player player = context.getPlayer();
            if (player != null) context.getItemInHand().consume(1, player);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public @Nullable BlockPlaceContext updatePlacementContext(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos targetPos = pos.relative(face.getOpposite());
        BlockState targetState = level.getBlockState(targetPos);
        if (GlowLightsBlockTile.isValidBlock(targetState, targetPos, level)) {
            BlockHitResult hit = new BlockHitResult(context.getClickLocation(), face, targetPos, true);
            return new SelfPlacementContext(context.getPlayer(), context.getHand(), context.getItemInHand(),hit);
        }
        //anything else falls through to the wall variant
        return context;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        if (context instanceof SelfPlacementContext) return super.getPlacementState(context);
        BlockState state = this.wallBlock.getStateForPlacement(context);
        return state != null && this.canPlace(context, state) ? state : null;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (!(context instanceof SelfPlacementContext)) return super.placeBlock(context, state);
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState oldState = level.getBlockState(pos);
        boolean b = super.placeBlock(context, state);
        if (level.getBlockEntity(pos) instanceof GlowLightsBlockTile tile) {
            tile.acceptBlock(oldState);
        }
        return b;
    }


}

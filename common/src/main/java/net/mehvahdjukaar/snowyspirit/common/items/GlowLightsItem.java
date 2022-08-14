package net.mehvahdjukaar.snowyspirit.common.items;

import net.mehvahdjukaar.snowyspirit.common.block.GlowLightsBlockTile;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GlowLightsItem extends Item {

    private final Block block;

    public GlowLightsItem(Block pBlock) {
        super(new Item.Properties().tab(ModRegistry.getTab(CreativeModeTab.TAB_DECORATIONS,"glow_lights")));
        this.block = pBlock;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.getAbilities().mayBuild) {
            Level world = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState clicked = world.getBlockState(pos);
            if (GlowLightsBlockTile.isValidBlock(clicked, pos, world)) {
                BlockState frame = this.block.getStateForPlacement(new BlockPlaceContext(context));
                if(frame != null) {
                    world.setBlockAndUpdate(pos, frame);

                    if (world.getBlockEntity(pos) instanceof GlowLightsBlockTile tile) {

                        SoundEvent sound = SoundEvents.AMETHYST_CLUSTER_HIT;
                        tile.acceptBlock(clicked);
                        world.playSound(player, pos, sound, SoundSource.BLOCKS, (0.8f + 1.0F) / 2.0F,1 * 1.3F);
                        if (!player.isCreative() && !world.isClientSide()) {
                            context.getItemInHand().shrink(1);
                        }
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }
        return super.useOn(context);
    }

}

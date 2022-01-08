package net.mehvahdjukaar.snowyspirit.common.items;

import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.IWreathProvider;
import net.mehvahdjukaar.snowyspirit.common.network.ClientBoundSyncWreath;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.network.NetworkDirection;

public class WreathItem extends BlockItem {
    public WreathItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {

            Direction dir = state.getValue(DoorBlock.FACING);
            boolean open = state.getValue(DoorBlock.OPEN);
            IWreathProvider capability = level.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
            if (capability != null) {
                capability.updateWeathBlock(pos, true, dir, open);

                if (false && pContext.getPlayer() instanceof ServerPlayer player) {

                    NetworkHandler.INSTANCE.sendTo(new ClientBoundSyncWreath(pos, true,
                                    dir, open),
                            player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
                    if (!player.gameMode.isCreative()) {
                        pContext.getItemInHand().shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.useOn(pContext);
    }
}

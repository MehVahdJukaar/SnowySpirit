package net.mehvahdjukaar.snowyspirit.mixins;

import net.mehvahdjukaar.snowyspirit.common.wreath.WreathHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class DoorBlockMixin {

    @Inject(method = "onRemove", at = @At("RETURN"))
    private void snowySpirit$removeWreaths(BlockState oldState,
                                Level world,
                                BlockPos pos,
                                BlockState newState,
                                boolean isMoving,
                                CallbackInfo ci) {
        if (WreathHandler.canHoldWreath(oldState) &&
                !WreathHandler.canHoldWreath(newState) &&
                (world instanceof ServerLevel sl)) {
            WreathHandler.removeWreathAt(pos, sl);
        }
    }
}

package net.mehvahdjukaar.snowyspirit.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.mehvahdjukaar.snowyspirit.common.entity.GolemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarvedPumpkinBlock.class)
public abstract class CarvedPumpkinBlockMixin {

    @Inject(method = "onPlace", at = @At("TAIL"))
    private void snowySpirit$spawnGingy(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving, CallbackInfo ci) {
        if (!oldState.is(state.getBlock())) {
            GolemHelper.trySpawningGingy(state, level, pos, null);
        }
    }

    @ModifyReturnValue(method = "canSpawnGolem", at = @At("RETURN"))
    private boolean snowySpirit$allowDispensingOnGingy(boolean original, LevelReader level, BlockPos pos) {
        return original || GolemHelper.canSpawnGingy(level, pos);
    }


}

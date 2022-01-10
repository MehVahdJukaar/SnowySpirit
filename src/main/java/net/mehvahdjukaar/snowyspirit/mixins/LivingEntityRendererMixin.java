package net.mehvahdjukaar.snowyspirit.mixins;

import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Redirect(method ="render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/world/entity/LivingEntity.getVehicle ()Lnet/minecraft/world/entity/Entity;",
                    ordinal = 0))
    private Entity shouldRiderSit(LivingEntity instance) {
        Entity vehicle = instance.getVehicle();
        if(vehicle instanceof SledEntity sledEntity){
            if(sledEntity.isTowingEntity(instance)) return null;
        }
        return vehicle;
    }
}

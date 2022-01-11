package net.mehvahdjukaar.snowyspirit.mixins;

import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

    @Redirect(method ="render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/world/entity/LivingEntity.getVehicle ()Lnet/minecraft/world/entity/Entity;",
                    ordinal = 0))
    private Entity shouldRiderSit(LivingEntity instance) {
        Entity vehicle = instance.getVehicle();
        if(vehicle instanceof SledEntity sledEntity){
            if(sledEntity.isWolfEntity(instance)) return null;
        }
        return vehicle;
    }
}

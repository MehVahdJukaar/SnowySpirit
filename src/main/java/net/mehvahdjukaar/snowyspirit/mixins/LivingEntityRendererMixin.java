package net.mehvahdjukaar.snowyspirit.mixins;

import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    @Shadow
    protected M model;

    protected LivingEntityRendererMixin(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    //animations for sled pullers
    //injects in all calls so it works with optishit code
    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/world/entity/LivingEntity.isPassenger ()Z"))
    private boolean isPassenger(LivingEntity instance) {
        Entity vehicle = instance.getVehicle();
        if (vehicle instanceof SledEntity sledEntity) {
            if (sledEntity.isMyWolfEntity(instance)) return false;
        }
        return instance.isPassenger();
    }


}

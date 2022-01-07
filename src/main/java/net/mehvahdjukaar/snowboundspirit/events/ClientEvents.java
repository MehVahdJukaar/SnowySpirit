package net.mehvahdjukaar.snowboundspirit.events;

import net.mehvahdjukaar.snowboundspirit.Christmas;
import net.mehvahdjukaar.snowboundspirit.common.IInputListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Christmas.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    /*
    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void renderEngravedBlocksOverlay(RenderLevelLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.player.level;
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        IWreathProvider capability = level.getCapability(CapabilityHandler.WREATH_CAPABILITY, null).orElse(null);

        if (capability != null) {
            float dist = mc.gameRenderer.getRenderDistance();
            dist *= dist;

            for (BlockPos pos : capability.getWreathBlocks()) {

                if (mc.player.distanceToSqr(Vec3.atCenterOf(pos)) < dist) {

                    poseStack.pushPose();
                    RenderSystem.enableDepthTest();
                    poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
                    poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

                    //render stuff

                    RenderSystem.disableDepthTest();
                    poseStack.popPose();
                }
            }
        }
    }
*/

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Entity riddenEntity = mc.player.getVehicle();
            Input movementInput = event.getInput();
            if (riddenEntity instanceof IInputListener listener) {
                listener.onInputUpdate(movementInput.left, movementInput.right,
                        movementInput.up, movementInput.down,
                        mc.options.keySprint.isDown(), movementInput.jumping);
            }
        }
    }
}
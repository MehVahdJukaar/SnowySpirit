package net.mehvahdjukaar.snowyspirit.wreath_stuff;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.ModCapabilities;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.WreathCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SnowySpirit.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientLevel level = Minecraft.getInstance().level;
            //Minecraft.getInstance().setScreen(new ModConfigSelectScreen(Minecraft.getInstance().screen));
            if (level != null) {
                level.getCapability(ModCapabilities.WREATH_CAPABILITY).ifPresent(c -> c.refreshClientBlocksVisuals(level));
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void renderWreaths(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            Minecraft mc = Minecraft.getInstance();
            Level level = mc.player.level;
            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
            var capability = ModCapabilities.get(level, ModCapabilities.WREATH_CAPABILITY);
            if (capability != null) {
                float dist = mc.gameRenderer.getRenderDistance();
                dist *= dist;

                poseStack.pushPose();

                MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
                RenderSystem.enableDepthTest();

                for (var entry : capability.getWreathBlocks().entrySet()) {
                    BlockPos pos = entry.getKey();

                    if (mc.player.distanceToSqr(Vec3.atCenterOf(pos)) < dist) {

                        poseStack.pushPose();
                        poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());

                        WreathCapability.WreathData data = entry.getValue();
                        Direction dir = data.getDirection();

                        BlockState state = ModRegistry.WREATH.get().defaultBlockState();
                        BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
                        poseStack.translate(0.5, 0.5, 0.5);

                        var dim = data.getDimensions();

                        if (dim != null) {
                            poseStack.pushPose();
                            poseStack.mulPose(Vector3f.YP.rotationDegrees(-dir.toYRot()));
                            poseStack.translate(-0.5, -0.5, -0.5 + dim.getSecond());
                            RenderUtil.renderBlock(0, poseStack, bufferSource, state, level, pos, blockRenderer);
                            poseStack.popPose();

                            poseStack.pushPose();
                            poseStack.mulPose(Vector3f.YP.rotationDegrees(-dir.getOpposite().toYRot()));
                            poseStack.translate(-0.5, -0.5, -0.5 + dim.getFirst());
                            RenderUtil.renderBlock(0, poseStack, bufferSource, state, level, pos, blockRenderer);
                            poseStack.popPose();
                        }

                        //render stuff
                        poseStack.popPose();
                    }
                }
                RenderSystem.disableDepthTest();
                bufferSource.endBatch();
                poseStack.popPose();
            }
        }
    }


}
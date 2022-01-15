package net.mehvahdjukaar.snowyspirit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.IInputListener;
import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.IWreathProvider;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.WreathProvider;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Christmas.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                level.getCapability(CapabilityHandler.WREATH_CAPABILITY).ifPresent(c -> c.updateAllBlocksClient(level));
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @SubscribeEvent
    public static void renderWreaths(RenderLevelLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.player.level;
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        IWreathProvider capability = level.getCapability(CapabilityHandler.WREATH_CAPABILITY, null).orElse(null);

        if (capability != null) {
            float dist = mc.gameRenderer.getRenderDistance();
            dist *= dist;

            poseStack.pushPose();


            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            RenderSystem.enableDepthTest();
            poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

            for (var entry : capability.getWreathBlocks().entrySet()) {
                BlockPos pos = entry.getKey();

                if (mc.player.distanceToSqr(Vec3.atCenterOf(pos)) < dist) {

                    poseStack.pushPose();
                    poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

                    //int pPackedLight = getLight(pos, level);
                    WreathProvider.WreathData data = entry.getValue();
                    Direction dir = data.face();
                    boolean open = data.open();
                    if (open) {
                        dir = data.hinge() ? dir.getCounterClockWise() : dir.getClockWise();
                    }
                    BlockState state = ModRegistry.WREATH.get().defaultBlockState();
                    BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.pushPose();
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(-dir.toYRot()));
                    poseStack.translate(-0.5, -0.5, -1.5);
                    renderBlockState(state, poseStack, bufferSource, blockRenderer, level, pos);

                    poseStack.popPose();
                    poseStack.pushPose();
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(-dir.getOpposite().toYRot()));
                    poseStack.translate(-0.5, -0.5, -0.5 - 0.1875);
                    renderBlockState(state, poseStack, bufferSource, blockRenderer, level, pos);

                    poseStack.popPose();
                    //render stuff
                    poseStack.popPose();

                }
            }
            RenderSystem.disableDepthTest();
            bufferSource.endBatch();
            poseStack.popPose();
        }
    }

    public static void renderBlockState(BlockState state, PoseStack matrixStack, MultiBufferSource buffer,
                                        BlockRenderDispatcher blockRenderer, Level world, BlockPos pos) {

        ForgeHooksClient.setRenderType(RenderType.cutout());
        blockRenderer.getModelRenderer().tesselateBlock(world,
                blockRenderer.getBlockModel(state), state, pos, matrixStack,
                buffer.getBuffer(RenderType.cutout()), false, new Random(), 0,
                OverlayTexture.NO_OVERLAY);
        ForgeHooksClient.setRenderType(null);
    }

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
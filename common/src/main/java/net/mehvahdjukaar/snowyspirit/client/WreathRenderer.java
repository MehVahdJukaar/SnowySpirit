package net.mehvahdjukaar.snowyspirit.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.mehvahdjukaar.moonlight.api.client.util.RenderUtil;
import net.mehvahdjukaar.snowyspirit.common.wreath.ChunksWithWreaths;
import net.mehvahdjukaar.snowyspirit.common.block.WreathBlock;
import net.mehvahdjukaar.snowyspirit.common.wreath.WreathData;
import net.mehvahdjukaar.snowyspirit.common.wreath.WreathHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class WreathRenderer {

    //mega slow
    public static void renderAllWreaths(PoseStack poseStack) {
        Minecraft mc = Minecraft.getInstance();
        Level level = mc.player.level();

        ChunksWithWreaths chunksWithWreaths = ModRegistry.WREATH_WORLD_DATA.getData(level);
        if (chunksWithWreaths == null || chunksWithWreaths.isEmpty()) return;
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        poseStack.pushPose();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        RenderSystem.enableDepthTest();


        Iterator<ChunkAccess> loadedChunksInRange = chunksWithWreaths.getLoadedChunksInRange(level, cameraPos, 64).iterator();
        while (loadedChunksInRange.hasNext()) {
            ChunkAccess chunk = loadedChunksInRange.next();
            WreathData wreathData = ModRegistry.WREATH_CHUNK_DATA.getOrNull(chunk);
            if (wreathData == null) continue;
            BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();

            for (BlockPos pos : wreathData.getWreathBlocks()) {


                poseStack.pushPose();
                poseStack.translate(pos.getX() - cameraPos.x(), pos.getY() - cameraPos.y(), pos.getZ() - cameraPos.z());


                BlockState doorState = level.getBlockState(pos);
                if (WreathHandler.canHoldWreath(doorState)) {
                    renderOnBothSides(level, pos, doorState, poseStack, w ->
                            RenderUtil.renderBlock(0, poseStack, bufferSource, w, level, pos, blockRenderer));
                }

                //render stuff
                poseStack.popPose();
            }
        }
        RenderSystem.disableDepthTest();
        bufferSource.endBatch();
        poseStack.popPose();
    }

    public static boolean hasWreathsInChunk(ChunkAccess chunk, BlockPos origin) {
        int minY = origin.getY();
        int maxY = minY + 16;
        WreathData d = ModRegistry.WREATH_CHUNK_DATA.getOrNull(chunk);
        return d != null && d.hasWreathsInSection(minY, maxY);
    }


    public static void renderWreathsInChunk(ChunkAccess chunk, BlockPos origin, PoseStack poseStack, BlockAndTintGetter level,
                                            Function<RenderType, VertexConsumer> getOrCreateChunkBuffer) {
        int minY = origin.getY();
        int maxY = minY + 16;
        WreathData data = ModRegistry.WREATH_CHUNK_DATA.getOrNull(chunk);
        if (data == null) return;
        RandomSource random = RandomSource.create();
        VertexConsumer vc = null;
        BlockState wreath = ModRegistry.WREATH.get().defaultBlockState();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        for (var pos : data.getWreathBlocks()) {
            if (pos.getY() >= minY && pos.getY() < maxY) {
                if (vc == null) {
                    vc = getOrCreateChunkBuffer.apply(RenderType.cutout());
                }
                BlockState doorState = level.getBlockState(pos);

                BlockPos relativePos = pos.subtract(origin);
                poseStack.pushPose();
                poseStack.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());
                if (WreathHandler.canHoldWreath(doorState)) {
                    VertexConsumer buffer = vc;
                    renderOnBothSides(level, pos, doorState, poseStack, w ->
                            blockRenderer.getModelRenderer().tesselateBlock(level, blockRenderer.getBlockModel(w), w,
                                    pos, poseStack, buffer, false, random,
                                    42, OverlayTexture.NO_OVERLAY));
                } else {
                    blockRenderer.getModelRenderer().tesselateBlock(level, blockRenderer.getBlockModel(wreath), wreath,
                            pos, poseStack, vc, false, random,
                            42, OverlayTexture.NO_OVERLAY);
                }


                poseStack.popPose();
            }

        }
    }


    //pose stack has to be at the door block corner
    private static void renderOnBothSides(BlockGetter level, BlockPos pos, BlockState doorState, PoseStack poseStack,
                                          Consumer<BlockState> renderWreath) {
        VoxelShape shape = doorState.getShape(level, pos);
        if (shape.isEmpty()) return;
        AABB bounds = shape.bounds();

        Direction.Axis panelAxis = Direction.Axis.Y;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (bounds.max(axis) - bounds.min(axis) < bounds.max(panelAxis) - bounds.min(panelAxis)) panelAxis = axis;
        }
        BlockState wreath = ModRegistry.WREATH.get().defaultBlockState();

        for (Direction.AxisDirection side : Direction.AxisDirection.values()) {
            Direction face = Direction.fromAxisAndDirection(panelAxis, side);
            boolean positive = side == Direction.AxisDirection.POSITIVE;
            Vec3 offset = Vec3.ZERO.with(panelAxis, positive ? bounds.max(panelAxis) : bounds.min(panelAxis) - 1);

            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            if (panelAxis.isHorizontal()) {
                renderWreath.accept(wreath.setValue(WreathBlock.FACING, face));
            } else {
                //closed trapdoor. lay it flat with the top towards the hinge
                Direction hinge = doorState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING)
                        .orElse(Direction.NORTH).getOpposite();
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(positive ? 180 - hinge.toYRot() : -hinge.toYRot()));
                poseStack.mulPose(Axis.XP.rotationDegrees(positive ? -90 : 90));

                poseStack.translate(-0.5, -0.5, -0.5);
                renderWreath.accept(wreath.setValue(WreathBlock.FACING, Direction.SOUTH));
            }
            poseStack.popPose();
        }
    }


}


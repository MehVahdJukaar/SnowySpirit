package net.mehvahdjukaar.snowyspirit.mixins;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(IglooPieces.IglooPiece.class)
public abstract class IglooPiecesMixin extends TemplateStructurePiece {


    @Shadow
    private static StructurePlaceSettings makeSettings(Rotation pRotation, ResourceLocation pLocation) {
        return null;
    }

    public IglooPiecesMixin(StructurePieceType pType, int pGenDepth, StructureManager pStructureManager, ResourceLocation pLocation, String pTemplateName, StructurePlaceSettings pPlaceSettings, BlockPos pTemplatePosition) {
        super(pType, pGenDepth, pStructureManager, pLocation, pTemplateName, pPlaceSettings, pTemplatePosition);
    }

    @Inject(method = "postProcess", at = @At("RETURN"))
    private void getCollisionShape(WorldGenLevel worldGenLevel, StructureFeatureManager pStructureFeatureManager,
                                   ChunkGenerator pChunkGenerator, Random pRandom, BoundingBox pBox,
                                   ChunkPos pChunkPos, BlockPos pPos, CallbackInfo ci) {

        if (pRandom.nextFloat() > 0.3) {
            ResourceLocation resourcelocation = new ResourceLocation(this.templateName);
            if (resourcelocation.equals(new ResourceLocation("igloo/top"))) {


                StructurePlaceSettings structureplacesettings = makeSettings(this.placeSettings.getRotation(), resourcelocation);
                BlockPos p = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(structureplacesettings,
                        new BlockPos(6, 0, 0)));
                int y = worldGenLevel.getHeight(Heightmap.Types.WORLD_SURFACE_WG, p.getX(), p.getZ());
                BlockPos blockPos = new BlockPos(p.getX(), y, p.getZ());
                if (pBox.isInside(blockPos)) {
                    Level level = worldGenLevel.getLevel();
                    SledEntity sledEntity = new SledEntity(level, blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ());
                    ContainerHolderEntity c = sledEntity.tryAddingChest(Items.CHEST.getDefaultInstance());
                    if (c != null)
                        c.setLootTable(Christmas.res("chests/igloo_sled"), level.getRandom().nextLong());
                    level.addFreshEntity(sledEntity);
                }
            }
        }
    }
}

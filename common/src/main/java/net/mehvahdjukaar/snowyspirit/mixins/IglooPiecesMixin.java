package net.mehvahdjukaar.snowyspirit.mixins;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.configs.RegistryConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.IglooPieces;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(IglooPieces.IglooPiece.class)
public abstract class IglooPiecesMixin extends TemplateStructurePiece {


    public IglooPiecesMixin(StructurePieceType p_226886_, int p_226887_, StructureTemplateManager p_226888_, ResourceLocation p_226889_, String p_226890_, StructurePlaceSettings p_226891_, BlockPos p_226892_) {
        super(p_226886_, p_226887_, p_226888_, p_226889_, p_226890_, p_226891_, p_226892_);
    }

    public IglooPiecesMixin(StructurePieceType p_226894_, CompoundTag p_226895_, StructureTemplateManager p_226896_, Function<ResourceLocation, StructurePlaceSettings> p_226897_) {
        super(p_226894_, p_226895_, p_226896_, p_226897_);
    }

    @Shadow
    private static StructurePlaceSettings makeSettings(Rotation pRotation, ResourceLocation pLocation) {
        return null;
    }



    @Inject(method = "postProcess", at = @At("RETURN"))
    private void getCollisionShape(WorldGenLevel worldGenLevel, StructureManager p_227569_,
                                   ChunkGenerator p_227570_, RandomSource pRandom, BoundingBox pBox,
                                   ChunkPos p_227573_, BlockPos p_227574_, CallbackInfo ci){

        if (pRandom.nextFloat() > 0.3 && RegistryConfigs.SLEDS.get()) {

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
                        c.setLootTable(SnowySpirit.res("chests/igloo_sled"), level.getRandom().nextLong());
                    level.addFreshEntity(sledEntity);
                }
            }
        }
    }
}

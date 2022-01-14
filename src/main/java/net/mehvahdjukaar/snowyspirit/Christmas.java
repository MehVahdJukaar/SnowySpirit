package net.mehvahdjukaar.snowyspirit;


import net.mehvahdjukaar.selene.Selene;
import net.mehvahdjukaar.snowyspirit.common.generation.WorldGenHandler;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.mehvahdjukaar.snowyspirit.init.ModSetup;
import net.minecraft.core.BlockPos;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.IglooFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Random;

/**
 * Author: MehVahdJukaar
 */
@Mod(Christmas.MOD_ID)
public class Christmas {
    public static final String MOD_ID = "snowyspirit";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    public Christmas() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRegistry.init(bus);
        bus.addListener(ModSetup::init);
        MinecraftForge.EVENT_BUS.register(this);
        WorldGenHandler.init();

    }


    public static boolean isChristmasTime() {
        return true;
    }
}

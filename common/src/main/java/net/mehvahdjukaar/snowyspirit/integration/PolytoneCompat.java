package net.mehvahdjukaar.snowyspirit.integration;

import net.mehvahdjukaar.polytone.api.ResolvedPointLight;
import net.mehvahdjukaar.polytone.api.VoxelVolumeApi;
import net.mehvahdjukaar.snowyspirit.dynamicpack.ClientDynamicResourcesHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

public class PolytoneCompat {

    public static void setupClient() {
        VoxelVolumeApi.registerCallback(VoxelVolumeApi.Activation.ONLY_WITH_COLORED_LIGHT_PACKS, event -> {
            addGlowLights(event, ModRegistry.GLOW_LIGHTS_BLOCKS);
            addGlowLights(event, ModRegistry.GLOW_LIGHTS_WALL_BLOCKS);
            addGlowLights(event, ModRegistry.ROPED_GLOW_LIGHTS);
        });
    }

    private static void addGlowLights(VoxelVolumeApi.RegisterEvent event, Map<DyeColor, Supplier<Block>> blocks) {
        blocks.forEach((color, block) -> {
            //no prismatic. tho we could do white
            if (color == null) return;
            event.addBlockLight(block.get(), s -> new ResolvedPointLight(
                    ClientDynamicResourcesHandler.getGlowLightRgb(color), s.getLightEmission()));
        });
    }
}

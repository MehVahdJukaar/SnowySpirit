package net.mehvahdjukaar.snowyspirit.client.block_model;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class GlowLightsModelLoader implements IModelLoader<GlowLightsModelGeometry> {

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
    }

    @Override
    public GlowLightsModelGeometry read(JsonDeserializationContext context, JsonObject json) {
        BlockModel model = ModelLoaderRegistry.ExpandedBlockModelDeserializer.INSTANCE
                .getAdapter(BlockModel.class).fromJsonTree(json.get("overlay"));
        boolean translucent = json.has("translucent") && json.get("translucent").getAsBoolean();
        return new GlowLightsModelGeometry(model, translucent);
    }
}

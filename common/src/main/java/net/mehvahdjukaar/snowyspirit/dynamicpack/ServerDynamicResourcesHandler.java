package net.mehvahdjukaar.snowyspirit.dynamicpack;

import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicServerResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ServerDynamicResourcesHandler extends DynamicServerResourceProvider {

    public static final ServerDynamicResourcesHandler INSTANCE = new ServerDynamicResourcesHandler();

    public ServerDynamicResourcesHandler() {
        super(SnowySpirit.res("generated_pack"), PlatHelper.isDev() ?
                PackGenerationStrategy.REGEN_ON_EVERY_RELOAD :  PackGenerationStrategy.CACHED);
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of(SnowySpirit.MOD_ID);
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> consumer) {
        consumer.accept(this::regenerateDynamicAssets);
    }

    private void regenerateDynamicAssets(ResourceManager resourceManager, ResourceSink sink) {

        SimpleTagBuilder builder = SimpleTagBuilder.of(SnowySpirit.res("sleds"));
        builder.addEntries(ModRegistry.SLED_ITEMS.values());
        sink.addTag(builder, Registries.ITEM);

        ResourceLocation templateId = SnowySpirit.res("sled_oak");
        StaticResource oakUnlockAdvancement = StaticResource.getOrThrow(resourceManager,
                ResType.ADVANCEMENTS.getPath(SnowySpirit.res("recipe/sled_oak")));

        ModRegistry.SLED_ITEMS.forEach((w, b) -> {
            if (w != VanillaWoodTypes.OAK) {
                //not addRecipe: this one carries over the template load conditions, so the generated
                //sleds stay gated behind the sleds config just like the oak one
                sink.addBlockTypeSwapRecipe(resourceManager, templateId, VanillaWoodTypes.OAK, w,
                        SnowySpirit.res("sled" + "_" + w.getTypeName()));
                sink.addSimilarJsonResource(resourceManager, oakUnlockAdvancement, "sled_oak", "sled_" + w.getTypeName());
            }
        });
    }

}

package net.mehvahdjukaar.snowyspirit.dynamicpack;

import com.google.common.base.Preconditions;
import net.mehvahdjukaar.moonlight.api.platform.PlatformHelper;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynServerResourcesProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.moonlight.block_set.wood.WoodType;
import net.mehvahdjukaar.moonlight.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.resources.pack.DynServerResourcesProvider;
import net.mehvahdjukaar.moonlight.resources.pack.DynamicDataPack;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.items.SledItem;
import net.mehvahdjukaar.snowyspirit.configs.RegistryConfigs;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class ServerDynamicResourcesHandler extends DynServerResourcesProvider {

    public ServerDynamicResourcesHandler() {
        super(new DynamicDataPack(SnowySpirit.res("generated_pack")));
        this.dynamicPack.generateDebugResources = PlatformHelper.isDev() || RegistryConfigs.DEBUG_RESOURCES.get();
    }

    @Override
    public Logger getLogger() {
        return SnowySpirit.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return RegistryConfigs.PACK_DEPENDANT_ASSETS.get();
    }

    @Override
    public void regenerateDynamicAssets(ResourceManager resourceManager) {
    }

    @Override
    public void generateStaticAssetsOnStartup(ResourceManager manager) {
        SimpleTagBuilder builder = SimpleTagBuilder.of(SnowySpirit.res("sleds"));
        ModRegistry.SLED_ITEMS.forEach((wood,sled)->{
            builder.addEntry(sled);
            makeSledRecipe(sled, dynamicPack::addRecipe);
        });
        dynamicPack.addTag(builder, Registry.ITEM_REGISTRY);
    }

    public static void makeConditionalWoodRec(FinishedRecipe r, WoodType wood, Consumer<FinishedRecipe> consumer, String name) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(wood.getNamespace()))
                .addRecipe(r)
                .generateAdvancement()
                .build(consumer, SnowySpirit.MOD_ID, name + "_" + wood.getAppendableId());
    }

    private static void makeSledRecipe(SledItem sled, Consumer<FinishedRecipe> consumer) {
        try {
            WoodType wood = sled.getWoodType();
            Item plank = wood.planks.asItem();
            Preconditions.checkArgument(plank != Items.AIR);

            ShapedRecipeBuilder.shaped(sled, 1)
                    .pattern("221")
                    .pattern("111")
                    .define('1', Items.STICK)
                    .define('2', plank)
                    .group("sled")
                    .unlockedBy("has_plank", InventoryChangeTrigger.TriggerInstance.hasItems(plank))
                    //.build(consumer);
                    .save((s) -> makeConditionalWoodRec(s, wood, consumer, "sled")); //

        } catch (Exception ignored) {
            SnowySpirit.LOGGER.error("Failed to generate recipe for item {}", sled);
        }
    }

}

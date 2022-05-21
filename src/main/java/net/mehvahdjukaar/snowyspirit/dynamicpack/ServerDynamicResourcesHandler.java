package net.mehvahdjukaar.snowyspirit.dynamicpack;

import com.google.common.base.Preconditions;
import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.mehvahdjukaar.selene.resourcepack.DynamicDataPack;
import net.mehvahdjukaar.selene.resourcepack.RPAwareDynamicDataProvider;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.items.SledItem;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServerDynamicResourcesHandler extends RPAwareDynamicDataProvider {

    public ServerDynamicResourcesHandler() {
        super(new DynamicDataPack(Christmas.res("virtual_resourcepack")));
        this.dynamicPack.generateDebugResources = false;
    }

    @Override
    public Logger getLogger() {
        return Christmas.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return false;
    }

    @Override
    public void regenerateDynamicAssets(ResourceManager resourceManager) {
    }

    @Override
    public void generateStaticAssetsOnStartup(ResourceManager manager) {
        List<ResourceLocation> posts = new ArrayList<>();

        //recipes
        ModRegistry.SLED_ITEMS.forEach((wood,sled)->{
            posts.add(sled.getRegistryName());

            makeSledRecipe(sled, dynamicPack::addRecipe);
        });
        //tag
        dynamicPack.addTag(Christmas.res("sleds"), posts, Registry.ITEM_REGISTRY);
    }

    public static void makeConditionalWoodRec(FinishedRecipe r, WoodType wood, Consumer<FinishedRecipe> consumer, String name) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition(wood.getNamespace()))
                .addRecipe(r)
                .generateAdvancement()
                .build(consumer, Christmas.MOD_ID, name + "_" + wood.getAppendableId());
    }

    private static void makeSledRecipe(SledItem sled, Consumer<FinishedRecipe> consumer) {
        try {
            WoodType wood = sled.woodType;
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
            Christmas.LOGGER.error("Failed to generate recipe for item {}", sled);
        }
    }

}

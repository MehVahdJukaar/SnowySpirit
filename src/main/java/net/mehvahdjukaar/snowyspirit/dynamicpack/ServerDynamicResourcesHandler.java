package net.mehvahdjukaar.snowyspirit.dynamicpack;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.mehvahdjukaar.selene.resourcepack.DynamicDataPack;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.items.SledItem;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ServerDynamicResourcesHandler {

    public static final DynamicDataPack DYNAMIC_DATA_PACK =
            new DynamicDataPack(Christmas.res("virtual_resourcepack"));

    //fired on mod setup
    public static void registerBus(IEventBus forgeBus) {
        DYNAMIC_DATA_PACK.registerPack(forgeBus);
        FMLJavaModLoadingContext.get().getModEventBus()
                .addListener(ServerDynamicResourcesHandler::generateAssets);
        DYNAMIC_DATA_PACK.generateDebugResources = false;
    }

    public static void generateAssets(final FMLCommonSetupEvent event) {

        Stopwatch watch = Stopwatch.createStarted();

        //sleds
        {
            List<ResourceLocation> posts = new ArrayList<>();

            //recipes
            for (var sled : ModRegistry.SLED_ITEMS.values()) {
                posts.add(sled.getRegistryName());

                makeSledRecipe(sled, DYNAMIC_DATA_PACK::addRecipe);
            }
            //tag
            DYNAMIC_DATA_PACK.addTag(Christmas.res("sleds"), posts, Registry.ITEM_REGISTRY);
        }

        Christmas.LOGGER.info("Generated runtime data resources in: {} seconds", watch.elapsed().toSeconds());
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
            Item plank = wood.plankBlock.asItem();
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

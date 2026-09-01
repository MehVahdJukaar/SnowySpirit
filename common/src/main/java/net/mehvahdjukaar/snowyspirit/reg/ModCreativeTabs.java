package net.mehvahdjukaar.snowyspirit.reg;

import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.platform.TabAdderHelper;
import net.mehvahdjukaar.moonlight.api.set.wood.VanillaWoodTypes;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.configs.CommonConfigs;
import net.mehvahdjukaar.snowyspirit.integration.supp.SuppCompat;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public class ModCreativeTabs {

    public static final RegSupplier<CreativeModeTab> MOD_TAB = !CommonConfigs.MOD_TAB.get() ? null :
            RegHelper.registerCreativeModeTab(SnowySpirit.res(SnowySpirit.MOD_ID), builder ->
                    builder.title(Component.translatable("tab.snowyspirit")).icon(
                            () -> ModRegistry.SLED_ITEMS.get(VanillaWoodTypes.OAK).getDefaultInstance()));


    public static void init() {
        RegHelper.addItemsToTabsRegistration(ModCreativeTabs::registerItemsToTabs);
    }

    public static void registerItemsToTabs(RegHelper.ItemToTabEvent e) {
        var adder = new TabAdderHelper(e, CommonConfigs::isEnabled).intoSingleTab(MOD_TAB);

        adder.after(i -> i.getItem().components().get(DataComponents.JUKEBOX_PLAYABLE) != null, CreativeModeTabs.TOOLS_AND_UTILITIES,
                ModRegistry.WINTER_DISC_NAME,
                ModRegistry.WINTER_DISC); // TODO

        adder.before(Items.HONEY_BOTTLE, CreativeModeTabs.FOOD_AND_DRINKS,
                ModRegistry.EGGNOG_NAME,
                ModRegistry.EGGNOG);

        if (SnowySpirit.SUPPLEMENTARIES_INSTALLED) {
            adder.after(SuppCompat::isCandy, CreativeModeTabs.FOOD_AND_DRINKS,
                    ModRegistry.GINGER_NAME,
                    ModRegistry.GINGERBREAD_COOKIE);
            adder.after(SuppCompat::isCandy, CreativeModeTabs.FOOD_AND_DRINKS,
                    ModRegistry.CANDY_CANE_NAME,
                    ModRegistry.CANDY_CANE);

            adder.after(SuppCompat::isGlobe, CreativeModeTabs.FUNCTIONAL_BLOCKS,
                    ModRegistry.SNOW_GLOBE_NAME,
                    ModRegistry.SNOW_GLOBE);
        } else {
            adder.before(Items.ROTTEN_FLESH, CreativeModeTabs.FOOD_AND_DRINKS,
                    ModRegistry.GINGER_NAME,
                    ModRegistry.GINGERBREAD_COOKIE);
            adder.before(Items.ROTTEN_FLESH, CreativeModeTabs.FOOD_AND_DRINKS,
                    ModRegistry.CANDY_CANE_NAME,
                    ModRegistry.CANDY_CANE);

            adder.after(Items.BELL, CreativeModeTabs.FUNCTIONAL_BLOCKS,
                    ModRegistry.SNOW_GLOBE_NAME,
                    ModRegistry.SNOW_GLOBE);
        }
        adder.after(ItemTags.BOATS, CreativeModeTabs.TOOLS_AND_UTILITIES,
                ModRegistry.SLED_NAME,
                ModRegistry.SLED_ITEMS.values().stream()
                        .map(i -> (Supplier<Item>) i::asItem).toArray(Supplier[]::new));
        adder.before(ItemTags.BANNERS, CreativeModeTabs.COLORED_BLOCKS,
                ModRegistry.GLOW_LIGHTS_NAME,
                ModRegistry.GLOW_LIGHTS_ITEMS.values().toArray(Supplier[]::new));

        adder.before(ItemTags.BANNERS, CreativeModeTabs.COLORED_BLOCKS,
                ModRegistry.GUMDROP_NAME,
                ModRegistry.GUMDROPS_BUTTONS.values().toArray(Supplier[]::new));


        adder.before(ItemTags.BANNERS, CreativeModeTabs.FUNCTIONAL_BLOCKS,
                ModRegistry.GLOW_LIGHTS_NAME,
                ModRegistry.GLOW_LIGHTS_ITEMS.values().toArray(Supplier[]::new));

        adder.before(ItemTags.BANNERS, CreativeModeTabs.FUNCTIONAL_BLOCKS,
                ModRegistry.GUMDROP_NAME,
                ModRegistry.GUMDROPS_BUTTONS.values().toArray(Supplier[]::new));

        adder.before(Items.GLOW_BERRIES, CreativeModeTabs.NATURAL_BLOCKS,
                ModRegistry.GINGER_NAME,
                ModRegistry.GINGER_FLOWER);

        adder.before(Items.WHEAT, CreativeModeTabs.INGREDIENTS,
                ModRegistry.GINGER_NAME,
                ModRegistry.GINGER);

        adder.after(Items.SMALL_DRIPLEAF, CreativeModeTabs.NATURAL_BLOCKS,
                ModRegistry.GINGER_NAME,
                ModRegistry.GINGER_WILD);

        adder.add(CreativeModeTabs.BUILDING_BLOCKS,
                ModRegistry.GINGER_NAME,
                ModRegistry.GINGERBREAD_FROSTED_BLOCK,
                ModRegistry.GINGERBREAD_BLOCK,
                ModRegistry.GINGERBREAD_STAIRS,
                ModRegistry.GINGERBREAD_SLAB,
                ModRegistry.GINGERBREAD_DOOR,
                ModRegistry.GINGERBREAD_TRAPDOOR);

        adder.add(CreativeModeTabs.BUILDING_BLOCKS,
                ModRegistry.CANDY_CANE_NAME,
                ModRegistry.CANDY_CANE_BLOCK);

        adder.before(Items.BOOKSHELF, CreativeModeTabs.FUNCTIONAL_BLOCKS,
                ModRegistry.WREATH_NAME,
                ModRegistry.WREATH);

        adder.add(CreativeModeTabs.SPAWN_EGGS,
                ModRegistry.GINGERBREAD_GOLEM_NAME,
                ModRegistry.GINGERBREAD_GOLEM_EGG);
    }


}

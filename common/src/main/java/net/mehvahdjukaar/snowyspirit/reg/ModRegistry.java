package net.mehvahdjukaar.snowyspirit.reg;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.teamabode.cave_enhancements.core.integration.quark.VerticalSlabBlock;
import net.mehvahdjukaar.moonlight.api.block.ModStairBlock;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlocksColorAPI;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.block.*;
import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.common.items.CandyCaneItem;
import net.mehvahdjukaar.snowyspirit.common.items.EggnogItem;
import net.mehvahdjukaar.snowyspirit.common.items.GlowLightsItem;
import net.mehvahdjukaar.snowyspirit.common.items.SledItem;
import net.mehvahdjukaar.snowyspirit.configs.ModConfigs;
import net.minecraft.Util;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class ModRegistry {

    //vanilla carpets color cache
    public static final Supplier<BiMap<DyeColor, Item>> CARPETS = Suppliers.memoize(() -> {
        var m = HashBiMap.<DyeColor, Item>create();
        for (DyeColor c : DyeColor.values()) {
            m.put(c, BlocksColorAPI.getColoredItem("carpet", c));
        }
        return m;
    });

    public static void init() {
        BlockSetAPI.addDynamicItemRegistration(ModRegistry::registerSledItems, WoodType.class);
        BlockSetAPI.addDynamicBlockRegistration(ModRegistry::hack, WoodType.class);
    }

    //TODO: remove and fix in moonlignt
    private static <T extends BlockType> void hack(Registrator<Block> itemRegistrator, Collection<T> ts) {
    }

    private static void registerSledItems(Registrator<Item> event, Collection<WoodType> woodTypes) {
        for (WoodType wood : woodTypes) {
            if (wood.canBurn()) {
                String name = wood.getVariantId("sled");
                SledItem item = new SledItem(wood);
                event.register(SnowySpirit.res(name), item);
                SLED_ITEMS.put(wood, item);
                wood.addChild("sled", (Object) item);
            }
        }
    }

    public static final Supplier<EntityType<SledEntity>> SLED = regEntity("sled",
            () -> EntityType.Builder.<SledEntity>of(SledEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10));

    public static final Supplier<EntityType<ContainerHolderEntity>> CONTAINER_ENTITY = regEntity("container_entity",
            () -> EntityType.Builder.of(ContainerHolderEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f)
                    .clientTrackingRange(8));


    public static final Map<WoodType, SledItem> SLED_ITEMS = new HashMap<>();

    public static final CreativeModeTab MOD_TAB = !ModConfigs.MOD_TAB.get() ? null :
            PlatHelper.createModTab(SnowySpirit.res(SnowySpirit.MOD_ID),
                    () -> SLED_ITEMS.get(WoodTypeRegistry.OAK_TYPE).getDefaultInstance(), false);

    public static final Supplier<Block> CANDY_CANE_BLOCK = regWithItem("candy_cane_block", () ->
                    new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                            .requiresCorrectToolForDrops().strength(1.5F).sound(SoundType.CALCITE)),
            CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final Supplier<Item> CANDY_CANE = regItem("candy_cane",
            () -> new CandyCaneItem(new Item.Properties().tab(getTab(CreativeModeTab.TAB_FOOD, "candy_cane"))
                    .food(new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).build())));

    public static final Supplier<Item> GINGERBREAD_COOKIE = regItem("gingerbread_cookie",
            () -> new Item(new Item.Properties().tab(getTab(CreativeModeTab.TAB_FOOD, "gingerbread_cookie"))
                    .food(new FoodProperties.Builder().nutrition(1).fast().saturationMod(0.4f).build())));

    public static final Supplier<Item> EGGNOG = regItem("eggnog", EggnogItem::new);

    public static final Supplier<Item> WINTER_DISC = regItem("winter_disc",
            () -> PlatHelper.newMusicDisc(14, ModSounds.WINTER_MUSIC, new Item.Properties()
                    .tab(getTab(CreativeModeTab.TAB_MISC, "winter_disc"))
                    .rarity(Rarity.RARE).stacksTo(1), 350));

    public static final Supplier<Block> GINGERBREAD_BLOCK = regWithItem("gingerbread", () ->
            new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_ORANGE)
                    .sound(SoundType.WOOD).strength(1F)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    //slab
    public static final Supplier<Block> GINGERBREAD_STAIRS = regWithItem("gingerbread_stairs", () -> new ModStairBlock(
            GINGERBREAD_BLOCK, BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())
    ), CreativeModeTab.TAB_BUILDING_BLOCKS);
    //TODO: finish
    //slab
    public static final Supplier<Block> GINGERBREAD_SLAB = regWithItem("gingerbread_slab", () -> new SlabBlock(
            BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())
    ), CreativeModeTab.TAB_BUILDING_BLOCKS);

    //vertical slab
    public static final Supplier<Block> GINGERBREAD_VERTICAL_SLAB = regWithItem("gingerbread_vertical_slab", () -> new VerticalSlabBlock(
            BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())
    ), PlatHelper.isModLoaded("quark") ? CreativeModeTab.TAB_BUILDING_BLOCKS : null);

    public static final Supplier<Block> GINGERBREAD_FROSTED_BLOCK = regWithItem("gingerbread_frosted", () ->
            new Block(BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final Supplier<Block> GINGERBREAD_DOOR = regWithItem("gingerbread_door", () ->
            new DoorBlock(BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())) {
            }, CreativeModeTab.TAB_REDSTONE);

    public static final Supplier<Block> GINGERBREAD_TRAPDOOR = regWithItem("gingerbread_trapdoor", () ->
            new TrapDoorBlock(BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())) {
            }, CreativeModeTab.TAB_REDSTONE);


    public static final Supplier<Block> GINGER_WILD = regWithItem("wild_ginger", () -> new WildGingerBlock(
            BlockBehaviour.Properties.copy(Blocks.TALL_GRASS)), CreativeModeTab.TAB_DECORATIONS);

    public static final Supplier<Block> GINGER_CROP = regBlock("ginger", () ->
            new GingerBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final Supplier<Item> GINGER_FLOWER = regItem("ginger_flower",
            () -> new ItemNameBlockItem(GINGER_CROP.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final Supplier<Item> GINGER = regItem("ginger",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));

    //pot
    public static final Supplier<Block> GINGER_POT = regBlock("potted_ginger", () -> PlatHelper.newFlowerPot(
            () -> (FlowerPotBlock) Blocks.FLOWER_POT, GINGER_CROP, BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)));

    public static final Supplier<SimpleParticleType> GLOW_LIGHT_PARTICLE = RegHelper.registerParticle(
            SnowySpirit.res("glow_light"));


    public static final Map<DyeColor, Supplier<Block>> GUMDROPS_BUTTONS =
            Arrays.stream(DyeColor.values()).collect(ImmutableMap.toImmutableMap(Function.identity(),
                    c -> regWithItem("gumdrop_" + c.getName(), () -> new GumdropButton(c),
                            CreativeModeTab.TAB_DECORATIONS)));


    public static final Map<DyeColor, Supplier<Block>> GLOW_LIGHTS_BLOCKS = Util.make(() -> {
        var m = new HashMap<DyeColor, Supplier<Block>>();
        for (DyeColor c : DyeColor.values()) {
            m.put(c, regBlock("glow_lights_" + c.getName(), () -> new GlowLightsBlock(c)));
        }
        m.put(null, regBlock("glow_lights_prismatic", () -> new GlowLightsBlock(null)));
        return m;
    });

    public static final Map<DyeColor, Supplier<Item>> GLOW_LIGHTS_ITEMS = Util.make(() -> {
        var m = new HashMap<DyeColor, Supplier<Item>>();
        for (DyeColor c : DyeColor.values()) {
            m.put(c, regItem("glow_lights_" + c.getName(), () -> new GlowLightsItem(GLOW_LIGHTS_BLOCKS.get(c).get())));
        }
        m.put(null, regItem("glow_lights_prismatic", () -> new GlowLightsItem(GLOW_LIGHTS_BLOCKS.get(null).get())));
        return m;
    });

    public static final Supplier<BlockEntityType<GlowLightsBlockTile>> GLOW_LIGHTS_BLOCK_TILE = regTile(
            "glow_lights", () -> PlatHelper.newBlockEntityType(GlowLightsBlockTile::new,
                    ModRegistry.GLOW_LIGHTS_BLOCKS.values().stream().map(Supplier::get).toArray(Block[]::new)));


    public static final Supplier<Block> WREATH = regWithItem("wreath", () ->
            new WreathBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                    .sound(SoundType.VINE).strength(0.1f).noCollission()), CreativeModeTab.TAB_DECORATIONS);


    public static final Supplier<Block> SNOW_GLOBE = regWithItem("snow_globe", () ->
            new SnowGlobeBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.5f)), CreativeModeTab.TAB_DECORATIONS);


    //gets the tab given or null if the item is disabled
    public static CreativeModeTab getTab(CreativeModeTab g, String regName) {
        if (ModConfigs.isEnabled(regName)) {
            return MOD_TAB == null ? g : MOD_TAB;
        }
        return null;
    }


    public static <T extends Item> Supplier<T> regItem(String name, Supplier<T> sup) {
        return RegHelper.registerItem(SnowySpirit.res(name), sup);
    }

    public static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> regTile(String name, Supplier<T> sup) {
        return RegHelper.registerBlockEntityType(SnowySpirit.res(name), sup);
    }

    public static <T extends Block> Supplier<T> regBlock(String name, Supplier<T> sup) {
        return RegHelper.registerBlock(SnowySpirit.res(name), sup);
    }

    public static <T extends Block> Supplier<T> regWithItem(String name, Supplier<T> blockFactory, CreativeModeTab tab) {
        return regWithItem(name, blockFactory, new Item.Properties().tab(getTab(tab, name)), 0);
    }

    public static <T extends Block> Supplier<T> regWithItem(String name, Supplier<T> blockFactory, Item.Properties properties, int burnTime) {
        Supplier<T> block = regBlock(name, blockFactory);
        regBlockItem(name, block, properties, burnTime);
        return block;
    }

    public static Supplier<BlockItem> regBlockItem(String name, Supplier<? extends Block> blockSup, Item.Properties properties, int burnTime) {
        return RegHelper.registerItem(SnowySpirit.res(name), () -> burnTime == 0 ? new BlockItem(blockSup.get(), properties) :
                new WoodBasedBlockItem(blockSup.get(), properties, burnTime));
    }

    public static <T extends Entity> Supplier<EntityType<T>> regEntity(String name, Supplier<EntityType.Builder<T>> builder) {
        return RegHelper.registerEntityType(SnowySpirit.res(name), () -> builder.get().build(name));
    }


}

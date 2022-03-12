package net.mehvahdjukaar.snowyspirit.init;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.mehvahdjukaar.selene.block_set.BlockSetManager;
import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.block.*;
import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.common.items.EggnogItem;
import net.mehvahdjukaar.snowyspirit.common.items.GlowLightsItem;
import net.mehvahdjukaar.snowyspirit.common.items.SledItem;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ModRegistry {

    public static BiMap<DyeColor, Item> CARPETS = HashBiMap.create();

    static {
        for (DyeColor c : DyeColor.values()) {
            CARPETS.put(c, ForgeRegistries.ITEMS.getValue(new ResourceLocation(c.getName() + "_carpet")));
        }
    }

    public static final TagKey<Item> VALID_CONTAINERS = ItemTags.create(Christmas.res("sled_container"));
    public static final TagKey<EntityType<?>> WOLVES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, Christmas.res("sled_pullers"));
    public static final TagKey<Block> SLED_SNOW = BlockTags.create(Christmas.res("sled_snow"));
    public static final TagKey<Block> SLED_SAND = BlockTags.create(Christmas.res("sled_sand"));


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Christmas.MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Christmas.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Christmas.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Christmas.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Christmas.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Christmas.MOD_ID);
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(ForgeRegistries.ACTIVITIES, Christmas.MOD_ID);
    public static final DeferredRegister<Schedule> SCHEDULES = DeferredRegister.create(ForgeRegistries.SCHEDULES, Christmas.MOD_ID);
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(ForgeRegistries.MEMORY_MODULE_TYPES, Christmas.MOD_ID);
    public static final DeferredRegister<SensorType<?>> POI_SENSORS = DeferredRegister.create(ForgeRegistries.SENSOR_TYPES, Christmas.MOD_ID);

    public static void init(IEventBus bus) {
        SOUNDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
        ACTIVITIES.register(bus);
        SCHEDULES.register(bus);
        MEMORY_MODULE_TYPES.register(bus);
        POI_SENSORS.register(bus);

        BlockSetManager.addBlockSetRegistrationCallback(ModRegistry::registerSledsItems, Item.class, WoodType.class);
    }

    //sign posts
    public static void registerSledsItems(RegistryEvent.Register<Item> event, Collection<WoodType> woodTypes) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (WoodType wood : woodTypes) {
            if (wood.canBurn()) {
                String name = wood.getVariantId("sled");
                Item item = new SledItem(wood).setRegistryName(name);
                registry.register(item);
                SLED_ITEMS.put(wood, (SledItem) item);
            }
        }
    }

    private static RegistryObject<Item> regItem(String name, Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }

    protected static RegistryObject<Item> regBlockItem(RegistryObject<Block> blockSup, CreativeModeTab group) {
        return regItem(blockSup.getId().getPath(), () -> new BlockItem(blockSup.get(), (new Item.Properties()).tab(group)));
    }

    public static RegistryObject<SoundEvent> makeSoundEvent(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(Christmas.res(name)));
    }

    public static final RegistryObject<SoundEvent> WINTER_MUSIC = makeSoundEvent("music.winter");

    public static final RegistryObject<EntityType<SledEntity>> SLED = ENTITIES.register("sled",
            () -> EntityType.Builder.<SledEntity>of(SledEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10)
                    .build("sled"));

    public static final RegistryObject<EntityType<ContainerHolderEntity>> CONTAINER_ENTITY = ENTITIES.register("container_entity",
            () -> EntityType.Builder.of(ContainerHolderEntity::new, MobCategory.MISC)
                    .sized(0.75f, 0.75f)
                    .clientTrackingRange(8)
                    .build("container_entity"));


    public static final Map<WoodType, SledItem> SLED_ITEMS = new HashMap<>();

    public static final RegistryObject<Block> CANDY_CANE_BLOCK = BLOCKS.register("candy_cane_block", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                    .requiresCorrectToolForDrops().strength(1.5F).sound(SoundType.CALCITE)));
    public static final RegistryObject<Item> CANDY_CANE_BLOCK_ITEM = regBlockItem(CANDY_CANE_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Item> CANDY_CANE = regItem("candy_cane",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(new FoodProperties
                    .Builder().nutrition(2).saturationMod(0.4f).build())));

    public static final RegistryObject<Item> GINGERBREAD_COOKIE = regItem("gingerbread_cookie",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD).food(new FoodProperties
                    .Builder().nutrition(1).fast().saturationMod(0.4f).build())));

    public static final RegistryObject<Item> EGGNOG = regItem("eggnog", EggnogItem::new);

    public static final RegistryObject<Item> WINTER_DISC = ITEMS.register("winter_disc",
            () -> new RecordItem(14, WINTER_MUSIC, new Item.Properties()
                    .tab(CreativeModeTab.TAB_MISC).rarity(Rarity.RARE).stacksTo(1)));

    public static final RegistryObject<Block> GINGERBREAD_BLOCK = BLOCKS.register("gingerbread", () ->
            new Block(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.TERRACOTTA_ORANGE)
                    .sound(SoundType.WOOD).strength(1F)));
    public static final RegistryObject<Item> GINGERBREAD_BLOCK_ITEM = regBlockItem(GINGERBREAD_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final RegistryObject<Block> GINGERBREAD_FROSTED_BLOCK = BLOCKS.register("gingerbread_frosted", () ->
            new Block(BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())));
    public static final RegistryObject<Item> GINGERBREAD_FROSTED_BLOCK_ITEM = regBlockItem(GINGERBREAD_FROSTED_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final RegistryObject<Block> GINGERBREAD_DOOR = BLOCKS.register("gingerbread_door", () ->
            new DoorBlock(BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())));
    public static final RegistryObject<Item> GINGERBREAD_DOOR_ITEM = regBlockItem(GINGERBREAD_DOOR, CreativeModeTab.TAB_REDSTONE);

    public static final RegistryObject<Block> GINGERBREAD_TRAPDOOR = BLOCKS.register("gingerbread_trapdoor", () ->
            new TrapDoorBlock(BlockBehaviour.Properties.copy(GINGERBREAD_BLOCK.get())));
    public static final RegistryObject<Item> GINGERBREAD_TRAPDOOR_ITEM = regBlockItem(GINGERBREAD_TRAPDOOR, CreativeModeTab.TAB_REDSTONE);


    public static final RegistryObject<Block> GINGER_WILD = BLOCKS.register("wild_ginger", () -> new WildGingerBlock(
            BlockBehaviour.Properties.copy(Blocks.TALL_GRASS)));
    public static final RegistryObject<Item> GINGER_WILD_ITEM = regBlockItem(GINGER_WILD, CreativeModeTab.TAB_DECORATIONS);


    public static final RegistryObject<Block> GINGER_CROP = BLOCKS.register("ginger", () ->
            new GingerBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final RegistryObject<Item> GINGER_FLOWER = regItem("ginger_flower",
            () -> new ItemNameBlockItem(GINGER_CROP.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> GINGER = regItem("ginger",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));

    //pot
    public static final RegistryObject<Block> GINGER_POT = BLOCKS.register("potted_ginger", () -> new FlowerPotBlock(
            () -> (FlowerPotBlock) Blocks.FLOWER_POT, GINGER_CROP, BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)));


    public static final Map<DyeColor, RegistryObject<Block>> GUMDROPS_BUTTONS = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<Item>> GUMDROPS_BUTTON_ITEMS = new HashMap<>();

    public static final Map<DyeColor, RegistryObject<Block>> GLOW_LIGHTS_BLOCKS = new HashMap<>();
    public static final Map<DyeColor, RegistryObject<Item>> GLOW_LIGHTS_ITEMS = new HashMap<>();

    static {
        for (DyeColor c : DyeColor.values()) {
            GUMDROPS_BUTTONS.put(c, BLOCKS.register("gumdrop_" + c.getName(), () -> new GumdropButton(c)));
            GUMDROPS_BUTTON_ITEMS.put(c, regBlockItem(GUMDROPS_BUTTONS.get(c), CreativeModeTab.TAB_DECORATIONS));
        }
        for (DyeColor c : DyeColor.values()) {
            GLOW_LIGHTS_BLOCKS.put(c, BLOCKS.register("glow_lights_" + c.getName(), () -> new GlowLightsBlock(c)));
            GLOW_LIGHTS_ITEMS.put(c, regItem("glow_lights_" + c.getName(), () -> new GlowLightsItem(GLOW_LIGHTS_BLOCKS.get(c).get())));
        }
        GLOW_LIGHTS_BLOCKS.put(null, BLOCKS.register("glow_lights_prismatic", () -> new GlowLightsBlock(null)));
        GLOW_LIGHTS_ITEMS.put(null, regItem("glow_lights_prismatic", () -> new GlowLightsItem(GLOW_LIGHTS_BLOCKS.get(null).get())));
    }

    public static final RegistryObject<BlockEntityType<GlowLightsBlockTile>> GLOW_LIGHTS_BLOCK_TILE = TILES
            .register("glow_lights", () -> BlockEntityType.Builder.of(GlowLightsBlockTile::new,
                    ModRegistry.GLOW_LIGHTS_BLOCKS.values().stream().map(RegistryObject::get).toArray(Block[]::new)).build(null));


    public static final RegistryObject<Block> WREATH = BLOCKS.register("wreath", () ->
            new WreathBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                    .sound(SoundType.VINE).strength(0.1f).noCollission()));
    public static final RegistryObject<Item> WREATH_ITEM = regBlockItem(WREATH, CreativeModeTab.TAB_DECORATIONS);


    //AI stuff

    public static final RegistryObject<MemoryModuleType<Boolean>> PLACED_PRESENT =
            MEMORY_MODULE_TYPES.register("placed_present", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    public static final RegistryObject<MemoryModuleType<GlobalPos>> WREATH_POS =
            MEMORY_MODULE_TYPES.register("wreath_pos", () -> new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    public static final RegistryObject<Block> SNOW_GLOBE = BLOCKS.register("snow_globe", () ->
            new SnowGlobeBlock(BlockBehaviour.Properties.of(Material.STONE).strength(0.5f)));
    public static final RegistryObject<Item> SNOW_GLOBE_ITEM = regBlockItem(SNOW_GLOBE, CreativeModeTab.TAB_DECORATIONS);

}

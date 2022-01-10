package net.mehvahdjukaar.snowyspirit.init;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.block.GingerBlock;
import net.mehvahdjukaar.snowyspirit.common.block.GumdropButton;
import net.mehvahdjukaar.snowyspirit.common.block.WreathBlock;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.mehvahdjukaar.snowyspirit.common.items.SledItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModRegistry {

    public static BiMap<DyeColor, Item> CARPETS = HashBiMap.create();

    static {
        for (DyeColor c : DyeColor.values()) {
            CARPETS.put(c, ForgeRegistries.ITEMS.getValue(new ResourceLocation(c.getName() + "_carpet")));
        }
    }

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Christmas.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Christmas.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Christmas.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Christmas.MOD_ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Christmas.MOD_ID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);
        PARTICLES.register(bus);
    }

    private static RegistryObject<Item> regItem(String name, Supplier<? extends Item> sup) {
        return ITEMS.register(name, sup);
    }

    protected static RegistryObject<Item> regBlockItem(RegistryObject<Block> blockSup, CreativeModeTab group) {
        return regItem(blockSup.getId().getPath(), () -> new BlockItem(blockSup.get(), (new Item.Properties()).tab(group)));
    }


    public static final RegistryObject<EntityType<SledEntity>> SLED = ENTITIES.register("sled",
            () -> EntityType.Builder.<SledEntity>of(SledEntity::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F)
                    .clientTrackingRange(10)
                    .build("sled"));

    public static final Map<Boat.Type, RegistryObject<Item>> SLED_ITEMS = Stream.of(Boat.Type.values()).collect(ImmutableMap.toImmutableMap((e) -> e,
            (t) -> regItem("sled_" + t.getName(), () -> new SledItem(t))));

    public static final RegistryObject<Block> CANDY_CANE_BLOCK = BLOCKS.register("candy_cane_block", () ->
            new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.COLOR_PINK)
                    .requiresCorrectToolForDrops().strength(1.5F).sound(SoundType.CALCITE)));
    public static final RegistryObject<Item> CANDY_CANE_BLOCK_ITEM = regBlockItem(CANDY_CANE_BLOCK, CreativeModeTab.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Item> CANDY_CANE = regItem("candy_cane",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));

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


    public static final RegistryObject<Block> GINGER_CROP = BLOCKS.register("ginger", () ->
            new GingerBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
    public static final RegistryObject<Item> GINGER_FLOWER = regItem("ginger_flower",
            () -> new ItemNameBlockItem(GINGER_CROP.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> GINGER = regItem("ginger",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_FOOD)));

    public static final Map<DyeColor, RegistryObject<Block>> GUMDROPS_BUTTON = Stream.of(DyeColor.values()).collect(ImmutableMap.toImmutableMap((e) -> e,
            (t) -> BLOCKS.register("gumdrop_" + t.getName(), () -> new GumdropButton(t))));

    public static final Map<DyeColor, RegistryObject<Item>> GUMDROPS_BUTTON_ITEMS = Stream.of(DyeColor.values()).collect(ImmutableMap.toImmutableMap((e) -> e,
            (t) -> regBlockItem(GUMDROPS_BUTTON.get(t), CreativeModeTab.TAB_DECORATIONS)));


    public static final RegistryObject<Block> WREATH = BLOCKS.register("wreath", () ->
            new WreathBlock(BlockBehaviour.Properties.of(Material.PLANT, MaterialColor.COLOR_GREEN)
                    .sound(SoundType.VINE).strength(0.1f).noCollission().noCollission()));
    public static final RegistryObject<Item> WREATH_ITEM = regBlockItem(WREATH, CreativeModeTab.TAB_DECORATIONS);

}

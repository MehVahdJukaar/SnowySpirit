package net.mehvahdjukaar.snowyspirit.reg;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Item> VALID_CONTAINERS = TagKey.create(Registry.ITEM_REGISTRY, SnowySpirit.res("sled_container"));
    public static final TagKey<EntityType<?>> WOLVES = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, SnowySpirit.res("sled_pullers"));
    public static final TagKey<Block> SLED_SNOW = TagKey.create(Registry.BLOCK_REGISTRY,SnowySpirit.res("sled_snow"));
    public static final TagKey<Block> SLED_SAND = TagKey.create(Registry.BLOCK_REGISTRY,SnowySpirit.res("sled_sand"));

}

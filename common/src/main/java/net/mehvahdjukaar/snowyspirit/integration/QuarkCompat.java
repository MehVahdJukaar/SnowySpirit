package net.mehvahdjukaar.snowyspirit.integration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.building.module.VariantChestsModule;

public class QuarkCompat {

    public static Item getSpruceChest() {
        if (!Quark.ZETA.modules.isEnabled(VariantChestsModule.class)) return Items.CHEST;
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse("quark:spruce_chest"));
    }
}

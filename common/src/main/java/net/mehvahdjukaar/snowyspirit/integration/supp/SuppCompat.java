package net.mehvahdjukaar.snowyspirit.integration.supp;

import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.supplementaries.common.inventories.SackContainerMenu;
import net.mehvahdjukaar.supplementaries.common.items.CandyItem;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.mehvahdjukaar.supplementaries.configs.CommonConfigs;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SuppCompat {

    public static boolean isSack(Item i) {
        return i instanceof SackItem;
    }

    public static AbstractContainerMenu createSackMenu(int id, Inventory inventory, ContainerHolderEntity entity) {
        return new SackContainerMenu(id, inventory, entity);
    }

    //TODO: add
    public static void triggerSweetTooth(Level level, LivingEntity entity) {
        CandyItem.increaseSweetTooth(level, entity, 8 * 20);
    }

    public static boolean isCandy(ItemStack stack) {
       return CommonConfigs.Tools.CANDY_ENABLED.get() ? stack.is(ModRegistry.CANDY_ITEM.get()) : stack.is(Items.PUMPKIN_PIE);
    }

    public static boolean isGlobe(ItemStack stack) {
        return CommonConfigs.Building.GLOBE_SEPIA.get() ? stack.is(ModRegistry.GLOBE_SEPIA.get().asItem()) :
                CommonConfigs.Building.GLOBE_ENABLED.get() ? stack.is(ModRegistry.GLOBE.get().asItem()) : stack.is(Items.BELL);

    }
}

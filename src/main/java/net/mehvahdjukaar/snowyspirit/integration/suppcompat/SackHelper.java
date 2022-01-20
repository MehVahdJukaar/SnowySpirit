package net.mehvahdjukaar.snowyspirit.integration.suppcompat;

import net.mehvahdjukaar.snowyspirit.common.entity.ContainerHolderEntity;
import net.mehvahdjukaar.supplementaries.common.configs.ServerConfigs;
import net.mehvahdjukaar.supplementaries.common.inventories.SackContainerMenu;
import net.mehvahdjukaar.supplementaries.common.items.SackItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;

public class SackHelper {

    public static boolean isSack(Item i) {
        return i instanceof SackItem;
    }

    public static int getSlotSize() {
        return ServerConfigs.cached.SACK_SLOTS;
    }

    public static AbstractContainerMenu createMenu(int id, Inventory inventory, ContainerHolderEntity entity) {
        return new SackContainerMenu(id, inventory, entity);
    }
}

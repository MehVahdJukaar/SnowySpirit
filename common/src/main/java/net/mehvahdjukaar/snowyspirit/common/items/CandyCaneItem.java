package net.mehvahdjukaar.snowyspirit.common.items;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.integration.supp.SuppCompat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CandyCaneItem extends Item {
    public CandyCaneItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (SnowySpirit.SUPPLEMENTARIES_INSTALLED) SuppCompat.triggerSweetTooth(level, entity);
        return super.finishUsingItem(stack, level, entity);
    }
}

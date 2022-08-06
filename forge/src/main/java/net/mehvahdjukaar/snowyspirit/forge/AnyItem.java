package net.mehvahdjukaar.randomium.forge;

import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.client.DuplicateItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AnyItem extends Item implements ICustomItemRendererProvider {
    public AnyItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public String getDescriptionId() {
        return Randomium.getAnyItem().getDescriptionId();
    }

    @Nullable
    @Override
    public String getCreatorModId(ItemStack itemStack) {
        ItemStack s = Randomium.getAnyItem();
        return s.getItem().getCreatorModId(s);
    }

    @Override
    public Rarity getRarity(ItemStack p_77613_1_) {
        return Randomium.getAnyItem().getRarity();
    }

    @Override
    public Supplier<ItemStackRenderer> getRendererFactory() {
        return DuplicateItemRenderer::new;
    }
}

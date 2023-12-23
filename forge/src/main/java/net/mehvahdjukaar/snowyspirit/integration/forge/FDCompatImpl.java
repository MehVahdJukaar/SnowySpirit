package net.mehvahdjukaar.snowyspirit.integration.forge;

import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vectorwing.farmersdelight.FarmersDelight;

import java.util.function.Supplier;

import static net.mehvahdjukaar.snowyspirit.reg.ModRegistry.regWithItem;

public class FDCompatImpl {
    public static void init() {
        RegHelper.addItemsToTabsRegistration(FDCompatImpl::addItemsToTabs);
    }

    public static void addItemsToTabs(RegHelper.ItemToTabEvent event){
        //event.add(FarmersDelight.CREATIVE_TAB, GINGER_CRATE.get());
    }

    public static final Supplier<Block> GINGER_CRATE = regWithItem(
            "ginger_crate", () ->
                    new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD))
    );
}

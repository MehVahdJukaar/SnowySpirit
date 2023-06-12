package net.mehvahdjukaar.snowyspirit.integration.fabric;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

import static net.mehvahdjukaar.snowyspirit.reg.ModRegistry.regWithItem;

public class FDCompatImpl {
    public static void init() {
    }

    public static final Supplier<Block> CORN_CRATE = regWithItem(
            "ginger_crate", () ->
                    new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)));
}

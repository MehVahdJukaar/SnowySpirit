package net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.HashSet;
import java.util.Set;

public interface IWreathProvider {
    void addWreath(BlockPos pos);

    void removeWreath(BlockPos pos);

    Set<BlockPos> getWreathBlocks();

    void setWreathBlocks(HashSet<BlockPos> engravedBlocks);

    boolean hasWreath(BlockPos pos);

    void updateWeathBlock(BlockPos pos, boolean hasWreath, Direction face, boolean open);
}


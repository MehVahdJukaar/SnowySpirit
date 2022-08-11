package net.mehvahdjukaar.snowyspirit.forge.capabilities;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;

public interface IWreathProvider {
    WreathData addWreath(BlockPos pos);

    void removeWreath(BlockPos pos, Level level, boolean animationAndDrop);

    Map<BlockPos, WreathData> getWreathBlocks();

    boolean hasWreath(BlockPos pos);

    void refreshWreathVisual(BlockPos pos, Level level);

    void refreshClientBlocksVisuals(Level level);

    void updateAllBlocks(ServerLevel level);

    public class WreathData{
        public Direction face = Direction.NORTH;
        public boolean open = true;
        public boolean hinge = true;

        public boolean needsInitialization = true;
        public Pair<Float, Float> openDimensions = null;
        public Pair<Float, Float> closedDimensions = null;

        public WreathData(BlockPos pos) {}
    }
}


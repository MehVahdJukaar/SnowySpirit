package net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Map;

public interface IWreathProvider {
    void addWreath(BlockPos pos, Direction direction, boolean open, boolean hinge);

    void removeWreath(BlockPos pos);

    Map<BlockPos, WreathData> getWreathBlocks();

    boolean hasWreath(BlockPos pos);

    void updateWeathBlock(BlockPos pos, Level level);

    void updateAllBlocksClient(ClientLevel level);

    void updateAllBlocks(ServerLevel level);

    public record WreathData(Direction face, Boolean open, Boolean hinge) {
    }
}


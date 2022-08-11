package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.HashSet;
import java.util.Set;


public class ClientBoundSyncAllWreaths implements Message {
    public final Set<BlockPos> pos;

    public ClientBoundSyncAllWreaths(FriendlyByteBuf buffer) {
        int count = buffer.readInt();
        this.pos = new HashSet<>();
        for (int i = 0; i < count; i++) {
            this.pos.add(buffer.readBlockPos());
        }
    }

    public ClientBoundSyncAllWreaths(Set<BlockPos> pos) {
        this.pos = pos;
    }

    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeInt(this.pos.size());
        for (BlockPos p : this.pos) {
            buffer.writeBlockPos(p);
        }
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        ClientReceivers.handleSyncAlWreathsPacket(this);
    }
}
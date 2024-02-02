package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;


public class ClientBoundSyncWreathMessage implements Message {

    public final BlockPos pos;
    public final boolean hasWreath;

    public ClientBoundSyncWreathMessage(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.hasWreath = buffer.readBoolean();
    }

    public ClientBoundSyncWreathMessage(BlockPos pos, boolean hasWreath) {
        this.pos = pos;
        this.hasWreath = hasWreath;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
        buffer.writeBoolean(this.hasWreath);
    }

    public void handle(NetworkHelper.Context context) {
        ClientReceivers.handleSyncWreathPacket(this);
    }
}
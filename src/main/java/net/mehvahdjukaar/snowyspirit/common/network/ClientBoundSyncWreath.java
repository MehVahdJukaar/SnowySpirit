package net.mehvahdjukaar.snowyspirit.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ClientBoundSyncWreath implements NetworkHandler.Message {
    public final BlockPos pos;
    public final boolean hasWreath;

    public ClientBoundSyncWreath(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.hasWreath = buffer.readBoolean();
    }

    public ClientBoundSyncWreath(BlockPos pos, boolean hasWreath) {
        this.pos = pos;
        this.hasWreath = hasWreath;
    }

    public static void buffer(ClientBoundSyncWreath message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.hasWreath);
    }

    public static void handler(ClientBoundSyncWreath message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientReceivers.handleSyncWreathPacket(message);
            }
        });
        context.setPacketHandled(true);
    }
}
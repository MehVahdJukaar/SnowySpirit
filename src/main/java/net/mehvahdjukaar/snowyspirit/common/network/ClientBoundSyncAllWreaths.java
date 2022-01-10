package net.mehvahdjukaar.snowyspirit.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;


public class ClientBoundSyncAllWreaths implements NetworkHandler.Message {
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

    public static void buffer(ClientBoundSyncAllWreaths message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.pos.size());
        for (BlockPos p : message.pos) {
            buffer.writeBlockPos(p);
        }
    }

    public static void handler(ClientBoundSyncAllWreaths message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientReceivers.handleSyncAlWreathsPacket(message);
            }
        });
        context.setPacketHandled(true);
    }
}
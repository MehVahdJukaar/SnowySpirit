package net.mehvahdjukaar.snowyspirit.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ClientBoundSyncWreath implements NetworkHandler.Message {
    public final BlockPos pos;

    public ClientBoundSyncWreath(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    public ClientBoundSyncWreath(BlockPos pos, boolean hasWreath, Direction face, boolean open) {
        this.pos = pos;
    }

    public static void buffer(ClientBoundSyncWreath message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
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
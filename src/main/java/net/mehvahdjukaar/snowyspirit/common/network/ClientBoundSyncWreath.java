package net.mehvahdjukaar.snowyspirit.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ClientBoundSyncWreath implements NetworkHandler.Message {
    public final BlockPos pos;
    public final boolean hasWreath;
    public final Direction face;
    public final boolean open;

    public ClientBoundSyncWreath(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.hasWreath = buffer.readBoolean();
        if(this.hasWreath) {
            this.face = buffer.readEnum(Direction.class);
            this.open = buffer.readBoolean();
        }
        else{
            this.face = Direction.NORTH;
            this.open = false;
        }
    }

    public ClientBoundSyncWreath(BlockPos pos, boolean hasWreath, Direction face, boolean open) {
        this.pos = pos;
        this.hasWreath = hasWreath;
        this.face = face;
        this.open = open;
    }

    public static void buffer(ClientBoundSyncWreath message, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(message.pos);
        buffer.writeBoolean(message.hasWreath);
        if(message.hasWreath) {
            buffer.writeEnum(message.face);
            buffer.writeBoolean(message.open);
        }
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
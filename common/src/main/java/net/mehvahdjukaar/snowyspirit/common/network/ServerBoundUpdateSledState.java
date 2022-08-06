package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ServerBoundUpdateSledState implements NetworkHandler.Message {
    public final float clientDx;
    public final float clientDy;
    public final float clientDz;

    public ServerBoundUpdateSledState(FriendlyByteBuf buffer) {
        this.clientDx = buffer.readFloat();
        this.clientDy = buffer.readFloat();
        this.clientDz = buffer.readFloat();
    }

    public ServerBoundUpdateSledState(Vec3 movement) {
        this.clientDx = (float)movement.x;
        this.clientDy = (float)movement.y;
        this.clientDz = (float)movement.z;

    }

    public static void buffer(ServerBoundUpdateSledState message, FriendlyByteBuf buffer) {
        buffer.writeFloat(message.clientDx);
        buffer.writeFloat(message.clientDy);
        buffer.writeFloat(message.clientDz);
    }

    public static void handler(ServerBoundUpdateSledState message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                if(context.getSender().getVehicle() instanceof SledEntity sled){
                    sled.setSyncedMovement(message.clientDx, message.clientDy, message.clientDx);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;


public class ServerBoundUpdateSledState implements Message {
    public final float clientDx;
    public final float clientDy;
    public final float clientDz;

    public ServerBoundUpdateSledState(FriendlyByteBuf buffer) {
        this.clientDx = buffer.readFloat();
        this.clientDy = buffer.readFloat();
        this.clientDz = buffer.readFloat();
    }

    public ServerBoundUpdateSledState(Vec3 movement) {
        this.clientDx = (float) movement.x;
        this.clientDy = (float) movement.y;
        this.clientDz = (float) movement.z;

    }

    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeFloat(this.clientDx);
        buffer.writeFloat(this.clientDy);
        buffer.writeFloat(this.clientDz);
    }

    @Override
    public void handle(ChannelHandler.Context context) {
        if (context.getSender().getVehicle() instanceof SledEntity sled) {
            sled.setSyncedMovement(this.clientDx, this.clientDy, this.clientDx);
        }
    }

}
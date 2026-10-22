package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.Message;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class ServerBoundUpdateSledStateMessage implements Message {

    public static final TypeAndCodec<RegistryFriendlyByteBuf, ServerBoundUpdateSledStateMessage> CODEC =
            Message.makeType(SnowySpirit.res("s2c_update_sled_movement"), ServerBoundUpdateSledStateMessage::new);

    private static final float MAX_SPEED = 4;

    public final float clientDx;
    public final float clientDy;
    public final float clientDz;

    public ServerBoundUpdateSledStateMessage(RegistryFriendlyByteBuf buffer) {
        this.clientDx = buffer.readFloat();
        this.clientDy = buffer.readFloat();
        this.clientDz = buffer.readFloat();
    }

    public ServerBoundUpdateSledStateMessage(Vec3 movement) {
        this.clientDx = (float) movement.x;
        this.clientDy = (float) movement.y;
        this.clientDz = (float) movement.z;

    }

    @Override
    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(this.clientDx);
        buffer.writeFloat(this.clientDy);
        buffer.writeFloat(this.clientDz);
    }

    @Override
    public void handle(Context context) {
        Player player = context.getPlayer();
        if (player.getVehicle() instanceof SledEntity sled && sled.getControllingPassenger() == player) {
            if (!Float.isFinite(clientDx) || !Float.isFinite(clientDy) || !Float.isFinite(clientDz)) return;
            sled.setSyncedMovement(Mth.clamp(clientDx, -MAX_SPEED, MAX_SPEED), Mth.clamp(clientDy, -MAX_SPEED, MAX_SPEED),
                    Mth.clamp(clientDz, -MAX_SPEED, MAX_SPEED));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return CODEC.type();
    }
}
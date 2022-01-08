package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.IWreathProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientReceivers {
    public static void handleSyncWreathPacket(ClientBoundSyncWreath message) {

            Level pLevel = Minecraft.getInstance().level;
            IWreathProvider pCap = pLevel.getCapability( CapabilityHandler.WREATH_CAPABILITY ).orElse( null );
            pCap.updateWeathBlock(message.pos, message.hasWreath, message.face, message.open );

    }
}

package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.IWreathProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

;

public class ClientReceivers {

    public static void handleSyncWreathPacket(ClientBoundSyncWreath message) {
        Level pLevel = Minecraft.getInstance().level;
        IWreathProvider pCap = pLevel.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
        pCap.refreshWreathVisual(message.pos, pLevel);

    }

    //blocks arent loaded yet here so we just add them all without parameters
    public static void handleSyncAlWreathsPacket(ClientBoundSyncAllWreaths message) {
        Level pLevel = Minecraft.getInstance().level;
        IWreathProvider pCap = pLevel.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
        message.pos.forEach(p -> pCap.addWreath(p, Direction.NORTH, true, true));
    }

}

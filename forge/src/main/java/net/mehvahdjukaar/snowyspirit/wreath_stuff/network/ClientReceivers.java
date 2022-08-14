package net.mehvahdjukaar.snowyspirit.wreath_stuff.network;

import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.WreathCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

;

public class ClientReceivers {

    public static void handleSyncWreathPacket(ClientBoundSyncWreathMessage message) {
        Level pLevel = Minecraft.getInstance().level;
        WreathCapability pCap = pLevel.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
        if (message.hasWreath) {
            pCap.refreshWreathVisual(message.pos, pLevel);
        } else {
            pCap.removeWreath(message.pos, pLevel, false);
        }

    }

    //blocks arent loaded yet here so we just add them all without parameters
    public static void handleSyncAlWreathsPacket(ClientBoundSyncAllWreaths message) {
        Level pLevel = Minecraft.getInstance().level;
        WreathCapability pCap = pLevel.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
        message.pos.forEach(pCap::addWreath);
    }

}

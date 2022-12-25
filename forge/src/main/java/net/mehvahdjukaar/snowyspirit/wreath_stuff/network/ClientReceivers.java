package net.mehvahdjukaar.snowyspirit.wreath_stuff.network;

import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.ModCapabilities;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.WreathCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientReceivers {

    public static void handleSyncWreathPacket(ClientBoundSyncWreathMessage message) {
        Level pLevel = Minecraft.getInstance().level;
        if (pLevel != null) {
            WreathCapability pCap = ModCapabilities.get(pLevel, ModCapabilities.WREATH_CAPABILITY);
            if (pCap != null) {
                if (message.hasWreath) {
                    pCap.refreshWreathVisual(message.pos, pLevel);
                } else {
                    pCap.removeWreath(message.pos, pLevel, false);
                }
            }
        }
    }

    //blocks arent loaded yet here so we just add them all without parameters
    public static void handleSyncAlWreathsPacket(ClientBoundSyncAllWreaths message) {
        Level pLevel = Minecraft.getInstance().level;
        if (pLevel != null) {
            WreathCapability pCap = ModCapabilities.get(pLevel, ModCapabilities.WREATH_CAPABILITY);
            if (pCap != null) {
                message.pos.forEach(pCap::addWreath);
            }
        }
    }

}

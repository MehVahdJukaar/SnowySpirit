package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.snowyspirit.common.wreath.WreathSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public class ClientReceivers {

    public static void handleSyncWreathPacket(ClientBoundSyncWreathMessage message) {
        Level pLevel = Minecraft.getInstance().level;
        WreathSavedData pCap = WreathSavedData.get(pLevel);
        if (pCap != null) {
            if (message.hasWreath) {
                pCap.refreshWreathVisual(message.pos, pLevel);
            } else {
                pCap.removeWreath(message.pos, pLevel, false);
            }
        }
    }

    //blocks arent loaded yet here so we just add them all without parameters
    public static void handleSyncAlWreathsPacket(ClientBoundSyncAllWreaths message) {
        Level pLevel = Minecraft.getInstance().level;
        if (pLevel != null) {
            WreathSavedData pCap = WreathSavedData.get(pLevel);
            if (pCap != null) {
                message.pos.forEach(pCap::addWreath);
            }
        }
    }

}

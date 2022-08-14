package net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CapabilityHandler {


    public static final Capability<WreathCapability> WREATH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(WreathCapability.class);
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
        WreathCapability wreathProvider = new WreathCapability();
        event.addCapability(SnowySpirit.res("wreath"), wreathProvider);
        event.addListener(wreathProvider::invalidate);
    }


}
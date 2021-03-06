package net.mehvahdjukaar.snowyspirit.common.capabilities;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.IWreathProvider;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.WreathProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CapabilityHandler {


    public static final Capability<IWreathProvider> WREATH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IWreathProvider.class);
    }

    public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
        WreathProvider wreathProvider = new WreathProvider();
        event.addCapability(Christmas.res("wreath"), wreathProvider);
        event.addListener(wreathProvider::invalidate);
    }


}
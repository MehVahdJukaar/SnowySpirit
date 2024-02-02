package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;

public class ModMessages {

    public static void init() {
        NetworkHelper.addRegistration(SnowySpirit.MOD_ID, event -> event
                .setVersion(2)
                .register(NetworkDir.SERVERBOUND, ServerBoundUpdateSledState.class, ServerBoundUpdateSledState::new)
                .register(NetworkDir.CLIENTBOUND, ClientBoundSyncWreathMessage.class, ClientBoundSyncWreathMessage::new)
                .register(NetworkDir.CLIENTBOUND, ClientBoundSyncAllWreaths.class, ClientBoundSyncAllWreaths::new));
    }

}
package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;

public class NetworkHandler {

    public static final ChannelHandler CHANNEL = ChannelHandler.builder(SnowySpirit.MOD_ID)
            .register(NetworkDir.PLAY_TO_SERVER, ServerBoundUpdateSledState.class, ServerBoundUpdateSledState::new)
            .register(NetworkDir.PLAY_TO_CLIENT, ClientBoundSyncWreathMessage.class, ClientBoundSyncWreathMessage::new)
            .register(NetworkDir.PLAY_TO_CLIENT, ClientBoundSyncAllWreaths.class, ClientBoundSyncAllWreaths::new)
            .build();

    public static void init() {

    }

}
package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;

public class NetworkHandler {

    public static final ChannelHandler CHANNEL = ChannelHandler.createChannel(SnowySpirit.res("network"));


    static {

        CHANNEL.register(NetworkDir.PLAY_TO_SERVER, ServerBoundUpdateSledState.class, ServerBoundUpdateSledState::new);
    }

    public static void init(){

    }

}
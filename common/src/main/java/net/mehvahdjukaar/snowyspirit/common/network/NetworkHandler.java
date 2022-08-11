package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.moonlight.api.platform.network.ChannelHandler;
import net.mehvahdjukaar.moonlight.api.platform.network.NetworkDir;
import net.mehvahdjukaar.supplementaries.Supplementaries;
import net.mehvahdjukaar.supplementaries.common.network.ClientBoundPlaySpeakerMessagePacket;

public class NetworkHandler {

    public static ChannelHandler CHANNEL;

    public static void registerMessages() {

        CHANNEL = ChannelHandler.createChannel(Supplementaries.res("network"));

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                ClientBoundPlaySpeakerMessagePacket.class, ClientBoundPlaySpeakerMessagePacket::new);

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                ClientBoundSyncWreathMessage.class,
                ClientBoundSyncWreathMessage::new);

        CHANNEL.register(NetworkDir.PLAY_TO_CLIENT,
                ClientBoundSyncAllWreaths.class,
                ClientBoundSyncAllWreaths::new);

        CHANNEL.register(NetworkDir.PLAY_TO_SERVER,
                ServerBoundUpdateSledState.class,
                ServerBoundUpdateSledState::new);
    }

}
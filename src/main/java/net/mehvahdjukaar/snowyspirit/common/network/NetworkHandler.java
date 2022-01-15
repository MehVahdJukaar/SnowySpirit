package net.mehvahdjukaar.snowyspirit.common.network;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NetworkHandler {
    public static SimpleChannel INSTANCE;
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";

    private static <MSG> void register(Class<MSG> messageClass, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        INSTANCE.registerMessage(ID++, messageClass, encoder, decoder, messageConsumer);
    }


    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(Christmas.res("network"), () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        register(ClientBoundSyncWreath.class, ClientBoundSyncWreath::buffer,
                ClientBoundSyncWreath::new, ClientBoundSyncWreath::handler);
        register(ClientBoundSyncAllWreaths.class, ClientBoundSyncAllWreaths::buffer,
                ClientBoundSyncAllWreaths::new, ClientBoundSyncAllWreaths::handler);

    }

    public static void sendToAllTrackingClients(Entity entity, ServerLevel world, Message message) {
        world.getChunkSource().broadcast(entity, INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendToAllInRangeClients(BlockPos pos, ServerLevel level, double distance, Message message) {
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if (currentServer != null) {
            PlayerList players = currentServer.getPlayerList();
            var dimension = level.dimension();
            players.broadcast(null, pos.getX(), pos.getY(), pos.getZ(),
                    distance,
                    dimension, INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServerPlayer(Message message) {
        Minecraft.getInstance().getConnection().send(
                INSTANCE.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER));
    }


    public interface Message {

    }
}
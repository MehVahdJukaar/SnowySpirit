package net.mehvahdjukaar.snowyspirit.common;

import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.common.block.WreathBlock;
import net.mehvahdjukaar.snowyspirit.common.capabilities.CapabilityHandler;
import net.mehvahdjukaar.snowyspirit.common.capabilities.wreath_cap.IWreathProvider;
import net.mehvahdjukaar.snowyspirit.common.network.ClientBoundSyncAllWreaths;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Christmas.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {


    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        CapabilityHandler.register(event);
        int a = 2;
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
        CapabilityHandler.attachCapabilities(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        ItemStack stack = event.getItemStack();
        if (stack.is(ModRegistry.WREATH_ITEM.get())) {
            Level level = event.getWorld();

            if (WreathBlock.placeWreathOnDoor(event.getPos(), level)) {
                if (!event.getPlayer().getAbilities().instabuild) {
                    stack.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickEvent(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world instanceof ServerLevel level) {
            level.getCapability(CapabilityHandler.WREATH_CAPABILITY).ifPresent(c -> c.updateAllBlocks(level));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ServerLevel level = player.getLevel();
        IWreathProvider cap = level.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
        if (cap != null)
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new ClientBoundSyncAllWreaths(cap.getWreathBlocks().keySet()));
    }

    @SubscribeEvent
    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();
        ServerLevel level = player.getLevel();
        IWreathProvider cap = level.getCapability(CapabilityHandler.WREATH_CAPABILITY).orElse(null);
        if (cap != null)
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new ClientBoundSyncAllWreaths(cap.getWreathBlocks().keySet()));
    }


}

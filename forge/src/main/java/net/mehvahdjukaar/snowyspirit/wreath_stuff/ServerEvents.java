package net.mehvahdjukaar.snowyspirit.wreath_stuff;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.network.NetworkHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.ModCapabilities;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.capabilities.WreathCapability;
import net.mehvahdjukaar.snowyspirit.wreath_stuff.network.ClientBoundSyncAllWreaths;
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

@Mod.EventBusSubscriber(modid = SnowySpirit.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEvents {


    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        ModCapabilities.register(event);
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Level> event) {
        ModCapabilities.attachCapabilities(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {

        ItemStack stack = event.getItemStack();
        if (stack.is(ModRegistry.WREATH.get().asItem())) {
            Level level = event.getLevel();

            if (WreathHelper.placeWreathOnDoor(event.getPos(), level)) {
                if (!event.getEntity().getAbilities().instabuild) {
                    stack.shrink(1);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void tickEvent(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel level) {
            level.getCapability(ModCapabilities.WREATH_CAPABILITY).ifPresent(c -> c.updateAllBlocks(level));
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = (ServerLevel) player.level();
        WreathCapability cap = ModCapabilities.get(level, ModCapabilities.WREATH_CAPABILITY);
        if (cap != null)
            NetworkHandler.CHANNEL.sendToClientPlayer(player,
                    new ClientBoundSyncAllWreaths(cap.getWreathBlocks().keySet()));
    }

    @SubscribeEvent
    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerLevel level = (ServerLevel) player.level();
        WreathCapability cap = ModCapabilities.get(level, ModCapabilities.WREATH_CAPABILITY);
        if (cap != null)
            NetworkHandler.CHANNEL.sendToClientPlayer(player,
                    new ClientBoundSyncAllWreaths(cap.getWreathBlocks().keySet()));
    }


}

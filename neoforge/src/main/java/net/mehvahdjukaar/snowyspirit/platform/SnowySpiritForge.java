package net.mehvahdjukaar.snowyspirit.platform;

import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.common.wreath.WreathHandler;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

/**
 * Author: MehVahdJukaar
 */
@Mod(SnowySpirit.MOD_ID)
public class SnowySpiritForge {

    public SnowySpiritForge(IEventBus busEvent) {

        SnowySpirit.commonInit();

        NeoForge.EVENT_BUS.register(this);
        PlatHelper.addCommonSetup(() -> {
            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(Utils.getID(ModRegistry.GINGER.get()), ModRegistry.GINGER_POT);
        });

        busEvent.addListener(this::onRegisterCapability);

    }

    public void onRegisterCapability(RegisterCapabilitiesEvent event) {
        event.registerEntity(Capabilities.ItemHandler.ENTITY,
                ModRegistry.CONTAINER_ENTITY.get(),
                (entity, object2) -> new InvWrapper(entity));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult res = WreathHandler.onRightClickBlock(event.getEntity(), event.getLevel(), event.getItemStack(), event.getPos());
        if (res != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(res);
        }
    }
}

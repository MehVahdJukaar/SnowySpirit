package net.mehvahdjukaar.randomium.forge;

import net.mehvahdjukaar.randomium.items.RandomiumItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RandomiumForge.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SnowySpiritForgeClient {

    @SubscribeEvent
    public static void test(TickEvent.ClientTickEvent tickEvent) {
        if (tickEvent.phase == TickEvent.Phase.END) {
            RandomiumItem.tickEffects();
        }
    }
}

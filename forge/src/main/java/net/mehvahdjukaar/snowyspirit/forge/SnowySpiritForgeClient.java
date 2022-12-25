package net.mehvahdjukaar.snowyspirit.forge;

import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.client.SammysParticleHacks;
import net.mehvahdjukaar.snowyspirit.integration.configured.ModConfigSelectScreen;
import net.mehvahdjukaar.snowyspirit.reg.ClientRegistry;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SnowySpirit.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SnowySpiritForgeClient {


    @SubscribeEvent
    public static void init(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientRegistry.setup();

            if (ModList.get().isLoaded("configured")) {
                ModConfigSelectScreen.registerConfigScreen(SnowySpirit.MOD_ID, ModConfigSelectScreen::new);
            }

        });
    }

    @Mod.EventBusSubscriber(modid = SnowySpirit.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRenderStage(RenderLevelStageEvent event) {
            if ((event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)) {

           //     SammysParticleHacks.renderLast(event.getPoseStack());
            }
        }

        @SubscribeEvent
        public static void onRenderLast(RenderLevelLastEvent event) {
             //   SammysParticleHacks.renderLast(event.getPoseStack());
        }
    }
}

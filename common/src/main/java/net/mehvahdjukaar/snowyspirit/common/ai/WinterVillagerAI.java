package net.mehvahdjukaar.snowyspirit.common.ai;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.moonlight.api.events.IVillagerBrainEvent;
import net.mehvahdjukaar.moonlight.api.events.MoonlightEventsHelper;
import net.mehvahdjukaar.moonlight.api.util.VillagerAIManager;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.integration.supplementaries.PlacePresentTask;
import net.mehvahdjukaar.snowyspirit.reg.ModMemoryModules;
import net.mehvahdjukaar.supplementaries.configs.RegistryConfigs;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;

public class WinterVillagerAI {

    public static boolean PRESENTS_ENABLED = RegistryConfigs.PRESENT_ENABLED.get();

    public static void setup() {

        MoonlightEventsHelper.addListener(WinterVillagerAI::onVillagerBrainInitialize, IVillagerBrainEvent.class);

        VillagerAIManager.registerMemory(ModMemoryModules.PLACED_PRESENT.get());
        VillagerAIManager.registerMemory(ModMemoryModules.WREATH_POS.get());
    }

    public static void onVillagerBrainInitialize(IVillagerBrainEvent event) {
        Villager villager = event.getVillager();
        if (SnowySpirit.isChristmasSeason(villager.level)) {
            if (villager.isBaby()) {
                var t = villager.getVillagerData().getType();
                if (t == VillagerType.SNOW || t == VillagerType.TAIGA) {
                    event.addTaskToActivity(Activity.PLAY, Pair.of(4, new ThrowSnowballsTask(10)));
                }
            } else {
                //event.addOrReplaceActivity(Activity.IDLE, getHalloweenIdlePackage(event.getVillager().getVillagerData().getProfession(), 0.5F));
                if (PRESENTS_ENABLED) {
                    event.addTaskToActivity(Activity.MEET, Pair.of(3, new PlacePresentTask(0.5f)));
                }
                event.addTaskToActivity(Activity.IDLE, Pair.of(3, new PlaceWreathTask(0.5f)));
            }
        } else {
            if (!villager.isBaby()) {
                event.addTaskToActivity(Activity.IDLE, Pair.of(4, new RemoveWreathTask(0.5f)));
            }
        }
    }
}
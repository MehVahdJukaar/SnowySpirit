package net.mehvahdjukaar.snowyspirit.common.ai;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.selene.villager_ai.VillagerAIManager;
import net.mehvahdjukaar.selene.villager_ai.VillagerBrainEvent;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.mehvahdjukaar.snowyspirit.suppcompat.PlacePresentTask;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.fml.ModList;

public class WinterVillagerAI {

    public static boolean PRESENTS_ENABLED = ModList.get().isLoaded("supplementaries");

    public static void init(){

        VillagerAIManager.addVillagerAiEventListener(WinterVillagerAI::onVillagerBrainInitialize);

        VillagerAIManager.registerMemory(ModRegistry.PLACED_PRESENT.get());
        VillagerAIManager.registerMemory(ModRegistry.WREATH_POS.get());
    }

    public static void onVillagerBrainInitialize(VillagerBrainEvent event){
        if(Christmas.isChristmasTime()) {
            Villager villager = event.getVillager();
            if(villager.isBaby()){
                var t = villager.getVillagerData().getType();
                if(t == VillagerType.SNOW || t == VillagerType.TAIGA) {
                    event.addTaskToActivity(Activity.PLAY, Pair.of(4, new ThrowSnowballsTask(10)));
                }
            }
            else {
                //event.addOrReplaceActivity(Activity.IDLE, getHalloweenIdlePackage(event.getVillager().getVillagerData().getProfession(), 0.5F));
                if(PRESENTS_ENABLED) {
                    event.addTaskToActivity(Activity.MEET, Pair.of(3, new PlacePresentTask(0.5f)));
                }
                event.addTaskToActivity(Activity.IDLE, Pair.of(3, new PlaceWreathTask(0.5f)));
            }
        }
    }
}
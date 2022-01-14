package net.mehvahdjukaar.snowyspirit.common.ai;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.selene.Selene;
import net.mehvahdjukaar.selene.villager_ai.VillagerAIManager;
import net.mehvahdjukaar.selene.villager_ai.VillagerBrainEvent;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WinterVillagerAI {


    public static void init(){

        VillagerAIManager.addVillagerAiEventListener(WinterVillagerAI::onVillagerBrainInitialize);
        if(ModList.get().isLoaded("supplementaries")) {
            VillagerAIManager.registerMemory(ModRegistry.PLACED_PRESENT.get());
        }
        VillagerAIManager.registerMemory(ModRegistry.WREATH_POS.get());
    }

    public static void onVillagerBrainInitialize(VillagerBrainEvent event){
        if(Christmas.isChristmasTime() && !event.getVillager().isBaby()) {
            //event.addOrReplaceActivity(Activity.IDLE, getHalloweenIdlePackage(event.getVillager().getVillagerData().getProfession(), 0.5F));
            event.addTaskToActivity(Activity.MEET,Pair.of(3, new PlacePresentTask(0.5f)));
            event.addTaskToActivity(Activity.IDLE,Pair.of(3, new PlaceWreathTask(0.5f)));
        }
    }

    public static ImmutableList<Pair<Integer, ? extends Behavior<? super Villager>>> getHalloweenIdlePackage(VillagerProfession profession, float speed) {
        var old = VillagerGoalPackages.getIdlePackage(profession, speed);
        List<Pair<Integer, ? extends Behavior<? super Villager>>> mutable = new ArrayList<>(old);

        mutable.add(Pair.of(3, new PlacePresentTask(speed)));

        return ImmutableList.copyOf(mutable);
    }
}
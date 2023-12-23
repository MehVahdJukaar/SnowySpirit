package net.mehvahdjukaar.snowyspirit.common.ai.forge;

import com.mojang.datafixers.util.Pair;
import net.mehvahdjukaar.moonlight.api.events.IVillagerBrainEvent;
import net.mehvahdjukaar.snowyspirit.common.ai.PlaceWreathTask;
import net.mehvahdjukaar.snowyspirit.common.ai.RemoveWreathTask;
import net.minecraft.world.entity.schedule.Activity;

public class WinterVillagerAIImpl {
    public static void addRemoveWreath(IVillagerBrainEvent event) {
        event.addTaskToActivity(Activity.IDLE, Pair.of(4, new RemoveWreathTask(0.5f)));
    }

    public static void addPlaceWreath(IVillagerBrainEvent event) {
        event.addTaskToActivity(Activity.IDLE, Pair.of(3, new PlaceWreathTask(0.5f)));
    }
}

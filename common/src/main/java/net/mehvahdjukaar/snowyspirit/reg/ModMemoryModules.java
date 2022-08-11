package net.mehvahdjukaar.snowyspirit.reg;

import com.mojang.serialization.Codec;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Optional;
import java.util.function.Supplier;

public class ModMemoryModules {

  public static void init(){

  }

    public static final Supplier<MemoryModuleType<Boolean>> PLACED_PRESENT =
            registerFeature("placed_present", () -> new MemoryModuleType<>(Optional.of(Codec.BOOL)));

    public static final Supplier<MemoryModuleType<GlobalPos>> WREATH_POS =
            registerFeature("wreath_pos", () -> new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));


    public static <T extends MemoryModuleType<?>> RegSupplier<T> registerFeature(String name, Supplier<T> memoryModule) {
        return RegHelper.register(SnowySpirit.res(name), memoryModule, Registry.MEMORY_MODULE_TYPE);
    }

}

package net.mehvahdjukaar.snowyspirit.reg;

import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.client.SledSoundInstance;
import net.mehvahdjukaar.snowyspirit.common.entity.SledEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {

    public static void init(){

    }

    public static final Supplier<SoundEvent> WINTER_MUSIC = makeSoundEvent("music.winter");
    public static final Supplier<SoundEvent> SLED_SOUND = makeSoundEvent("entity.sled");
    public static final Supplier<SoundEvent> SLED_SOUND_SNOW = makeSoundEvent("entity.sled_snow");


    private static Supplier<SoundEvent> makeSoundEvent(String name) {
        return RegHelper.registerSound(SnowySpirit.res(name), () -> new SoundEvent(SnowySpirit.res(name)));
    }

    public static void playSledSounds(SledEntity sledEntity) {
        Minecraft.getInstance().getSoundManager().play(new SledSoundInstance(sledEntity, false));
        Minecraft.getInstance().getSoundManager().play(new SledSoundInstance(sledEntity,true));
    }

}

package net.mehvahdjukaar.snowyspirit.integration;

import dev.architectury.injectables.annotations.ExpectPlatform;


public class FDCompat {

    @ExpectPlatform
    public static void init() {
        throw new AssertionError();
    }
}

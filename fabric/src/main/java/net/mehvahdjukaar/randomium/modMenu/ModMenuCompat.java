package net.mehvahdjukaar.randomium.modMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.moonlight.api.integration.fabric.ClothConfigCompat;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.FabricConfigSpec;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.configs.CommonConfigs;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if( CommonConfigs.SPEC instanceof ConfigSpec spec){
            return parent-> ClothConfigCompat.makeScreen(parent, (FabricConfigSpec) spec, Randomium.res(
                    "textures/blocks/gingerbread_frosted_block.png"
            ));
        }
        return parent -> null;
    }
}
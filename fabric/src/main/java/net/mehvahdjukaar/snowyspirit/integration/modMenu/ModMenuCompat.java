package net.mehvahdjukaar.snowyspirit.integration.modMenu;

import com.nhoryzon.mc.farmersdelight.FarmersDelightMod;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.moonlight.api.integration.cloth_config.ClothConfigCompat;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.FabricConfigSpec;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.configs.RegistryConfigs;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ClothConfigCompat.makeScreen(parent, (FabricConfigSpec) RegistryConfigs.SPEC, SnowySpirit.res(
                "textures/blocks/gingerbread_frosted_block.png"
        ));
    }
}
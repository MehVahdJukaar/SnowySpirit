package net.mehvahdjukaar.snowyspirit.integration.platform.mod_menu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;

public class ModMenuCompat implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> ClientHelper.getMoonlightConfigScreen(SnowySpirit.MOD_ID, parent,
                SnowySpirit.res("textures/block/gingerbread_frosted_block.png"));
    }
}

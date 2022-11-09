package net.mehvahdjukaar.snowyspirit.integration.configured;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mrcrayfish.configured.api.IModConfig;
import com.mrcrayfish.configured.client.util.ScreenUtil;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigScreen;
import net.mehvahdjukaar.moonlight.api.integration.configured.CustomConfigSelectScreen;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.configs.RegistryConfigs;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

//credits to MrCrayfish's Configured Mod
public class ModConfigScreen extends CustomConfigScreen {

    private static final Map<String, ItemStack> ICONS = new HashMap<>();

    static {
        addIcon("sleds", ModRegistry.SLED_ITEMS.get(WoodTypeRegistry.OAK_TYPE));
        addIcon("gumdrops", ModRegistry.GUMDROPS_BUTTONS.get(DyeColor.GREEN).get().asItem());
        addIcon("glow lights", ModRegistry.GLOW_LIGHTS_ITEMS.get(null).get().asItem());
        addIcon("blocks and items", ModRegistry.CANDY_CANE_BLOCK.get());
        addIcon("snowy season", ModRegistry.SNOW_GLOBE.get());
        addIcon("misc", ModRegistry.GINGERBREAD_COOKIE.get());
    }

    public ModConfigScreen(CustomConfigSelectScreen parent, IModConfig config) {
        super(parent, config);
        this.icons.putAll(ICONS);
    }

    public ModConfigScreen(String modId, ItemStack mainIcon, ResourceLocation background, Component title, Screen parent,
                           IModConfig config) {
        super(modId, mainIcon, background, title, parent, config);
        this.icons.putAll(ICONS);
    }


    private static void addIcon(String s, ItemLike i) {
        ICONS.put(s, i.asItem().getDefaultInstance());
    }

    @Override
    public boolean hasFancyBooleans() {
        return true;
    }

    @Override
    public void onSave() {
    }

    @Override
    public CustomConfigScreen createSubScreen(Component title) {
        return new ModConfigScreen(this.modId, this.mainIcon, this.background, title, this, this.config);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        var level = Minecraft.getInstance().level;
        if(level != null && SnowySpirit.isChristmasSeason(level)) {
            int x = (int) (this.width * 0.93f);
            this.itemRenderer.renderAndDecorateFakeItem(Items.SNOWBALL.getDefaultInstance(), x, 16);
            if (ScreenUtil.isMouseWithin(x , 16, 16, 16, mouseX, mouseY)) {
                this.renderTooltip(poseStack, this.font.split(Component.translatable("gui.snowyspirit.snow_season_on").withStyle(ChatFormatting.AQUA), 200), mouseX, mouseY);
            }
        }
    }
}
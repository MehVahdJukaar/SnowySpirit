package net.mehvahdjukaar.snowyspirit.client;

import net.mehvahdjukaar.moonlight.api.client.gui.ConfigScreenExtensions;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public class SeasonConfigOverlay implements ConfigScreenExtensions.Overlay {

    private static final int ON_COLOR = 0xFF55FFEB;
    private static final int OFF_COLOR = 0xFF9CA2A1;
    private static final int UNKNOWN_COLOR = 0xFFA0A0A0;

    public static void register() {
        ConfigScreenExtensions.registerOverlay(SnowySpirit.MOD_ID, new SeasonConfigOverlay());
    }

    @Override
    public void render(GuiGraphics graphics, ConfigScreenExtensions.Panel panel, int mouseX, int mouseY, float partialTick) {
        Level level = Minecraft.getInstance().level;
        String key;
        int color;
        //season mods decide per world, so outside of one we have nothing to go by
        if (SnowySpirit.USES_SEASON_MOD && level == null) {
            key = "gui.snowyspirit.snow_season_unknown";
            color = UNKNOWN_COLOR;
        } else if (level != null ? SnowySpirit.isChristmasSeason(level) : SnowySpirit.isChristmasRealTime()) {
            key = "gui.snowyspirit.snow_season_on";
            color = ON_COLOR;
        } else {
            key = "gui.snowyspirit.snow_season_off";
            color = OFF_COLOR;
        }
        var font = Minecraft.getInstance().font;
        int x = (panel.left() + panel.right()) / 2;
        int y = panel.bottom() - font.lineHeight - 4;
        graphics.drawCenteredString(font, Component.translatable(key), x, y, color);
    }
}

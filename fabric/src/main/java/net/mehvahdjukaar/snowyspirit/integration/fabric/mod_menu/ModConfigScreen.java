package net.mehvahdjukaar.snowyspirit.integration.fabric.mod_menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mehvahdjukaar.moonlight.api.client.gui.LinkButton;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.FabricConfigListScreen;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.configs.ClientConfigs;
import net.mehvahdjukaar.snowyspirit.configs.CommonConfigs;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModConfigScreen extends FabricConfigListScreen {

    public ModConfigScreen(Screen parent) {
        super(SnowySpirit.MOD_ID, ModRegistry.WREATH.get().asItem().getDefaultInstance(),
                Component.literal(ChatFormatting.AQUA + "Snowy Spirit Configs"),
                SnowySpirit.res("textures/blocks/gingerbread_frosted_block.png"),
                parent, CommonConfigs.SPEC, ClientConfigs.SPEC);
    }

    @Override
    protected void addExtraButtons() {

        ResourceLocation icons = LinkButton.MISC_ICONS;

        int y = this.height - 27;
        int centerX = this.width / 2;

        this.addRenderableWidget(new Button(centerX - 45, y, 90, 20, CommonComponents.GUI_BACK, (button) -> this.minecraft.setScreen(this.parent)));

        LinkButton patreon = LinkButton.create(icons, this, centerX - 45 - 22, y, 3, 1,
                "https://www.patreon.com/user?u=53696377", "Support me on Patreon :D");

        LinkButton kofi = LinkButton.create(icons, this, centerX - 45 - 22 * 2, y, 2, 2,
                "https://ko-fi.com/mehvahdjukaar", "Donate a Coffe");

        LinkButton curseforge = LinkButton.create(icons, this, centerX - 45 - 22 * 3, y, 1, 2,
                "https://www.curseforge.com/minecraft/mc-mods/snowy-spirit", "CurseForge Page");

        LinkButton github = LinkButton.create(icons, this, centerX - 45 - 22 * 4, y, 0, 2,
                "https://github.com/MehVahdJukaar/SnowySpirit", "Github");


        LinkButton discord = LinkButton.create(icons, this, centerX + 45 + 2, y, 1, 1,
                "https://discord.com/invite/qdKRTDf8Cv", "Mod Discord");

        LinkButton youtube = LinkButton.create(icons, this, centerX + 45 + 2 + 22, y, 0, 1,
                "https://www.youtube.com/watch?v=LSPNAtAEn28&t=1s", "Youtube Channel");

        LinkButton twitter = LinkButton.create(icons, this, centerX + 45 + 2 + 22 * 2, y, 2, 1,
                "https://twitter.com/Supplementariez?s=09", "Twitter Page");

        LinkButton akliz = LinkButton.create(icons, this, centerX + 45 + 2 + 22 * 3, y, 3, 2,
                "https://www.akliz.net/supplementaries", "Need a server? Get one with Akliz");


        this.addRenderableWidget(kofi);
        this.addRenderableWidget(akliz);
        this.addRenderableWidget(patreon);
        this.addRenderableWidget(curseforge);
        this.addRenderableWidget(discord);
        this.addRenderableWidget(youtube);
        this.addRenderableWidget(github);
        this.addRenderableWidget(twitter);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        var level = Minecraft.getInstance().level;
        if (level != null && SnowySpirit.isChristmasSeason(level)) {
            int x = (int) (this.width * 0.93f);
            this.itemRenderer.renderAndDecorateFakeItem(Items.SNOWBALL.getDefaultInstance(), x, 16);
        }
    }

}


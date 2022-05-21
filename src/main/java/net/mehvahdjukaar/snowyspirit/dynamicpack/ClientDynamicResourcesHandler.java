package net.mehvahdjukaar.snowyspirit.dynamicpack;

import net.mehvahdjukaar.selene.client.asset_generators.LangBuilder;
import net.mehvahdjukaar.selene.client.asset_generators.textures.Palette;
import net.mehvahdjukaar.selene.client.asset_generators.textures.Respriter;
import net.mehvahdjukaar.selene.client.asset_generators.textures.SpriteUtils;
import net.mehvahdjukaar.selene.client.asset_generators.textures.TextureImage;
import net.mehvahdjukaar.selene.resourcepack.*;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.init.Configs;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import org.apache.logging.log4j.Logger;

public class ClientDynamicResourcesHandler extends RPAwareDynamicTextureProvider {

    public ClientDynamicResourcesHandler() {
        super(new DynamicTexturePack(Christmas.res("virtual_resourcepack")));
        this.dynamicPack.generateDebugResources = false;
    }

    @Override
    public Logger getLogger() {
        return Christmas.LOGGER;
    }

    @Override
    public boolean dependsOnLoadedPacks() {
        return Configs.RESOURCE_PACK_SUPPORT.get();
    }

    @Override
    public void generateStaticAssetsOnStartup(ResourceManager manager) {
        //LangBuilder langBuilder = new LangBuilder();

        StaticResource itemModel = StaticResource.getOrLog(manager,
                ResType.ITEM_MODELS.getPath(Christmas.res("sled_oak")));

        ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {
            //if(wood.isVanilla())continue;
            // langBuilder.addEntry(sled, wood.getNameForTranslation("sled"));

            try {
                dynamicPack.addSimilarJsonResource(itemModel,
                        "sled_oak", wood.getVariantId("sled"));
            } catch (Exception ex) {
                getLogger().error("Failed to generate Sled item model for {} : {}", sled, ex);
            }
        });

        //dynamicPack.addLang(Christmas.res("en_us"), langBuilder.build());
    }

    @Override
    public void regenerateDynamicAssets(ResourceManager manager) {
        //entity textures
        try (TextureImage template = TextureImage.open(manager, Christmas.res("entity/sled/oak"))) {

            Respriter respriter = Respriter.of(template);

            ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {
                //if (wood.isVanilla()) continue;
                ResourceLocation textureRes = Christmas.res("entity/sled/" + wood.getTexturePath());
                if (this.alreadyHasTextureAtLocation(manager, textureRes)) return;


                try (TextureImage plankTexture = TextureImage.open(manager,
                             RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {
                    //Palette targetPalette = SpriteUtils.extrapolateWoodItemPalette(plankTexture);
                    var targetPalette = Palette.fromImage(plankTexture);
                    TextureImage newImage = respriter.recolor(targetPalette);
                    //TextureImage newImage = respriter.recolorWithAnimationOf(plankTexture);
                    dynamicPack.addAndCloseTexture(textureRes, newImage);

                } catch (Exception ex) {
                    getLogger().error("Failed to generate Sign Post item texture for for {} : {}", sled, ex);
                }
            });
        } catch (Exception ex) {
            getLogger().error("Could not generate any Sled entity texture : ", ex);
        }

        //item textures
        try (TextureImage template = TextureImage.open(manager, Christmas.res("items/sleds/sled_oak"));
             TextureImage boatMask = TextureImage.open(manager, Christmas.res("items/sleds/boat_mask"));
             TextureImage sledMask = TextureImage.open(manager, Christmas.res("items/sleds/sled_mask"))) {

            Palette palette = Palette.fromImage(template, sledMask);
            Respriter respriter = Respriter.ofPalette(template, palette);

            ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {
                //if (wood.isVanilla()) continue;
                ResourceLocation textureRes = Christmas.res("items/sleds/"+sled.getRegistryName().getPath());
                if (this.alreadyHasTextureAtLocation(manager, textureRes)) return;

                TextureImage newImage = null;
                if (wood.boatItem != null) {
                    try (TextureImage vanillaBoat = TextureImage.open(manager,
                                 RPUtils.findFirstItemTextureLocation(manager, wood.boatItem.get()))) {

                        Palette targetPalette = Palette.fromImage(vanillaBoat, boatMask);
                        newImage = respriter.recolor(targetPalette);

                    } catch (Exception ex) {
                        getLogger().error("Could not find boat texture for wood type {}. Using plank texture : {}", wood, ex);
                    }
                }
                //if it failed use plank one
                if (newImage == null) {
                    try (TextureImage plankPalette = TextureImage.open(manager,
                                 RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {
                        Palette targetPalette = SpriteUtils.extrapolateWoodItemPalette(plankPalette);
                        newImage = respriter.recolor(targetPalette);

                    } catch (Exception ex) {
                        getLogger().error("Failed to generate Sign Post item texture for for {} : {}", sled, ex);
                    }
                }
                if (newImage != null) {
                    dynamicPack.addAndCloseTexture(textureRes, newImage);
                }
            });
        } catch (Exception ex) {
            getLogger().error("Could not generate any Sleds item texture : ", ex);
        }
    }

    @Override
    public void addDynamicTranslations(DynamicLanguageManager.LanguageAccessor lang) {
        ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {
            LangBuilder.addDynamicEntry(lang, "item.snowyspirit.sled", wood, sled);
        });
    }
}

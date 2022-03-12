package net.mehvahdjukaar.snowyspirit.dynamicpack;

import com.mojang.blaze3d.platform.NativeImage;
import net.mehvahdjukaar.selene.block_set.wood.WoodType;
import net.mehvahdjukaar.selene.resourcepack.AssetGenerators;
import net.mehvahdjukaar.selene.resourcepack.DynamicTexturePack;
import net.mehvahdjukaar.selene.resourcepack.RPUtils;
import net.mehvahdjukaar.selene.resourcepack.RPUtils.ResType;
import net.mehvahdjukaar.selene.resourcepack.RPUtils.StaticResource;
import net.mehvahdjukaar.selene.resourcepack.ResourcePackAwareDynamicTextureProvider;
import net.mehvahdjukaar.selene.textures.Palette;
import net.mehvahdjukaar.selene.textures.Respriter;
import net.mehvahdjukaar.selene.textures.SpriteUtils;
import net.mehvahdjukaar.snowyspirit.Christmas;
import net.mehvahdjukaar.snowyspirit.init.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.Logger;

public class ClientDynamicResourcesHandler extends ResourcePackAwareDynamicTextureProvider {

    public static final ClientDynamicResourcesHandler INSTANCE = new ClientDynamicResourcesHandler();
    public static final DynamicTexturePack DYNAMIC_TEXTURE_PACK =
            new DynamicTexturePack(Christmas.res("virtual_resourcepack"));

    public static void registerBus(IEventBus bus) {
        DYNAMIC_TEXTURE_PACK.registerPack(bus);

        DYNAMIC_TEXTURE_PACK.generateDebugResources = true;
    }

    @Override
    public DynamicTexturePack getDynamicPack() {
        return DYNAMIC_TEXTURE_PACK;
    }

    @Override
    public Logger getLogger() {
        return Christmas.LOGGER;
    }

    @Override
    public boolean hasTexturePackSupport() {
        return false;
    }

    @Override
    public void generateStaticAssetsOnStartup(ResourceManager manager) {
        //generate static resources

        AssetGenerators.LangBuilder langBuilder = new AssetGenerators.LangBuilder();

        //------sleds item models-----
        {
            StaticResource itemModel = getResOrLog(manager,
                    RPUtils.resPath(Christmas.res("sled_oak"), ResType.ITEM_MODELS));

            for (var e : ModRegistry.SLED_ITEMS.entrySet()) {
                WoodType wood = e.getKey();
                if (!wood.isVanilla() || true) {
                    var v = e.getValue();
                    langBuilder.addEntry(v, e.getKey().getNameForTranslation("sled"));

                    try {
                        DYNAMIC_TEXTURE_PACK.addSimilarJsonResource(itemModel,
                                "sled_oak", wood.getVariantId("sled"));
                    } catch (Exception ex) {
                        getLogger().error("Failed to generate Sled item model for {} : {}", v, ex);
                    }
                }
            }
        }

        DYNAMIC_TEXTURE_PACK.addLang(Christmas.res("en_us"), langBuilder.build());
    }

    @Override
    public void regenerateTextures(ResourceManager manager) {
        //entity textures
        try (NativeImage template = readImage(manager, Christmas.res("textures/entity/sled/oak.png"))) {

            Respriter respriter = new Respriter(template);

            for (var e : ModRegistry.SLED_ITEMS.entrySet()) {
                WoodType wood = e.getKey();
                //if (wood.isVanilla()) continue;
                ResourceLocation textureRes = Christmas.res(
                        String.format("entity/sled/%s", wood.getTexturePath()));
                if (this.alreadyHasTextureAtLocation(manager, textureRes)) continue;
                var v = e.getValue();

                NativeImage newImage = null;

                try (NativeImage plankPalette = RPUtils.findFirstBlockTexture(manager, wood.plankBlock)) {
                    Palette targetPalette = SpriteUtils.extrapolateWoodItemPalette(plankPalette);
                    newImage = respriter.recolorImage(targetPalette);

                } catch (Exception ex) {
                    getLogger().error("Failed to generate Sign Post item texture for for {} : {}", v, ex);
                }

                if (newImage != null) {

                    DYNAMIC_TEXTURE_PACK.addTexture(textureRes, newImage);
                }
            }
        } catch (Exception ex) {
            getLogger().error("Could not generate any Sled entity texture : ", ex);
        }

        //item textures
        try (NativeImage template = readImage(manager, Christmas.res("textures/items/sleds/sled_oak.png"));
             NativeImage boatMask = readImage(manager, Christmas.res("textures/items/sleds/boat_mask.png"));
             NativeImage sledMask = readImage(manager, Christmas.res("textures/items/sleds/sled_mask.png"))) {

            Palette palette = Palette.fromImage(template, sledMask);
            Respriter respriter = new Respriter(template, palette);

            for (var e : ModRegistry.SLED_ITEMS.entrySet()) {
                WoodType wood = e.getKey();
                //if (wood.isVanilla()) continue;
                ResourceLocation textureRes = Christmas.res(
                        String.format("items/sleds/%s", wood.getVariantId("sled")));
                if (this.alreadyHasTextureAtLocation(manager, textureRes)) continue;
                var v = e.getValue();

                NativeImage newImage = null;
                if (wood.boatItem != null) {
                    try (NativeImage vanillaBoat = RPUtils.findFirstItemTexture(manager, wood.boatItem.get())) {

                        Palette targetPalette = Palette.fromImage(vanillaBoat, boatMask);
                        newImage = respriter.recolorImage(targetPalette);

                        /*
                        try (NativeImage scribbles = recolorFromVanilla(manager, vanillaBoat,
                                Christmas.res("textures/items/hanging_signs/sign_scribbles_mask.png"),
                                Christmas.res("textures/items/sign_posts/scribbles_template.png"));) {
                            SpriteUtils.mergeImages(newImage, scribbles);
                        } catch (Exception ex) {
                            getLogger().error("Could not properly color Sign Post item texture for {} : {}", v, ex);
                        }*/

                    } catch (Exception ex) {
                        getLogger().error("Could not find boat texture for wood type {}. Using plank texture : {}", wood, ex);
                    }
                }
                //if it failed use plank one
                if (newImage == null) {
                    try (NativeImage plankPalette = RPUtils.findFirstBlockTexture(manager, wood.plankBlock)) {
                        Palette targetPalette = SpriteUtils.extrapolateWoodItemPalette(plankPalette);
                        newImage = respriter.recolorImage(targetPalette);

                    } catch (Exception ex) {
                        getLogger().error("Failed to generate Sign Post item texture for for {} : {}", v, ex);
                    }
                }
                if (newImage != null) {

                    DYNAMIC_TEXTURE_PACK.addTexture(textureRes, newImage);
                }
            }
        } catch (Exception ex) {
            getLogger().error("Could not generate any Sleds item texture : ", ex);
        }
    }

}

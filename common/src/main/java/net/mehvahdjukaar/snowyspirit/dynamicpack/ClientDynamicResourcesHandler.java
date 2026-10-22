package net.mehvahdjukaar.snowyspirit.dynamicpack;

import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.misc.IProgressTracker;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicClientResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.SpriteUtils;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.mehvahdjukaar.moonlight.api.util.math.colors.HSVColor;
import net.mehvahdjukaar.snowyspirit.SnowySpirit;
import net.mehvahdjukaar.snowyspirit.reg.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.function.Consumer;

public class ClientDynamicResourcesHandler extends DynamicClientResourceProvider {

    public static final ClientDynamicResourcesHandler INSTANCE = new ClientDynamicResourcesHandler();

    public ClientDynamicResourcesHandler() {
        super(SnowySpirit.res("generated_pack"), PackGenerationStrategy.CACHED);
    }

    private static final Map<DyeColor, float[]> COLORS = new EnumMap<>(DyeColor.class);

    private static final List<DyeColor> BRIGHT_COLORS = Arrays.stream(DyeColor.values()).filter(c ->
            (c.ordinal() < 16 && c != DyeColor.BROWN && c != DyeColor.BLACK && c != DyeColor.GRAY && c != DyeColor.LIGHT_GRAY)).toList();


    public static float[] getGlowLightColor(DyeColor color, RandomSource randomSource) {
        if (color == null) {
            var c = new HSVColor(randomSource.nextFloat(), 1, 1f, 1).asRGB();
            return new float[]{c.red(), c.green(), c.blue()};
            //color = BRIGHT_COLORS.get(randomSource.nextInt(BRIGHT_COLORS.size()));
        }
        var arr = COLORS.get(color);
        if (arr == null) {
            throw new Error("Returned null color. How? " + color + " " + COLORS);
        }
        return arr;
    }

    public static int getGlowLightRgb(DyeColor color) {
        float[] c = COLORS.get(color);
        if (c == null) return color.getFireworkColor();
        return FastColor.ARGB32.colorFromFloat(1, c[0], c[1], c[2]) & 0xFFFFFF;
    }

    @Override
    public void reload(ResourceManager manager, IProgressTracker reporter) {
        super.reload(manager, reporter);

        try {
            var l = SpriteUtils.parsePaletteStrip(manager,
                    ResType.PARTICLE_TEXTURES.getPath(SnowySpirit.res("glow_lights_colors")),
                    DyeColor.values().length);
            var i = l.iterator();
            for (var d : DyeColor.values()) {
                if (i.hasNext()) {
                    addColor(d, i.next());
                } else {
                    //default for tinted
                    addColor(d, d.getFireworkColor());//fix tinted
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addColor(DyeColor d, int c) {
        int n = (c & 0xFF0000) >> 16;
        int o = (c & 0xFF00) >> 8;
        int p = (c & 0xFF);
        COLORS.put(d, new float[]{p / 255.0F, o / 255.0F, n / 255.0F});
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of(SnowySpirit.MOD_ID);
    }

    @Override
    protected void regenerateDynamicAssets(Consumer<ResourceGenTask> consumer) {
        consumer.accept(this::regenerateDynamicAssets);
    }

    private void regenerateDynamicAssets(ResourceManager manager, ResourceSink sink) {
        StaticResource itemModel = StaticResource.getOrLog(manager,
                ResType.ITEM_MODELS.getPath(SnowySpirit.res("sled_oak")));

        ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {

            try {
                sink.addSimilarJsonResource(manager, itemModel, "sled_oak", wood.getVariantId("sled"));
            } catch (Exception ex) {
                SnowySpirit.LOGGER.error("Failed to generate Sled item model for {} : {}", sled, ex);
            }
        });

        //entity textures
        try (TextureImage template = TextureImage.open(manager, SnowySpirit.res("entity/sled/oak"))) {

            Respriter respriter = Respriter.of(template);

            ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {

                ResourceLocation textureRes = SnowySpirit.res("entity/sled/" + wood.getTexturePath());
                if (sink.alreadyHasTextureAtLocation(manager, textureRes)) return;


                try (TextureImage plankTexture = TextureImage.open(manager,
                        RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {
                    //Palette targetPalette = SpriteUtils.extrapolateWoodItemPalette(plankTexture);
                    var targetPalette = Palette.fromImage(plankTexture);
                    TextureImage newImage = respriter.recolor(targetPalette);
                    //TextureImage newImage = respriter.recolorWithAnimationOf(plankTexture);
                    sink.addTexture(textureRes, newImage);

                } catch (Exception ex) {
                    SnowySpirit.LOGGER.error("Failed to generate sled entity texture for for {} : {}", sled, ex);
                }
            });
        } catch (Exception ex) {
            SnowySpirit.LOGGER.error("Could not generate any sled entity texture : ", ex);
        }

        //item textures
        try (TextureImage template = TextureImage.open(manager, SnowySpirit.res("item/sleds/sled_oak"));
             TextureImage boatMask = TextureImage.open(manager, SnowySpirit.res("item/sleds/boat_mask"));
             TextureImage sledMask = TextureImage.open(manager, SnowySpirit.res("item/sleds/sled_mask"))) {

            Palette palette = Palette.fromImage(template, sledMask);
            Respriter respriter = Respriter.ofPalette(template, palette);

            ModRegistry.SLED_ITEMS.forEach((wood, sled) -> {
                //if (wood.isVanilla()) continue;
                ResourceLocation textureRes = SnowySpirit.res("item/sleds/" + Utils.getID(sled).getPath());
                if (sink.alreadyHasTextureAtLocation(manager, textureRes)) return;

                TextureImage newImage = null;
                Item boat = wood.getItemOfThis("boat");
                if (boat != null) {
                    try (TextureImage vanillaBoat = TextureImage.open(manager,
                            RPUtils.findFirstItemTextureLocation(manager, boat))) {

                        Palette targetPalette = Palette.fromImage(vanillaBoat, boatMask);
                        newImage = respriter.recolor(targetPalette);

                    } catch (Exception ex) {
                        SnowySpirit.LOGGER.warn("Could not find boat texture for wood type {}. Using plank texture : {}", wood, ex);
                    }
                }
                //if it failed use plank one
                if (newImage == null) {
                    try (TextureImage plankPalette = TextureImage.open(manager,
                            RPUtils.findFirstBlockTextureLocation(manager, wood.planks))) {
                        Palette targetPalette = SpriteUtils.extrapolateWoodItemPalette(plankPalette);
                        newImage = respriter.recolor(targetPalette);

                    } catch (Exception ex) {
                        SnowySpirit.LOGGER.error("Failed to generate sled item texture for for {} : {}", sled, ex);
                    }
                }
                if (newImage != null) {
                    sink.addTexture(textureRes, newImage);
                }
            });
        } catch (Exception ex) {
            SnowySpirit.LOGGER.error("Could not generate any Sleds item texture : ", ex);
        }
    }

    @Override
    public void addDynamicTranslations(AfterLanguageLoadEvent lang) {
        ModRegistry.SLED_ITEMS.forEach((wood, sled) -> LangBuilder.addDynamicEntry(lang, "item.snowyspirit.sled", wood, sled));
    }
}

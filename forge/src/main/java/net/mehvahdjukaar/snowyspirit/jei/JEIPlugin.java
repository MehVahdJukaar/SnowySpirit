package net.mehvahdjukaar.randomium.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.mehvahdjukaar.randomium.Randomium;
import net.mehvahdjukaar.randomium.forge.RandomiumForge;
import net.mehvahdjukaar.randomium.recipes.RandomiumDuplicateRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = Randomium.res("jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(RandomiumDuplicateRecipe.class, DuplicateRecipeExtension::new);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RecipeTypes.CRAFTING, createRandomiumDuplicateRecipe());
    }

    public static List<CraftingRecipe> createRandomiumDuplicateRecipe() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        String group = "randomium.jei.duplicate";

        Ingredient randomium = Ingredient.of(Randomium.RANDOMIUM_ITEM.get().getDefaultInstance());
        Ingredient in = Ingredient.of(new ItemStack(RandomiumForge.DUPLICATE_ITEM.get()));
        NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, randomium, in);
        ResourceLocation id = Randomium.res("duplicate");
        ShapelessRecipe recipe = new ShapelessRecipe(id, group, new ItemStack(RandomiumForge.DUPLICATE_ITEM.get()), inputs);
        recipes.add(recipe);

        return recipes;
    }


}
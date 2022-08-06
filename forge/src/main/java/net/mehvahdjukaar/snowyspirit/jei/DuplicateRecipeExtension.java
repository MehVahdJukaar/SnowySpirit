package net.mehvahdjukaar.randomium.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.mehvahdjukaar.randomium.recipes.RandomiumDuplicateRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

public class DuplicateRecipeExtension implements ICraftingCategoryExtension {
    private final RandomiumDuplicateRecipe recipe;

    public DuplicateRecipeExtension(RandomiumDuplicateRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, PoseStack poseStack, double mouseX, double mouseY) {
        Minecraft.getInstance().font.draw(poseStack, I18n.get("randomium.jei.duplicate"), 60.0F, 46.0F, 5592405);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ICraftingGridHelper iCraftingGridHelper, IFocusGroup iFocusGroup) {

    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.recipe.getId();
    }
}
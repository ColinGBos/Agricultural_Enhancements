package vapourdrive.agricultural_enhancements.integrations.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class SeedRecipeCategory implements IRecipeCategory<SeedRecipeWrapper> {
    private final IDrawable background;
    private final IDrawable slot;
    private final IDrawable icon;

//    private final IDrawableStatic block;
    public SeedRecipeCategory(IGuiHelper guiHelper) {
        background = guiHelper.createBlankDrawable(120, 18);
        slot = guiHelper.getSlotDrawable();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registration.CROP_MANAGER_ITEM.get()));
    }

    @Override
    public @NotNull RecipeType<SeedRecipeWrapper> getRecipeType() {
        return JEI_Plugin.SEEDS;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("agriculturalenhancements.crop_manager");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SeedRecipeWrapper recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getSeedIngredient());
    }

    @Override
    public void draw(@NotNull SeedRecipeWrapper recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack stack, double mouseX, double mouseY) {
        slot.draw(stack);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.font.draw(stack, recipe.getBlockName(), 22,5, 0xFF808080);
    }
}

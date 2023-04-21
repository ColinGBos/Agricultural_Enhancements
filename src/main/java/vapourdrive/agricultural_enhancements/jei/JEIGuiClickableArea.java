package vapourdrive.agricultural_enhancements.jei;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JEIGuiClickableArea implements IGuiClickableArea {
    Rect2i area;
    List<RecipeType<?>> recipeTypesList;
    List<Component> hoveringText;
    public JEIGuiClickableArea(int xPos, int yPos, int width, int height, List<Component> hoverComponents ,RecipeType<?>... recipeTypes){
        area = new Rect2i(xPos, yPos, width, height);
        recipeTypesList = Arrays.asList(recipeTypes);
        hoveringText = hoverComponents;
    }

    @Override
    public @NotNull Rect2i getArea() {
        return area;
    }

    @Override
    public @NotNull List<Component> getTooltipStrings() {
        return hoveringText;
    }

    @Override
    public void onClick(@NotNull IFocusFactory focusFactory, @NotNull IRecipesGui recipesGui) {
        recipesGui.showTypes(recipeTypesList);
    }
}

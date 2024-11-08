package vapourdrive.agricultural_enhancements.content.fertilizer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Optional;

public class FertilizerUtils {
    public static int[] getFertilizerResultForItem(Level world, ItemStack itemStack) {
        Optional<RecipeHolder<FertilizerRecipe>> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        return matchingRecipe
            .map(recipe -> recipe.value().getOutputs())
            .orElse(null);
    }

    public static Optional<RecipeHolder<FertilizerRecipe>> getMatchingRecipeForInput(Level world, ItemStack itemStack) {
        RecipeManager recipeManager = world.getRecipeManager();
        return recipeManager.getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SingleRecipeInput(itemStack), world);
    }
}

package vapourdrive.agricultural_enhancements.content.fertilizer;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Optional;

public class FertilizerUtils {
    public static int[] getFertilizerResultForItem(Level world, ItemStack itemStack) {
        Optional<FertilizerRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        return matchingRecipe.map(FertilizerRecipe::getOutputs).orElse(null);
    }

    public static Optional<FertilizerRecipe> getMatchingRecipeForInput(Level world, ItemStack itemStack) {
        RecipeManager recipeManager = world.getRecipeManager();
        SimpleContainer singleItemInventory = new SimpleContainer(itemStack);
        return recipeManager.getRecipeFor(Registration.FERTILIZER_TYPE.get(), singleItemInventory, world);
    }
}

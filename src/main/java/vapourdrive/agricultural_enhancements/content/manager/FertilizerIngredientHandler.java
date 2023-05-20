package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.IngredientHandler;

import javax.annotation.Nonnull;

public class FertilizerIngredientHandler extends IngredientHandler {
    public FertilizerIngredientHandler(AbstractBaseFuelUserTile tile, int size) {
        super(tile, size);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        AgriculturalEnhancements.debugLog("Why isn't the fertilizer ingredientHandler working...");
        return true;
    }
}

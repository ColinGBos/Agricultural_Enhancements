package vapourdrive.agricultural_enhancements.modules.manager;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.IngredientHandler;
import vapourdrive.agricultural_enhancements.setup.Registration;

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

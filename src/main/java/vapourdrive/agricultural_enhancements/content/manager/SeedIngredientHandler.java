package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.AbstractBaseFuelUserTile;
import vapourdrive.vapourware.shared.base.itemhandlers.IngredientHandler;

import javax.annotation.Nonnull;

public class SeedIngredientHandler extends IngredientHandler {
    public SeedIngredientHandler(AbstractBaseFuelUserTile tile, int size) {
        super(tile, size);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        AgriculturalEnhancements.debugLog("Why is the seed handler working?...");
        return AgriculturalEnhancements.seeds.contains(stack.getItem());
    }
}

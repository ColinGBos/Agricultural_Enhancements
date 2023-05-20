package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.world.item.ItemStack;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.IngredientHandler;

import javax.annotation.Nonnull;

public class HarvesterIngredientHandler extends IngredientHandler {
    public HarvesterIngredientHandler(AbstractBaseFuelUserTile tile, int size) {
        super(tile, size);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }
}

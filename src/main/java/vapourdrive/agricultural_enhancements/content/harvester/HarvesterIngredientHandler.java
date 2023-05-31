package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.world.item.ItemStack;
import vapourdrive.vapourware.shared.base.AbstractBaseFuelUserTile;
import vapourdrive.vapourware.shared.base.itemhandlers.IngredientHandler;

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

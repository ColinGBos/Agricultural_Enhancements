package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseFuelUserTile;
import vapourdrive.vapourware.shared.base.itemhandlers.IngredientHandler;

import javax.annotation.Nonnull;

public class FertilizerIngredientHandler extends IngredientHandler {
    public FertilizerIngredientHandler(AbstractBaseFuelUserTile tile, int size) {
        super(tile, size);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.is(Registration.FERTILIZER.get());
    }
}

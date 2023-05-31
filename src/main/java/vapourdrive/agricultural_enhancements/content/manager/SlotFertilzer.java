package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.slots.BaseSlotIngredient;

public class SlotFertilzer extends BaseSlotIngredient {
    public SlotFertilzer(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, "agriculturalenhancements.fertilizerslot");
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return stack.is(Registration.FERTILISER.get());
    }
}

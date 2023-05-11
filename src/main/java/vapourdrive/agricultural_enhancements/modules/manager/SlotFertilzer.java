package vapourdrive.agricultural_enhancements.modules.manager;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.modules.base.slots.BaseSlotIngredient;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class SlotFertilzer extends BaseSlotIngredient {
    public SlotFertilzer(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition, world, "agriculturalenhancements.fertilizerslot");
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return stack.is(Registration.FERTILISER.get());
    }
}

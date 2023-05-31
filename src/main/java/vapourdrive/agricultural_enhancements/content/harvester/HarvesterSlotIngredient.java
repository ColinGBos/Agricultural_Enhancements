package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.vapourware.shared.base.slots.BaseSlotIngredient;

public class HarvesterSlotIngredient extends BaseSlotIngredient {
    public HarvesterSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, "agriculturalenhancements.toolslot");
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }
}

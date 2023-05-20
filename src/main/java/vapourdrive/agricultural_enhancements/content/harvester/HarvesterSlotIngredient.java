package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.content.base.slots.BaseSlotIngredient;

public class HarvesterSlotIngredient extends BaseSlotIngredient {
    public HarvesterSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition, world, "agriculturalenhancements.toolslot");
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return stack.getMaxStackSize() == 1;
    }
}

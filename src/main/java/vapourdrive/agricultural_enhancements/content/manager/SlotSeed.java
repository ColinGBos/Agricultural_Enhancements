package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.content.base.slots.BaseSlotIngredient;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.seeds;

public class SlotSeed extends BaseSlotIngredient {
    public SlotSeed(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition, world, "agriculturalenhancements.seedslot");
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return seeds.contains(stack.getItem());
    }
}

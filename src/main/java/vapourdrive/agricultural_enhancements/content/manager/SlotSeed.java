package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.slots.BaseSlotIngredient;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.seeds;

public class SlotSeed extends BaseSlotIngredient {
    public SlotSeed(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, new DeferredComponent(AgriculturalEnhancements.MODID, "seedslot"));
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return seeds.contains(stack.getItem());
    }
}

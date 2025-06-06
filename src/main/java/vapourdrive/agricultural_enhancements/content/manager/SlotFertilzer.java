package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.slots.BaseSlotIngredient;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

public class SlotFertilzer extends BaseSlotIngredient {
    public SlotFertilzer(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, new DeferredComponent(AgriculturalEnhancements.MODID, "fertilizerslot"));
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        return stack.is(Registration.FERTILIZER.get());
    }
}

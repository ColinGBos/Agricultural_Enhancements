package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.slots.BaseSlotIngredient;

public class FertilizerSlotIngredient extends BaseSlotIngredient {
    final Level world;

    public FertilizerSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition);
        this.world = world;
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
//        return true;
        AgriculturalEnhancements.debugLog("within the slot call");
        return this.world.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SingleRecipeInput(stack), this.world).isPresent();
    }
}

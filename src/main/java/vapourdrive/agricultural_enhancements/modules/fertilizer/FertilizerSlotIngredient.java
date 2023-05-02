package vapourdrive.agricultural_enhancements.modules.fertilizer;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.slots.BaseSlotIngredient;
import vapourdrive.agricultural_enhancements.setup.Registration;

public class FertilizerSlotIngredient extends BaseSlotIngredient {
    public FertilizerSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition, world);
    }

    @Override
    protected boolean isValidIngredient(ItemStack stack) {
        AgriculturalEnhancements.debugLog("calling ingredient check");
        boolean ret = this.world.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SimpleContainer(stack), this.world).isPresent();
        AgriculturalEnhancements.debugLog("Slot Ingredient check: "+ret);
        return ret;
    }
}

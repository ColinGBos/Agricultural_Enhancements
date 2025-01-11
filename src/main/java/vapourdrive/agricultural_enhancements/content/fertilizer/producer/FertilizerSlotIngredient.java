package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
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
        AgriculturalEnhancements.debugLog("within the ingredient valid call");
        return this.world.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SimpleContainer(stack), this.world).isPresent();
    }
}

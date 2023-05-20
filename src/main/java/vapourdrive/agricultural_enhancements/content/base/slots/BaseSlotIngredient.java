package vapourdrive.agricultural_enhancements.content.base.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import javax.annotation.Nonnull;

public class BaseSlotIngredient extends AbstractMachineSlot {
    protected final IItemHandler itemHandler;
    private final int index;
    protected final Level world;

    public BaseSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition, "agriculturalenhancements.ingredientslot");
        this.itemHandler = itemHandler;
        this.index = index;
        this.world = world;
    }

    public BaseSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world, String title) {
        super(itemHandler, index, xPosition, yPosition, title);
        this.itemHandler = itemHandler;
        this.index = index;
        this.world = world;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !this.isValidIngredient(stack)) {
            AgriculturalEnhancements.debugLog("Returning false from parent class");
            return false;
        }
        boolean ret = itemHandler.isItemValid(index, stack);
        AgriculturalEnhancements.debugLog("Itemhandler says: " + ret);
        return ret;
    }


    protected boolean isValidIngredient(ItemStack stack) {
        return false;
//        return this.world.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SimpleContainer(stack), this.world).isPresent();
    }
}

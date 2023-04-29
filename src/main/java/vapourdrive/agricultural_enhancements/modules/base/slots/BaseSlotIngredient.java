package vapourdrive.agricultural_enhancements.modules.base.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class BaseSlotIngredient extends AbstractMachineSlot{
    private final IItemHandler itemHandler;
    private final int index;
    protected final Level world;

    public BaseSlotIngredient(IItemHandler itemHandler, int index, int xPosition, int yPosition, Level world) {
        super(itemHandler, index, xPosition, yPosition, "message.furnacemk2.ingredientslot");
        this.itemHandler = itemHandler;
        this.index = index;
        this.world = world;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || !this.isValidIngredient(stack))
            return false;
        return itemHandler.isItemValid(index, stack);
    }


    protected boolean isValidIngredient(ItemStack stack) {
        return false;
//        return this.world.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SimpleContainer(stack), this.world).isPresent();
    }
}

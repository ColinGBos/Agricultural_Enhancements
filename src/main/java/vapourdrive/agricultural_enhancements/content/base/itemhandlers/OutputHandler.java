package vapourdrive.agricultural_enhancements.content.base.itemhandlers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import javax.annotation.Nonnull;

public class OutputHandler extends ItemStackHandler {
    BlockEntity tile;

    public OutputHandler(BlockEntity tile, int size) {
        this.tile = tile;
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    protected void onContentsChanged(int slot) {
        // To make sure the TE persists when the chunk is saved later we need to
        // mark it dirty every time the item handler changes
        tile.setChanged();
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean internal) {
        //processes the stack if it's an 'internal' inquiry
        return internal ? internalInsertItem(slot, stack, simulate) : stack;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        //'external' attempts to insert stacks should be unsuccessful
        AgriculturalEnhancements.debugLog("Insert Item");
        if (AgriculturalEnhancements.debugMode) {
            AgriculturalEnhancements.debugLog("trying to debug insert into output.");
            return internalInsertItem(slot, stack, simulate);
        }
        return insertItem(slot, stack, simulate, false);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    public ItemStack internalInsertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    public boolean isFull() {
        for (int i = 0; i < this.getSlots(); i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack.getCount() < this.getStackLimit(i, stack)) {
                return false;
            }
        }
        return true;
    }
}

package vapourdrive.agricultural_enhancements.modules.base.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class AbstractMachineSlot extends SlotItemHandler {
    public final String slotTitle;

    public AbstractMachineSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, String slotTitle) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotTitle = slotTitle;
    }

    public String getTitle() {
        return this.slotTitle;
    }
}

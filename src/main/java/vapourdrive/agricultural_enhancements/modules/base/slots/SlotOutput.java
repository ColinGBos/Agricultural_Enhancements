package vapourdrive.agricultural_enhancements.modules.base.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import javax.annotation.Nonnull;

public class SlotOutput extends AbstractMachineSlot {

    public SlotOutput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, "agriculturalenhancements.outputslot");
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return AgriculturalEnhancements.debugMode;
    }
}
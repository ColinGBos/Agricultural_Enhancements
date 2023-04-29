package vapourdrive.agricultural_enhancements.modules.base;

import net.minecraft.world.item.ItemStack;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

public interface IFuelUser {
    int getMaxFuel();

    int getMinFuelToWork();

    int getCurrentFuel();

    int getCurrentBurn();

    void setCurrentBurn(int burn);

    int getIncrementalFuelToAdd();

    void setIncrementalFuelToAdd(int increment);

    int getFuelToAdd();

    void setFuelToAdd(int fuel);

    boolean addFuel(int toAdd, boolean simulate);

    boolean consumeFuel(int toConsume, boolean simulate);

    boolean canWork();

    ItemStack getCurrentFuelStack();

    void setCurrentFuelStack(ItemStack stack);

    double getEfficiencyMultiplier();

    void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate);

    ItemStack getStackInSlot(MachineUtils.Area area, int index);

    ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate);

    int[] getOutputSlots();


}

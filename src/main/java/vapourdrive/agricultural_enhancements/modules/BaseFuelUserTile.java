package vapourdrive.agricultural_enhancements.modules;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseFuelUserTile extends BlockEntity implements IFuelUser {

    public final int maxFuel;
    public final int minWorkFuel;
    public int wait = 0;

    public int toAdd = 0;
    public int increment = 0;

    private ItemStack currentFuelStack = ItemStack.EMPTY;
    private int currentBurn = 0;

    public final int[] FUEL_SLOT = {0};

    public int[] OUTPUT_SLOTS;
    public BaseFuelUserTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int maxFuel, int minWorkFuel, int[] OUTPUT_SLOTS) {
        super(pType, pPos, pBlockState);
        this.maxFuel = maxFuel;
        this.minWorkFuel = minWorkFuel;
        this.OUTPUT_SLOTS = OUTPUT_SLOTS;
    }

    @Override
    public int getMaxFuel() {
        return this.maxFuel;
    }

    @Override
    public int getMinFuelToWork() {
        return this.minWorkFuel;
    }

    @Override
    public int getCurrentFuel() {
        return 0;
    }

    @Override
    public int getCurrentBurn() {
        return this.currentBurn;
    }

    @Override
    public void setCurrentBurn(int burn) {

    }

    @Override
    public int getIncrementalFuelToAdd() {
        return this.increment;
    }

    @Override
    public void setIncrementalFuelToAdd(int increment) {
        this.increment = increment;

    }

    @Override
    public int getFuelToAdd() {
        return this.toAdd;
    }

    @Override
    public void setFuelToAdd(int toAdd) {
        this.toAdd = toAdd;

    }

    @Override
    public boolean addFuel(int toAdd, boolean simulate) {
        return false;
    }

    @Override
    public boolean consumeFuel(int toConsume, boolean simulate) {
        return false;
    }

    @Override
    public boolean canWork() {
        return false;
    }

    @Override
    public ItemStack getCurrentFuelStack() {
        return this.currentFuelStack;
    }

    @Override
    public void setCurrentFuelStack(ItemStack stack) {
        this.currentFuelStack = stack;
    }

    @Override
    public int[] getOutputSlots(){
        return this.OUTPUT_SLOTS;
    }
}

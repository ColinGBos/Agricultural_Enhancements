package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.modules.irrigation.IIrrigationBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static vapourdrive.agricultural_enhancements.setup.Registration.IRRIGATION_CONTROLLER_TILE;

public class IrrigationControllerTile extends AbstractBaseFuelUserTile {

    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, outputHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

    public final IrrigationControllerData machineData = new IrrigationControllerData();

    public IrrigationControllerTile(BlockPos pos, BlockState state) {
        super(IRRIGATION_CONTROLLER_TILE.get(), pos, state, 3200000, 1000, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});
    }

    public void tickServer(BlockState state) {
        ItemStack fuel = getStackInSlot(MachineUtils.Area.FUEL, 0);
        MachineUtils.doFuelProcess(fuel, wait, this);
        if (wait % 80 == 0) {
            doWorkProcesses(state);
        }
        wait += 1;
        if (wait >= 160) {
            wait = 0;
        }
    }

    private void doWorkProcesses(BlockState state) {
        int target = 0;
        if (canWork()) {
            target = 15;
            consumeFuel(getMinFuelToWork(), false);
        }
        changeSurroundingBlocks(state, target);
        changeStateIfNecessary(state, target > 0);
    }

    public void changeSurroundingBlocks(BlockState state, int target) {
        Direction direction_rear = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        Direction direction_up = Direction.UP;
        Direction[] directions = {direction_rear, direction_up};
        assert this.level != null;
        for (Direction direction : directions) {
            BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction));
            if (targetState.getBlock() instanceof IIrrigationBlock pipe) {
                if (targetState.getValue(IrrigationPipeBlock.IRRIGATION) != target) {
                    BlockPos pos = this.worldPosition.relative(direction);
                    level.setBlockAndUpdate(pos, targetState.setValue(IrrigationPipeBlock.IRRIGATION, target));
                    if (target == 15) {
                        pipe.bringNeighboursUp(direction.getOpposite(), level, pos, 15);
                    } else {
                        pipe.bringNeighboursDown(direction.getOpposite(), level, pos, 15, this.worldPosition);
                    }
                }
                //                AgriculturalEnhancements.debugLog(""+targetState);
            }
        }
    }

    @Override
    public boolean canWork() {
        assert this.level != null;
//        AgriculturalEnhancements.debugLog("brightness: " + this.level.getRawBrightness(this.worldPosition.above(), 0));
        if (this.level.getRawBrightness(this.worldPosition.above(), 0) < 9) {
            return false;
        }
        BlockState belowState = this.level.getBlockState(this.worldPosition.below());
        if (belowState.getFluidState().isSourceOfType(Fluids.WATER)) {
            return getCurrentFuel() >= getMinFuelToWork();
        }
        return false;
    }

    @Override
    public void load(CompoundTag tag) {
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));
        outputHandler.deserializeNBT(tag.getCompound("invOut"));
        machineData.set(IrrigationControllerData.Data.FUEL, tag.getInt("fuel"));
        increment = tag.getInt("increment");
        toAdd = tag.getInt("toAdd");
        wait = tag.getInt("wait");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("invFuel", fuelHandler.serializeNBT());
        tag.put("invOut", outputHandler.serializeNBT());
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("increment", increment);
        tag.putInt("toAdd", toAdd);
        tag.putInt("wait", wait);

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return combinedHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    public IItemHandler getItemHandler() {
        return combined;
    }

    public double getEfficiencyMultiplier() {
        return ConfigSettings.FURNACE_BASE_EFFICIENCY.get();
    }

    @Override
    public int getCurrentFuel() {
        return machineData.get(IrrigationControllerData.Data.FUEL);
    }

    @Override
    public boolean addFuel(int toAdd, boolean simulate) {
        if (toAdd + getCurrentFuel() > getMaxFuel()) {
            return false;
        }
        if (!simulate) {
            machineData.set(IrrigationControllerData.Data.FUEL, getCurrentFuel() + toAdd);
        }

        return true;
    }

    @Override
    public boolean consumeFuel(int toConsume, boolean simulate) {
        if (getCurrentFuel() < toConsume) {
            return false;
        }
        if (!simulate) {
            machineData.set(IrrigationControllerData.Data.FUEL, getCurrentFuel() - toConsume);
        }
        return true;
    }

    public IrrigationControllerData getMachineData() {
        return machineData;
    }

    @Override
    public ItemStack getStackInSlot(MachineUtils.Area area, int index) {
        return switch (area) {
            case FUEL -> fuelHandler.getStackInSlot(FUEL_SLOT[index]);
            case OUTPUT -> outputHandler.getStackInSlot(OUTPUT_SLOTS[index]);
            case INGREDIENT, INGREDIENT_2 -> ItemStack.EMPTY;
        };
    }

    @Override
    public void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate) {
        switch (area) {
            case FUEL -> fuelHandler.extractItem(FUEL_SLOT[index], amount, simulate);
            case OUTPUT -> outputHandler.extractItem(OUTPUT_SLOTS[index], amount, simulate);
        }
    }

    @Override
    public ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate) {
        return switch (area) {
            case FUEL -> fuelHandler.insertItem(FUEL_SLOT[index], stack, simulate);
            case OUTPUT -> outputHandler.insertItem(OUTPUT_SLOTS[index], stack, simulate, true);
            case INGREDIENT, INGREDIENT_2 -> ItemStack.EMPTY;
        };
    }
}

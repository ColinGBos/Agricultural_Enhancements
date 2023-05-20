package vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller;

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
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.content.irrigation.IIrrigationBlock;
import vapourdrive.agricultural_enhancements.content.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static vapourdrive.agricultural_enhancements.setup.Registration.IRRIGATION_CONTROLLER_TILE;

public class IrrigationControllerTile extends AbstractBaseFuelUserTile {

    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, outputHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

    public final IrrigationControllerData machineData = new IrrigationControllerData();
    private int irrigateTimer = 0;
    private int consumeFuelTimer = 0;

    public IrrigationControllerTile(BlockPos pos, BlockState state) {
        super(IRRIGATION_CONTROLLER_TILE.get(), pos, state, ConfigSettings.IRRIGATION_CONTROLLER_FUEL_STORAGE.get() * 100, ConfigSettings.IRRIGATION_CONTROLLER_FUEL_TO_WORK.get(), new int[]{0, 1, 2, 3, 4});
    }

    public void tickServer(BlockState state) {
        super.tickServer(state);
        if (irrigateTimer == ConfigSettings.IRRIGATION_CONTROLLER_PROCESS_TIME.get()) {
            doWorkProcesses(state);
            irrigateTimer = 0;
        }
        irrigateTimer++;
        if (consumeFuelTimer >= 20) {
//            AgriculturalEnhancements.debugLog("consume fuel for the irrigation controller");
            if (canWork(state)) {
                consumeFuel(getMinFuelToWork(), false);
            }
            consumeFuelTimer = 0;
        }
        consumeFuelTimer++;
    }

    private void doWorkProcesses(BlockState state) {
        int target = 0;
        if (canWork(state)) {
            target = 15;
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
    public boolean canWork(BlockState state) {
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
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));
        outputHandler.deserializeNBT(tag.getCompound("invOut"));
        machineData.set(IrrigationControllerData.Data.FUEL, tag.getInt("fuel"));
        irrigateTimer = tag.getInt("irrigateTimer");
        consumeFuelTimer = tag.getInt("consumeFuelTimer");

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("invFuel", fuelHandler.serializeNBT());
        tag.put("invOut", outputHandler.serializeNBT());
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("irrigateTimer", irrigateTimer);
        tag.putInt("consumeFuelTimer", consumeFuelTimer);

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

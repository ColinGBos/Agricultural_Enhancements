package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.modules.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.modules.irrigation.IIrrigationBlock;
import vapourdrive.agricultural_enhancements.modules.irrigation.IrrigationPipeBlock;
import vapourdrive.agricultural_enhancements.modules.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.modules.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static vapourdrive.agricultural_enhancements.setup.Registration.HARVESTER_TILE;

public class IrrigationControllerTile extends AbstractBaseFuelUserTile {

    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final LazyOptional<FuelHandler> lazyFuelHandler = LazyOptional.of(()-> fuelHandler);

    public final IrrigationControllerData machineData = new IrrigationControllerData();

    public IrrigationControllerTile(BlockPos pos, BlockState state) {
        super(HARVESTER_TILE.get(), pos, state, 6400000, 500, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});
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
        Direction direction_facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction direction_up = Direction.UP;
        Direction[] directions = {direction_facing, direction_up};
        int target = 0;
        if (canWork()) {
            target = 15;
            consumeFuel(getMinFuelToWork(), false);
        }
        else{
            assert this.level != null;
            for(Direction direction:directions) {
                BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction));
                if (targetState.getBlock() instanceof IIrrigationBlock) {
                    if (targetState.getValue(IrrigationPipeBlock.IRRIGATION) != target) {
                        BlockPos pos = this.worldPosition.relative(direction);
                        level.setBlockAndUpdate(pos, targetState.setValue(IrrigationPipeBlock.IRRIGATION, target));
                    }
                    //                AgriculturalEnhancements.debugLog(""+targetState);
                }
            }
        }
    }

    public void changeSurroundingBlocks(BlockState state, int target){
        Direction direction_facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction direction_up = Direction.UP;
        Direction[] directions = {direction_facing, direction_up};
        assert this.level != null;
        for(Direction direction:directions) {
            BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction));
            if (targetState.getBlock() instanceof IIrrigationBlock) {
                if (targetState.getValue(IrrigationPipeBlock.IRRIGATION) != target) {
                    BlockPos pos = this.worldPosition.relative(direction);
                    level.setBlockAndUpdate(pos, targetState.setValue(IrrigationPipeBlock.IRRIGATION, target));
                }
                //                AgriculturalEnhancements.debugLog(""+targetState);
            }
        }
    }

    @Override
    public boolean canWork() {
        return getCurrentFuel() < getMinFuelToWork();
    }

    @Override
    public void load(CompoundTag tag) {
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));
        machineData.set(IrrigationControllerData.Data.FUEL, tag.getInt("fuel"));
        increment = tag.getInt("increment");
        toAdd = tag.getInt("toAdd");
        wait = tag.getInt("wait");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("invFuel", fuelHandler.serializeNBT());
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("increment", increment);
        tag.putInt("toAdd", toAdd);
        tag.putInt("wait", wait);

    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return lazyFuelHandler.cast();
        }
        return super.getCapability(capability, side);
    }

    public IItemHandler getItemHandler() {
        return fuelHandler;
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
        return fuelHandler.getStackInSlot(FUEL_SLOT[index]);
    }

    @Override
    public void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate) {
        if (Objects.requireNonNull(area) == MachineUtils.Area.FUEL) {
            fuelHandler.extractItem(FUEL_SLOT[index], amount, simulate);
        }
    }

    @Override
    public ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate) {
        return fuelHandler.insertItem(FUEL_SLOT[index], stack, simulate);
    }
}

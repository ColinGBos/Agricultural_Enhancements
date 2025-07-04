package vapourdrive.agricultural_enhancements.content.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.irrigation.IIrrigationBlock;
import vapourdrive.agricultural_enhancements.content.irrigation.IrrigationPipeBlock;
import vapourdrive.vapourware.shared.base.AbstractBaseFuelUserTile;
import vapourdrive.vapourware.shared.base.itemhandlers.FuelHandler;
import vapourdrive.vapourware.shared.base.itemhandlers.OutputHandler;
import vapourdrive.vapourware.shared.utils.MachineUtils;

import java.util.Objects;

import static vapourdrive.agricultural_enhancements.setup.Registration.IRRIGATION_CONTROLLER_TILE;

public class IrrigationControllerTile extends AbstractBaseFuelUserTile implements MenuProvider {

    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, outputHandler);
//    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

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
        if (this.level.getRawBrightness(this.worldPosition.above(), 0) < 9) {
            return false;
        } else if ((Objects.requireNonNull(this.getLevel())).hasNeighborSignal(this.worldPosition)) {
            return false;
        }
        BlockState belowState = this.level.getBlockState(this.worldPosition.below());
        if (!belowState.getFluidState().isSourceOfType(Fluids.WATER)) {
            return false;
        }
        Direction direction_rear = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
        BlockState rearState = this.level.getBlockState(this.worldPosition.relative(direction_rear));
        BlockState aboveState = this.level.getBlockState(this.worldPosition.above());
        if (rearState.getBlock() instanceof IIrrigationBlock || aboveState.getBlock() instanceof IIrrigationBlock) {
            return getCurrentFuel() >= getMinFuelToWork();
        }
        return false;
    }


    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        fuelHandler.deserializeNBT(registries, tag.getCompound("invFuel"));
        outputHandler.deserializeNBT(registries, tag.getCompound("invOut"));
        machineData.set(IrrigationControllerData.Data.FUEL, tag.getInt("fuel"));
        irrigateTimer = tag.getInt("irrigateTimer");
        consumeFuelTimer = tag.getInt("consumeFuelTimer");

    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("invFuel", fuelHandler.serializeNBT(registries));
        tag.put("invOut", outputHandler.serializeNBT(registries));
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("irrigateTimer", irrigateTimer);
        tag.putInt("consumeFuelTimer", consumeFuelTimer);

    }

    public IItemHandler getItemHandler(@Nullable Direction side) {
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
            default -> ItemStack.EMPTY;
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
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ContainerData getContainerData() {
        return this.getMachineData();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(AgriculturalEnhancements.MODID+".irrigation_controller");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new IrrigationControllerMenu(id, this.level, this.worldPosition, player.getInventory(), player, this.getMachineData());
    }
}

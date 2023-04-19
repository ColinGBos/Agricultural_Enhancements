package vapourdrive.agricultural_enhancements.modules.harvester;

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
import vapourdrive.agricultural_enhancements.modules.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.modules.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static vapourdrive.agricultural_enhancements.setup.Registration.HARVESTER_TILE;

public class HarvesterTile extends AbstractBaseFuelUserTile {

    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, outputHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

    public final HarvesterData harvesterData = new HarvesterData();

    public HarvesterTile(BlockPos pos, BlockState state) {
        super(HARVESTER_TILE.get(), pos, state, 6400000, 2500, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});
    }

    public void tickServer(BlockState state) {
        ItemStack fuel = getStackInSlot(MachineUtils.Area.FUEL, 0);
        MachineUtils.doFuelProcess(fuel, wait, this);
        if (wait % 20 == 0) {
            doWorkProcesses(state);
        }
        wait += 1;
        if (wait >= 160) {
            wait = 0;
        }
    }

    private void doWorkProcesses(BlockState state) {
        if (canWork()) {
            changeStateIfNecessary(state, true);
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
//            AgriculturalEnhancements.debugLog(""+direction);
            assert this.level != null;
            for (int i = 1; i <= 9; i++) {
                BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction, i));
                if (targetState.getBlock() instanceof CropBlock crop) {
                    if (crop.isMaxAge(targetState)) {
                        BlockPos pos = this.worldPosition.relative(direction, i);
                        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY);
                        List<ItemStack> drops = targetState.getDrops(lootcontext$builder);
//                        AgriculturalEnhancements.debugLog("Drops pre-clean: " + drops);
                        drops = MachineUtils.cleanItemStacks(drops);
//                        AgriculturalEnhancements.debugLog("Drops pre-cull: " + drops);
                        ItemStack seed = crop.getCloneItemStack(level, pos, targetState);
                        for (ItemStack drop : drops) {
                            if (ItemStack.isSame(drop, seed)) {
                                drop.shrink(1);
                                if (drop.isEmpty()) {
                                    drops.remove(drop);
                                }
                                break;
                            }
                        }
//                        AgriculturalEnhancements.debugLog("Drops post-cull: " + drops);
//                        AgriculturalEnhancements.debugLog("Seed: "+seed);

                        if (MachineUtils.canPushAllOutputs(drops, this)) {
                            for (ItemStack stack : drops) {
                                MachineUtils.pushOutput(stack, false, this);
                            }
//                            AgriculturalEnhancements.debugLog("Server Success");
                            MachineUtils.animate(level, pos, level.getRandom(), SoundEvents.CROP_BREAK, 0f);
                            level.setBlockAndUpdate(pos, targetState.setValue(crop.getAgeProperty(), 1));
                            consumeFuel(getMinFuelToWork(), false);
                        }
                    }
                }
//                AgriculturalEnhancements.debugLog(""+targetState);
            }
        } else {
            changeStateIfNecessary(state, false);
        }
    }

    @Override
    public boolean canWork() {
        if (getCurrentFuel() < getMinFuelToWork()) {
            return false;
        }
        return !outputHandler.isFull();
    }

    @Override
    public void load(CompoundTag tag) {
        outputHandler.deserializeNBT(tag.getCompound("invOut"));
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));

        harvesterData.set(HarvesterData.Data.FUEL, tag.getInt("fuel"));

        increment = tag.getInt("increment");
        toAdd = tag.getInt("toAdd");
        wait = tag.getInt("wait");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("invOut", outputHandler.serializeNBT());
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
            if (side == Direction.DOWN) {
                return lazyOutputHandler.cast();
            }
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
        return harvesterData.get(HarvesterData.Data.FUEL);
    }

    @Override
    public boolean addFuel(int toAdd, boolean simulate) {
        if (toAdd + getCurrentFuel() > getMaxFuel()) {
            return false;
        }
        if (!simulate) {
            harvesterData.set(HarvesterData.Data.FUEL, getCurrentFuel() + toAdd);
        }

        return true;
    }

    @Override
    public boolean consumeFuel(int toConsume, boolean simulate) {
        if (getCurrentFuel() < toConsume) {
            return false;
        }
        if (!simulate) {
            harvesterData.set(HarvesterData.Data.FUEL, getCurrentFuel() - toConsume);
        }
        return true;
    }

    public HarvesterData getHarvesterData() {
        return harvesterData;
    }

    @Override
    public ItemStack getStackInSlot(MachineUtils.Area area, int index) {
        return switch (area) {
            case FUEL -> fuelHandler.getStackInSlot(FUEL_SLOT[index]);
            case OUTPUT -> outputHandler.getStackInSlot(OUTPUT_SLOTS[index]);
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
        };
    }
}

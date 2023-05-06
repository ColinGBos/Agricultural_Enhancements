package vapourdrive.agricultural_enhancements.modules.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.modules.soil.TilledSoilBlock;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static vapourdrive.agricultural_enhancements.setup.Registration.CROP_MANAGER_TILE;

public class CropManagerTile extends AbstractBaseFuelUserTile {

    public final int[] FERTILIZER_SLOT = {0};
    public final int[] SEED_SLOTS = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14};
    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final FertilizerIngredientHandler fertilizerHandler = new FertilizerIngredientHandler(this, FERTILIZER_SLOT.length);
    private final SeedIngredientHandler seedHandler = new SeedIngredientHandler(this, SEED_SLOTS.length);
    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, fertilizerHandler, outputHandler, seedHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

    public final CropManagerData machineData = new CropManagerData();

    public int fertilizerToAdd = 0;
    public int incrementalFertilizerToAdd = 0;
    private final int maxFertilizer = 1280;

    public CropManagerTile(BlockPos pos, BlockState state) {
        super(CROP_MANAGER_TILE.get(), pos, state, 6400000, 2500, new int[]{0, 1, 2});
    }

    public void tickServer(BlockState state) {
        ItemStack fuel = getStackInSlot(MachineUtils.Area.FUEL, 0);
        MachineUtils.doFuelProcess(fuel, wait, this);
        ItemStack ingredient = getStackInSlot(MachineUtils.Area.INGREDIENT, 0);
        doConsumeProcess(ingredient);
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
                BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction, i).relative(Direction.DOWN,1));
                if (targetState.is(Registration.TILLED_SOIL_BLOCK.get())) {
                    int currentFert = targetState.getValue(TilledSoilBlock.SOIL_NUTRIENTS);
                    if (currentFert<TilledSoilBlock.MAX_NUTRIENTS) {
                        BlockPos pos = this.worldPosition.relative(direction, i).relative(Direction.DOWN,1);
                        if(consumeFertilizer(TilledSoilBlock.MAX_NUTRIENTS-currentFert, true)) {
                            level.setBlockAndUpdate(pos, targetState.setValue(TilledSoilBlock.SOIL_NUTRIENTS, TilledSoilBlock.MAX_NUTRIENTS));
                            consumeFertilizer(TilledSoilBlock.MAX_NUTRIENTS-currentFert, false);
                            consumeFuel(getMinFuelToWork()*TilledSoilBlock.MAX_NUTRIENTS-currentFert, false);
                        }
                    }
                }
//                AgriculturalEnhancements.debugLog(""+targetState);
            }
        } else {
            changeStateIfNecessary(state, false);
        }
    }

    public void doConsumeProcess(ItemStack stack) {
        if (wait %20==0 && consumeFuel(minWorkFuel, true)) {
            AgriculturalEnhancements.debugLog("Doing consume process "+stack);
//            AgriculturalEnhancements.debugLog("N: " + fertilizerProducerData.get(FertilizerProducerData.Data.N));
//            AgriculturalEnhancements.debugLog("P: " + fertilizerProducerData.get(FertilizerProducerData.Data.P));
//            AgriculturalEnhancements.debugLog("K: " + fertilizerProducerData.get(FertilizerProducerData.Data.K));
            int toAdd = tryConsumeStack(stack);
            if (toAdd > 0) {
//            AgriculturalEnhancements.debugLog("Doing fuel process");
                setFertilizerToAdd(toAdd);
                if (!addFertilizer(getFertilizerToAdd(), true)) {
                    setFertilizerToAdd(getMaxFertilizer() - getCurrentFertilizer());
                }
                setIncrementalFertilizerToAdd(getFertilizerToAdd());
            }
        }
        if (getFertilizerToAdd() > 0) {
            addFertilizer(getIncrementalFertilizerToAdd(), false);
            setFertilizerToAdd(getFertilizerToAdd() - getIncrementalFertilizerToAdd());
        }
    }

    public int tryConsumeStack(ItemStack stack) {
        if (!stack.isEmpty()) {
            removeFromSlot(MachineUtils.Area.INGREDIENT, 0, 1, false);
            return 5;
        }
        return 0;
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
        fertilizerHandler.deserializeNBT(tag.getCompound("invFert"));
        seedHandler.deserializeNBT(tag.getCompound("invSeeds"));
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));

        machineData.set(CropManagerData.Data.FUEL, tag.getInt("fuel"));
        machineData.set(CropManagerData.Data.FERTILIZER, tag.getInt("fertilizer"));

        increment = tag.getInt("increment");
        toAdd = tag.getInt("toAdd");
        wait = tag.getInt("wait");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("invOut", outputHandler.serializeNBT());
        tag.put("invFert", fertilizerHandler.serializeNBT());
        tag.put("invSeeds", seedHandler.serializeNBT());
        tag.put("invFuel", fuelHandler.serializeNBT());

        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("fertilizer", getCurrentFertilizer());
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
        return machineData.get(CropManagerData.Data.FUEL);
    }

    @Override
    public boolean addFuel(int toAdd, boolean simulate) {
        if (toAdd + getCurrentFuel() > getMaxFuel()) {
            return false;
        }
        if (!simulate) {
            machineData.set(CropManagerData.Data.FUEL, getCurrentFuel() + toAdd);
        }

        return true;
    }

    @Override
    public boolean consumeFuel(int toConsume, boolean simulate) {
        if (getCurrentFuel() < toConsume) {
            return false;
        }
        if (!simulate) {
            machineData.set(CropManagerData.Data.FUEL, getCurrentFuel() - toConsume);
        }
        return true;
    }

    public CropManagerData getMachineData() {
        return machineData;
    }

    @Override
    public ItemStack getStackInSlot(MachineUtils.Area area, int index) {
        return switch (area) {
            case FUEL -> fuelHandler.getStackInSlot(FUEL_SLOT[index]);
            case OUTPUT -> outputHandler.getStackInSlot(OUTPUT_SLOTS[index]);
            case INGREDIENT -> fertilizerHandler.getStackInSlot(FERTILIZER_SLOT[index]);
            case INGREDIENT_2 -> seedHandler.getStackInSlot(SEED_SLOTS[index]);
        };
    }

    @Override
    public void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate) {
        switch (area) {
            case FUEL -> fuelHandler.extractItem(FUEL_SLOT[index], amount, simulate);
            case OUTPUT -> outputHandler.extractItem(OUTPUT_SLOTS[index], amount, simulate);
            case INGREDIENT -> fertilizerHandler.extractItem(FERTILIZER_SLOT[index], amount, simulate);
            case INGREDIENT_2 -> seedHandler.extractItem(SEED_SLOTS[index], amount, simulate);
        }
    }

    @Override
    public ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate) {
        return switch (area) {
            case FUEL -> fuelHandler.insertItem(FUEL_SLOT[index], stack, simulate);
            case OUTPUT -> outputHandler.insertItem(OUTPUT_SLOTS[index], stack, simulate, true);
            case INGREDIENT -> fertilizerHandler.insertItem(FERTILIZER_SLOT[index], stack, simulate);
            case INGREDIENT_2 -> seedHandler.insertItem(SEED_SLOTS[index], stack, simulate);
        };
    }

    public int getMaxFertilizer() {
        return this.maxFertilizer;
    }

    public void setFertilizerToAdd(int toSet){
        this.fertilizerToAdd=toSet;
    }
    public int getFertilizerToAdd() {
        return this.fertilizerToAdd;
    }
    public void setIncrementalFertilizerToAdd(int increment) {
        this.incrementalFertilizerToAdd = increment;
    }
    public int getIncrementalFertilizerToAdd() {
        return this.incrementalFertilizerToAdd;
    }
    public int getCurrentFertilizer() {
        return machineData.get(CropManagerData.Data.FERTILIZER);
    }

    public boolean addFertilizer(int toAdd, boolean simulate) {
        if (toAdd + getCurrentFertilizer() > getMaxFertilizer()) {
//            AgriculturalEnhancements.debugLog("Can't add element: "+getCurrentElement(element));
            return false;
        }
        if (!simulate) {
//            AgriculturalEnhancements.debugLog("Adding element + : "+toAdd+" "+getCurrentElement(element));
            machineData.set(CropManagerData.Data.FERTILIZER, getCurrentFertilizer() + toAdd);
        }

        return true;
    }

    public boolean consumeFertilizer(int toConsume, boolean simulate) {
        if (getCurrentFertilizer() < toConsume) {
            return false;
        }
        if (!simulate) {
            machineData.set(CropManagerData.Data.FERTILIZER, getCurrentFertilizer() - toConsume);
        }
        return true;
    }
}

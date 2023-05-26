package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.content.soil.HoeTilledToSoilHandler;
import vapourdrive.agricultural_enhancements.content.soil.TilledSoilBlock;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

import static vapourdrive.agricultural_enhancements.setup.Registration.CROP_MANAGER_TILE;

public class CropManagerTile extends AbstractBaseFuelUserTile {

    public final int[] FERTILIZER_SLOT = {0};
    public final int[] SEED_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
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
    private int soilTimer = 0;
    private int plantTimer = 0;

    private final ArrayList<Integer> blocks = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

    public CropManagerTile(BlockPos pos, BlockState state) {
        super(CROP_MANAGER_TILE.get(), pos, state, ConfigSettings.CROP_MANAGER_FUEL_STORAGE.get() * 100, ConfigSettings.CROP_MANAGER_FUEL_TO_WORK.get(), new int[]{0, 1, 2});
    }

    public void tickServer(BlockState state) {
        super.tickServer(state);
        ItemStack ingredient = getStackInSlot(MachineUtils.Area.INGREDIENT, 0);
        doConsumeProcess(ingredient);
        if (soilTimer == ConfigSettings.CROP_MANAGER_SOIL_PROCESS_TIME.get()) {
            doNutrientWorkProcesses(state);
            soilTimer = 0;
        }
        if (plantTimer == ConfigSettings.CROP_MANAGER_CROP_PROCESS_TIME.get()) {
            doPlantWorkProcesses(state);
            plantTimer = 0;
        }
        soilTimer++;
        plantTimer++;
    }

    private void doTillProcesses(BlockState state) {
        if (canWork(state)) {
            AgriculturalEnhancements.debugLog("Can work, tilling soil");
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
//            AgriculturalEnhancements.debugLog(""+direction);
            assert this.level != null;
            for (int i = 1; i <= 9; i++) {
                BlockState soilState = this.level.getBlockState(this.worldPosition.relative(direction, i).below());
                BlockState cropState = this.level.getBlockState(this.worldPosition.relative(direction, i));
                BlockPos soilPos = this.worldPosition.relative(direction, i).below();
                BlockPos cropPos = soilPos.above();
                if (!cropState.getMaterial().isReplaceable()) {
                    AgriculturalEnhancements.debugLog(cropState + " is not replacable");
                    continue;
                }
                if (HoeTilledToSoilHandler.cannotTill(soilState.getBlock(), soilPos, level)) {
                    continue;
                }
                if (!cropState.isAir()) {
                    level.setBlockAndUpdate(cropPos, Blocks.AIR.defaultBlockState());
                }
                level.setBlockAndUpdate(soilPos, Registration.TILLED_SOIL_BLOCK.get().getStateForPlacement(level, soilPos));
                MachineUtils.playSound(level, soilPos, level.getRandom(), SoundEvents.HOE_TILL, 0f, 0.5f);

//                AgriculturalEnhancements.debugLog(""+soilState);
            }
        }
    }

    private void doNutrientWorkProcesses(BlockState state) {
        if (canWork(state)) {
//            AgriculturalEnhancements.debugLog("Can work, check soil");
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
//            AgriculturalEnhancements.debugLog(""+direction);
            assert this.level != null;
            for (int i = 1; i <= 9; i++) {
                BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction, i).below());
                if (!targetState.is(Registration.TILLED_SOIL_BLOCK.get())) {
                    continue;
                }
                int currentFert = targetState.getValue(TilledSoilBlock.SOIL_NUTRIENTS);
                if (currentFert >= TilledSoilBlock.MAX_NUTRIENTS) {
                    continue;
                }
                BlockPos pos = this.worldPosition.relative(direction, i).below();
                if (!consumeFertilizer((TilledSoilBlock.MAX_NUTRIENTS - currentFert) * 100, true)) {
                    continue;
                }
                if (!consumeFuel(getMinFuelToWork() * TilledSoilBlock.MAX_NUTRIENTS - currentFert, true)) {
                    continue;
                }
                level.setBlockAndUpdate(pos, targetState.setValue(TilledSoilBlock.SOIL_NUTRIENTS, TilledSoilBlock.MAX_NUTRIENTS));
                MachineUtils.playSound(level, pos, level.getRandom(), SoundEvents.GRAVEL_HIT, 0f, 0.5f);
                consumeFertilizer((TilledSoilBlock.MAX_NUTRIENTS - currentFert) * 100, false);
                consumeFuel(getMinFuelToWork() * TilledSoilBlock.MAX_NUTRIENTS - currentFert, false);
//                AgriculturalEnhancements.debugLog(""+targetState);
            }
        }
    }

    private void doPlantWorkProcesses(BlockState state) {
        if (canWork(state)) {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            assert this.level != null;
            int i = blocks.get(level.getRandom().nextInt(blocks.size()));
            blocks.remove(Integer.valueOf(i));
            if (blocks.isEmpty()) {
                blocks.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            }
//            AgriculturalEnhancements.debugLog("Integer: "+i);
            BlockPos cropPos = this.worldPosition.relative(direction, i);
            BlockState targetState = this.level.getBlockState(cropPos);
            if (!targetState.isAir()) {
                return;
            }
            ItemStack stack = getFirstValidIngredient();
            if (stack.isEmpty()) {
                return;
            }
            if (stack.getItem() instanceof BlockItem blockItem) {
                BlockState cropState = blockItem.getBlock().defaultBlockState();
                if (cropState.canSurvive(level, cropPos)) {
                    level.setBlockAndUpdate(cropPos, blockItem.getBlock().defaultBlockState());
                    MachineUtils.playSound(level, cropPos, level.getRandom(), SoundEvents.CROP_PLANTED, 0f);
                    stack.shrink(1);
                    consumeFuel(getMinFuelToWork(), false);
                }
            }
//                AgriculturalEnhancements.debugLog(""+targetState);
        }
    }

    public void doConsumeProcess(ItemStack stack) {
        if (getFertilizerToAdd() == 0) {
            int toAdd = tryConsumeStack(stack);
            if (toAdd > 0) {
//            AgriculturalEnhancements.debugLog("Doing fuel process");
                setFertilizerToAdd(toAdd);
                if (!addFertilizer(getFertilizerToAdd(), true)) {
                    setFertilizerToAdd(getMaxFertilizer() - getCurrentFertilizer());
                }
                setIncrementalFertilizerToAdd(getFertilizerToAdd() / 10);
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
            return 500;
        }
        return 0;
    }

    public boolean canWork(BlockState state) {
        if (getCurrentFuel() < getMinFuelToWork()) {
            changeStateIfNecessary(state, false);
            return false;
        }
        changeStateIfNecessary(state, true);
        return true;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        outputHandler.deserializeNBT(tag.getCompound("invOut"));
        fertilizerHandler.deserializeNBT(tag.getCompound("invFert"));
        seedHandler.deserializeNBT(tag.getCompound("invSeeds"));
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));
        machineData.set(CropManagerData.Data.FUEL, tag.getInt("fuel"));
        machineData.set(CropManagerData.Data.FERTILIZER, tag.getInt("fertilizer"));
        soilTimer = tag.getInt("soilTimer");
        plantTimer = tag.getInt("plantTimer");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("invOut", outputHandler.serializeNBT());
        tag.put("invFert", fertilizerHandler.serializeNBT());
        tag.put("invSeeds", seedHandler.serializeNBT());
        tag.put("invFuel", fuelHandler.serializeNBT());
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("fertilizer", getCurrentFertilizer());
        tag.putInt("soilTimer", soilTimer);
        tag.putInt("plantTimer", plantTimer);
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

    public ItemStack getFirstValidIngredient() {
        for (int seedSlot : SEED_SLOTS) {
            ItemStack stack = seedHandler.getStackInSlot(seedSlot);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
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
        return ConfigSettings.CROP_MANAGER_FERTILIZER_STORAGE.get();
    }

    public void setFertilizerToAdd(int toSet) {
        this.fertilizerToAdd = toSet;
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

    public void resetTillage(BlockState state) {
        doTillProcesses(state);
    }
}

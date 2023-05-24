package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
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
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.IngredientHandler;
import vapourdrive.agricultural_enhancements.content.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static vapourdrive.agricultural_enhancements.setup.Registration.HARVESTER_TILE;

public class HarvesterTile extends AbstractBaseFuelUserTile {

    public final int[] INGREDIENT_SLOT = {0};
    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final IngredientHandler ingredientHandler = new HarvesterIngredientHandler(this, INGREDIENT_SLOT.length);
    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, outputHandler, ingredientHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);
    public final HarvesterData harvesterData = new HarvesterData();
    private int harvestTimer = 0;

    public HarvesterTile(BlockPos pos, BlockState state) {
        super(HARVESTER_TILE.get(), pos, state, ConfigSettings.HARVESTER_FUEL_STORAGE.get() * 100, ConfigSettings.HARVESTER_FUEL_TO_WORK.get(), new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14});
    }

    public void tickServer(BlockState state) {
        super.tickServer(state);
        if (harvestTimer % ConfigSettings.HARVESTER_PROCESS_TIME.get() == 0) {
            doWorkProcesses(state);
        }
        harvestTimer++;
        if (harvestTimer >= ConfigSettings.HARVESTER_PROCESS_TIME.get()) {
            harvestTimer = 0;
        }
    }

    private void doWorkProcesses(BlockState state) {
        if (canWork(state)) {
            Direction direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
//            AgriculturalEnhancements.debugLog(""+direction);
            assert this.level != null;
            for (int i = 1; i <= 9; i++) {
                BlockState targetState = this.level.getBlockState(this.worldPosition.relative(direction, i));
                if (targetState.getBlock() instanceof CropBlock crop) {
                    if (crop.isMaxAge(targetState)) {
                        BlockPos pos = this.worldPosition.relative(direction, i);
                        ItemStack tool = getStackInSlot(MachineUtils.Area.INGREDIENT, 0);
                        LootContext.Builder builder = (new LootContext.Builder((ServerLevel) level)).withRandom(level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool);
                        List<ItemStack> drops = MachineUtils.cleanItemStacks(targetState.getDrops(builder));
//                        AgriculturalEnhancements.debugLog("Drops pre-cull: " + drops);
                        ItemStack seed = crop.getCloneItemStack(level, pos, targetState);
                        if (isNonDestructive()) {
                            for (ItemStack drop : drops) {
                                if (ItemStack.isSame(drop, seed)) {
                                    drop.shrink(1);
                                    if (drop.isEmpty()) {
                                        drops.remove(drop);
                                    }
                                    break;
                                }
                            }
                        }

                        if (MachineUtils.canPushAllOutputs(drops, this)) {
                            for (ItemStack stack : drops) {
                                MachineUtils.pushOutput(stack, false, this);
                            }
//                            AgriculturalEnhancements.debugLog("Server Success");
                            MachineUtils.playSound(level, pos, level.getRandom(), SoundEvents.CROP_BREAK, 0f, 0.7f);
                            if (isNonDestructive()) {
                                level.setBlockAndUpdate(pos, targetState.setValue(crop.getAgeProperty(), 1));
                            } else {
                                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                            }
                            consumeFuel(getMinFuelToWork(), false);
                        }
                    }
                }
//                AgriculturalEnhancements.debugLog(""+targetState);
            }
        }
    }

    public boolean isNonDestructive() {
        if (!ConfigSettings.HARVESTER_NON_DESTRUCTIVE_HARVESTING.get()) {
            return false;
        } else {
            return harvesterData.get(HarvesterData.Data.MODE) == 1;
        }
    }

    public boolean toggleMode() {
        if (ConfigSettings.HARVESTER_NON_DESTRUCTIVE_HARVESTING.get()) {
            if (harvesterData.get(HarvesterData.Data.MODE) == 0) {
                harvesterData.set(HarvesterData.Data.MODE, 1);
            } else {
                harvesterData.set(HarvesterData.Data.MODE, 0);
            }
            return true;
        }
        harvesterData.set(HarvesterData.Data.MODE, 0);
        return false;
    }

    @Override
    public boolean canWork(BlockState state) {
        if (getCurrentFuel() < getMinFuelToWork()) {
            changeStateIfNecessary(state, false);
            return false;
        }
        if (outputHandler.isFull()) {
            changeStateIfNecessary(state, false);
            return false;
        } else {
            changeStateIfNecessary(state, true);
            return true;
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        outputHandler.deserializeNBT(tag.getCompound("invOut"));
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));
        ingredientHandler.deserializeNBT(tag.getCompound("invIngredient"));
        harvesterData.set(HarvesterData.Data.FUEL, tag.getInt("fuel"));
        harvesterData.set(HarvesterData.Data.MODE, tag.getInt("mode"));
        harvestTimer = tag.getInt("harvestTimer");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("invOut", outputHandler.serializeNBT());
        tag.put("invFuel", fuelHandler.serializeNBT());
        tag.put("invIngredient", ingredientHandler.serializeNBT());
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("harvestTimer", harvestTimer);
        tag.putInt("mode", harvesterData.get(HarvesterData.Data.MODE));
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
            case INGREDIENT -> ingredientHandler.getStackInSlot(INGREDIENT_SLOT[index]);
            case INGREDIENT_2 -> ItemStack.EMPTY;
        };
    }

    @Override
    public void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate) {
        switch (area) {
            case FUEL -> fuelHandler.extractItem(FUEL_SLOT[index], amount, simulate);
            case OUTPUT -> outputHandler.extractItem(OUTPUT_SLOTS[index], amount, simulate);
            case INGREDIENT -> ingredientHandler.extractItem(INGREDIENT_SLOT[index], amount, simulate);
        }
    }

    @Override
    public ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate) {
        return switch (area) {
            case FUEL -> fuelHandler.insertItem(FUEL_SLOT[index], stack, simulate);
            case OUTPUT -> outputHandler.insertItem(OUTPUT_SLOTS[index], stack, simulate, true);
            case INGREDIENT -> ingredientHandler.insertItem(INGREDIENT_SLOT[index], stack, simulate);
            case INGREDIENT_2 -> ItemStack.EMPTY;
        };
    }
}

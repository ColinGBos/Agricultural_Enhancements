package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.vapourware.shared.base.AbstractBaseFuelUserTile;
import vapourdrive.vapourware.shared.base.itemhandlers.FuelHandler;
import vapourdrive.vapourware.shared.base.itemhandlers.IngredientHandler;
import vapourdrive.vapourware.shared.base.itemhandlers.OutputHandler;
import vapourdrive.vapourware.shared.base.itemhandlers.ToolHandler;
import vapourdrive.vapourware.shared.utils.MachineUtils;

import java.util.List;
import java.util.Objects;

import static vapourdrive.agricultural_enhancements.setup.Registration.HARVESTER_TILE;

public class HarvesterTile extends AbstractBaseFuelUserTile implements MenuProvider {

    public final int[] INGREDIENT_SLOT = {0};
    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final IngredientHandler ingredientHandler = new ToolHandler(this, INGREDIENT_SLOT.length);
//    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, outputHandler, ingredientHandler);
//    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);
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
                if (targetState.getBlock() instanceof CropBlock || targetState.getBlock() instanceof BushBlock) {
                    BlockPos pos = this.worldPosition.relative(direction, i);
                    ItemStack tool = getStackInSlot(MachineUtils.Area.INGREDIENT_1, 0);
                    LootParams.Builder builder =
                            (new LootParams.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN,
                                    Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool);

                    if (targetState.getBlock() instanceof CropBlock crop) {
                        if (crop.isMaxAge(targetState)) {
                            List<ItemStack> drops = MachineUtils.cleanItemStacks(targetState.getDrops(builder));
//                        AgriculturalEnhancements.debugLog("Drops pre-cull: " + drops);
                            if (isNonDestructive()) {
                                ItemStack seed = crop.getCloneItemStack(level, pos, targetState);
                                drops = cullSeed(drops, seed);
                            }
                            if (MachineUtils.canPushAllOutputs(drops, this)) {
                                for (ItemStack stack : drops) {
                                    MachineUtils.pushOutput(stack, false, this);
                                }
//                            AgriculturalEnhancements.debugLog("Server Success");
                                MachineUtils.playSound(level, pos, level.getRandom(), SoundEvents.CROP_BREAK, 0f, 0.7f);
                                if (isNonDestructive()) {
                                    level.setBlockAndUpdate(pos, crop.getStateForAge(0));
                                } else {
                                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                }
                                consumeFuel(getMinFuelToWork(), false);
                            }
                        }
                    } else if (targetState.getBlock() instanceof BushBlock) {
                        List<ItemStack> drops = MachineUtils.cleanItemStacks(targetState.getDrops(builder));
                        if (MachineUtils.getTotalCount(drops) > 1) {
//                          We re-run the drops so they aren't always including bonus drops
                            drops = MachineUtils.cleanItemStacks(targetState.getDrops(builder));
                            if (MachineUtils.canPushAllOutputs(drops, this)) {
                                for (ItemStack stack : drops) {
                                    MachineUtils.pushOutput(stack, false, this);
                                }
//                            AgriculturalEnhancements.debugLog("Server Success");
                                MachineUtils.playSound(level, pos, level.getRandom(), SoundEvents.CROP_BREAK, 0f, 0.7f);
                                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                consumeFuel(getMinFuelToWork(), false);
                            }
                        }
                    }
                }
//                AgriculturalEnhancements.debugLog(""+targetState);
            }
        }
    }

    public List<ItemStack> cullSeed(List<ItemStack> drops, ItemStack seed) {
        for (ItemStack drop : drops) {
            if (ItemStack.isSameItem(drop, seed)) {
                drop.shrink(1);
                if (drop.isEmpty()) {
                    drops.remove(drop);
                }
                break;
            }
        }
        return drops;
    }

    public boolean isNonDestructive() {
        if (!ConfigSettings.HARVESTER_NON_DESTRUCTIVE_HARVESTING.get()) {
            return false;
        } else {
            return harvesterData.get(HarvesterData.Data.MODE) == 1;
        }
    }

    public void setMode(boolean isNonDestructive) {
        if (isNonDestructive) {
            harvesterData.set(HarvesterData.Data.MODE, 1);
        } else {
            harvesterData.set(HarvesterData.Data.MODE, 0);
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
        boolean canWork = true;
        if ((Objects.requireNonNull(this.getLevel())).hasNeighborSignal(this.worldPosition)) {
            canWork = false;
        } else if (this.getCurrentFuel() < this.getMinFuelToWork()) {
            canWork = false;
        } else if (outputHandler.isFull()) {
            canWork = false;
        }
        this.changeStateIfNecessary(state, canWork);
        return canWork;
    }


    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        outputHandler.deserializeNBT(registries, tag.getCompound("invOut"));
        fuelHandler.deserializeNBT(registries, tag.getCompound("invFuel"));
        ingredientHandler.deserializeNBT(registries, tag.getCompound("invIngredient"));
        harvesterData.set(HarvesterData.Data.FUEL, tag.getInt("fuel"));
        harvesterData.set(HarvesterData.Data.MODE, tag.getInt("mode"));
        harvestTimer = tag.getInt("harvestTimer");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("invOut", outputHandler.serializeNBT(registries));
        tag.put("invFuel", fuelHandler.serializeNBT(registries));
        tag.put("invIngredient", ingredientHandler.serializeNBT(registries));
        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("harvestTimer", harvestTimer);
        tag.putInt("mode", harvesterData.get(HarvesterData.Data.MODE));
    }

    public IItemHandler getItemHandler(@org.jetbrains.annotations.Nullable Direction side) {
//        return combined;
        if (side == Direction.DOWN) {
            return outputHandler;
        }
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
            case INGREDIENT_1 -> ingredientHandler.getStackInSlot(INGREDIENT_SLOT[index]);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate) {
        switch (area) {
            case FUEL -> fuelHandler.extractItem(FUEL_SLOT[index], amount, simulate);
            case OUTPUT -> outputHandler.extractItem(OUTPUT_SLOTS[index], amount, simulate);
            case INGREDIENT_1 -> ingredientHandler.extractItem(INGREDIENT_SLOT[index], amount, simulate);
        }
    }

    @Override
    public ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate) {
        return switch (area) {
            case FUEL -> fuelHandler.insertItem(FUEL_SLOT[index], stack, simulate);
            case OUTPUT -> outputHandler.insertItem(OUTPUT_SLOTS[index], stack, simulate, true);
            case INGREDIENT_1 -> ingredientHandler.insertItem(INGREDIENT_SLOT[index], stack, simulate);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public ContainerData getContainerData() {
        return this.getHarvesterData();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(AgriculturalEnhancements.MODID+".harvester");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new HarvesterMenu(id, this.level, this.worldPosition, player.getInventory(), player, this.getHarvesterData());
    }
}

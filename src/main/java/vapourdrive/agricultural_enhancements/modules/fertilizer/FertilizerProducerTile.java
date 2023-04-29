package vapourdrive.agricultural_enhancements.modules.fertilizer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.IngredientHandler;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static vapourdrive.agricultural_enhancements.setup.Registration.FERTILIZER_PRODUCER_TILE;
import static vapourdrive.agricultural_enhancements.utils.MachineUtils.getMatchingRecipeForInput;
//import static vapourdrive.agricultural_enhancements.utils.MachineUtils.pushOutput;

public class FertilizerProducerTile extends AbstractBaseFuelUserTile {

    public final int[] INGREDIENT_SLOT = {0};
    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final IngredientHandler ingredientHandler = new IngredientHandler(this, INGREDIENT_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, ingredientHandler, outputHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

    public final FertilizerProducerData fertilizerProducerData = new FertilizerProducerData();
    private ItemStack lastSmelting = ItemStack.EMPTY;
    private ItemStack currentIngredient = ItemStack.EMPTY;
    public int n = 0;
    public int p = 0;
    public int k = 0;

    public FertilizerProducerTile(BlockPos pos, BlockState state) {
        super(FERTILIZER_PRODUCER_TILE.get(), pos, state, 12800000, 100, new int[]{0, 1, 2, 3});
    }

    public void tickServer(BlockState state) {
        ItemStack fuel = getStackInSlot(MachineUtils.Area.FUEL, 0);
        ItemStack ingredient = getStackInSlot(MachineUtils.Area.INGREDIENT, 0);
        MachineUtils.doFuelProcess(fuel, wait, this);

        if (!lastSmelting.isEmpty() && !ItemStack.isSame(ingredient, lastSmelting)) {
            fertilizerProducerData.set(FertilizerProducerData.Data.COOK_PROGRESS, 0);
        }
        if (!ingredient.isEmpty()) {
            doWorkProcesses(ingredient, state);
        }
        wait += 1;
        if (wait >= 160) {
            wait = 0;
        }
    }

    private void doWorkProcesses(ItemStack ingredient, BlockState state) {
        if (canWork()) {
            changeStateIfNecessary(state, true);

            if (fertilizerProducerData.get(FertilizerProducerData.Data.COOK_PROGRESS) == 0){
                if(currentIngredient.isEmpty()) {
                    currentIngredient = ingredient;
                    fertilizerProducerData.set(FertilizerProducerData.Data.COOK_MAX, MachineUtils.getCookTime(level, ingredient));
                }

                if(pushOutput(currentIngredient, true) >= 1 && getCurrentFuel() >= fertilizerProducerData.get(FertilizerProducerData.Data.COOK_MAX)){
                    assert level != null;
                    level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL);
                    this.setChanged();
                    progressCook();
                }
            } else if (fertilizerProducerData.get(FertilizerProducerData.Data.COOK_PROGRESS) >= 0) {
                progressCook();
                if (fertilizerProducerData.get(FertilizerProducerData.Data.COOK_PROGRESS) >= fertilizerProducerData.get(FertilizerProducerData.Data.COOK_MAX)) {
                    if (pushOutput(currentIngredient, false) == -1) {
                        //ingredientHandler.extractItem(INPUT_SLOT[0], 1, false);
                        removeFromSlot(MachineUtils.Area.INGREDIENT, 0, 1, false);
                        ItemStack remainingIngredient = getStackInSlot(MachineUtils.Area.INGREDIENT,0);
                        if(remainingIngredient.isEmpty()){
                            currentIngredient = ItemStack.EMPTY;

                        } else if (!ItemStack.isSame(remainingIngredient, currentIngredient)) {
                            fertilizerProducerData.set(FertilizerProducerData.Data.COOK_MAX, 0);
                        }
                        if(remainingIngredient.isEmpty() || fertilizerProducerData.get(FertilizerProducerData.Data.COOK_MAX) < fertilizerProducerData.get(FertilizerProducerData.Data.COOK_MAX)) {
                            assert level != null;
                            level.setBlock(worldPosition, state.setValue(BlockStateProperties.LIT, false), Block.UPDATE_ALL);
                            this.setChanged();
                        }
                    }
                    fertilizerProducerData.set(FertilizerProducerData.Data.COOK_PROGRESS, 0);
                }
            }

        } else {
            changeStateIfNecessary(state, false);
        }
    }

    public int pushOutput(ItemStack stack, boolean simulate){
        assert this.level != null;
        Optional<FertilizerRecipe> recipe = this.level.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SimpleContainer(stack), this.level);
        if(recipe.isPresent()) {
            int[] outputs = recipe.get().getOutputs();
        }

        return 0;
    }

    public void progressCook() {
        fertilizerProducerData.set(FertilizerProducerData.Data.COOK_PROGRESS, fertilizerProducerData.get(FertilizerProducerData.Data.COOK_PROGRESS)+100);
        consumeFuel(100, false);
    }

    public static ItemStack getSmeltingResultForItem(Level world, ItemStack itemStack) {
        Optional<SmeltingRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        return matchingRecipe.map(furnaceRecipe -> furnaceRecipe.getResultItem().copy()).orElse(ItemStack.EMPTY);
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
        ingredientHandler.deserializeNBT(tag.getCompound("invIngr"));
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));

        fertilizerProducerData.set(FertilizerProducerData.Data.FUEL, tag.getInt("fuel"));

        increment = tag.getInt("increment");
        toAdd = tag.getInt("toAdd");
        wait = tag.getInt("wait");
        n = tag.getInt("n");
        p = tag.getInt("p");
        k = tag.getInt("k");

        super.load(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        tag.put("invOut", outputHandler.serializeNBT());
        tag.put("invIngr", ingredientHandler.serializeNBT());
        tag.put("invFuel", fuelHandler.serializeNBT());

        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("increment", increment);
        tag.putInt("toAdd", toAdd);
        tag.putInt("wait", wait);
        tag.putInt("n", n);
        tag.putInt("p", p);
        tag.putInt("k", k);

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
        return fertilizerProducerData.get(FertilizerProducerData.Data.FUEL);
    }

    @Override
    public boolean addFuel(int toAdd, boolean simulate) {
        if (toAdd + getCurrentFuel() > getMaxFuel()) {
            return false;
        }
        if (!simulate) {
            fertilizerProducerData.set(FertilizerProducerData.Data.FUEL, getCurrentFuel() + toAdd);
        }

        return true;
    }

    @Override
    public boolean consumeFuel(int toConsume, boolean simulate) {
        if (getCurrentFuel() < toConsume) {
            return false;
        }
        if (!simulate) {
            fertilizerProducerData.set(FertilizerProducerData.Data.FUEL, getCurrentFuel() - toConsume);
        }
        return true;
    }

    public FertilizerProducerData getFertilizerProducerData() {
        return fertilizerProducerData;
    }

    @Override
    public ItemStack getStackInSlot(MachineUtils.Area area, int index) {
        return switch (area) {
            case FUEL -> fuelHandler.getStackInSlot(FUEL_SLOT[index]);
            case OUTPUT -> outputHandler.getStackInSlot(OUTPUT_SLOTS[index]);
            case INGREDIENT -> ingredientHandler.getStackInSlot(INGREDIENT_SLOT[index]);
        };
    }

    @Override
    public void removeFromSlot(MachineUtils.Area area, int index, int amount, boolean simulate) {
        switch (area) {
            case FUEL -> fuelHandler.extractItem(FUEL_SLOT[index], amount, simulate);
            case OUTPUT -> outputHandler.extractItem(OUTPUT_SLOTS[index], amount, simulate);
            case INGREDIENT -> ingredientHandler.extractItem(INGREDIENT_SLOT[index], amount, simulate );
        }
    }

    @Override
    public ItemStack insertToSlot(MachineUtils.Area area, int index, ItemStack stack, boolean simulate) {
        return switch (area) {
            case FUEL -> fuelHandler.insertItem(FUEL_SLOT[index], stack, simulate);
            case OUTPUT -> outputHandler.insertItem(OUTPUT_SLOTS[index], stack, simulate, true);
            case INGREDIENT -> ingredientHandler.insertItem(INGREDIENT_SLOT[index], stack, simulate);
        };
    }
}

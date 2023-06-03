package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.content.fertilizer.FertilizerUtils;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseFuelUserTile;
import vapourdrive.vapourware.shared.base.itemhandlers.FuelHandler;
import vapourdrive.vapourware.shared.base.itemhandlers.IngredientHandler;
import vapourdrive.vapourware.shared.base.itemhandlers.OutputHandler;
import vapourdrive.vapourware.shared.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

import static vapourdrive.agricultural_enhancements.setup.Registration.FERTILIZER_PRODUCER_TILE;
import static vapourdrive.vapourware.shared.utils.MachineUtils.canPushAllOutputs;
import static vapourdrive.vapourware.shared.utils.MachineUtils.pushOutput;

public class FertilizerProducerTile extends AbstractBaseFuelUserTile {

    public enum Element {
        N,
        P,
        K
    }

    public final int[] INGREDIENT_SLOT = {0};
    private final FuelHandler fuelHandler = new FuelHandler(this, FUEL_SLOT.length);
    private final IngredientHandler ingredientHandler = new IngredientHandler(this, INGREDIENT_SLOT.length);
    private final OutputHandler outputHandler = new OutputHandler(this, OUTPUT_SLOTS.length);
    private final LazyOptional<OutputHandler> lazyOutputHandler = LazyOptional.of(() -> outputHandler);
    private final CombinedInvWrapper combined = new CombinedInvWrapper(fuelHandler, ingredientHandler, outputHandler);
    private final LazyOptional<CombinedInvWrapper> combinedHandler = LazyOptional.of(() -> combined);

    public final FertilizerProducerData fertilizerProducerData = new FertilizerProducerData();
    public final int[] elementToAdd = {0, 0, 0};
    public final int[] incrementalElementToAdd = {0, 0, 0};
    public int createFertTimer = 0;
    public int consumerTimer = 0;

    private final int createFertMaxTime = ConfigSettings.FERTILIZER_PRODUCER_PRODUCE_TIME.get();
    private final int consumeMaxTime = ConfigSettings.FERTILIZER_PRODUCER_INGREDIENT_TIME.get();

    public FertilizerProducerTile(BlockPos pos, BlockState state) {
        super(FERTILIZER_PRODUCER_TILE.get(), pos, state, ConfigSettings.FERTILIZER_PRODUCER_FUEL_STORAGE.get() * 100, ConfigSettings.FERTILIZER_PRODUCER_FUEL_TO_WORK.get(), new int[]{0, 1, 2, 3});
    }

    public void tickServer(BlockState state) {
        super.tickServer(state);
        ItemStack ingredient = getStackInSlot(MachineUtils.Area.INGREDIENT_1, 0);
        doWorkProcesses(ingredient, state);

        createFertTimer++;
        if (createFertTimer >= createFertMaxTime) {
            createFertTimer = 0;
        }
        if (consumerTimer >= consumeMaxTime) {
            consumerTimer = 0;
        }
    }

    private void doWorkProcesses(ItemStack ingredient, BlockState state) {
        doConsumeProcess(ingredient);
        doCreateProcess();
        canWork(state);
    }

    public void doCreateProcess() {
        if (createFertTimer == 0) {
            int i = ConfigSettings.FERTILIZER_PRODUCER_NUTRIENTS_PER_FERTILIZER.get();
            if (!consumeElement(Element.N, i, true) || !consumeElement(Element.P, i, true) || !consumeElement(Element.K, i, true)) {
                return;
            }
            if (canPushAllOutputs(Collections.singletonList(new ItemStack(Registration.FERTILISER.get())), this)) {
                pushOutput(new ItemStack(Registration.FERTILISER.get()), false, this);
                consumeElement(Element.N, i, false);
                consumeElement(Element.P, i, false);
                consumeElement(Element.K, i, false);
            }
        }
    }

    public void doConsumeProcess(ItemStack stack) {
        if (consumerTimer == 0 && consumeFuel(minWorkFuel, true) && !stack.isEmpty()) {
            int[] toAdds = tryConsumeStack(stack);
            if (toAdds != null) {
                for (Element element : Element.values()) {
                    int emnt = toAdds[element.ordinal()];
                    if (emnt > 0) {
                        setElementToAdd(element, emnt);
                        if (!addElement(element, getElementToAdd(element), true)) {
                            setElementToAdd(element, getMaxElement() - getCurrentElement(element));
                        }
                        setIncrementalElementToAdd(element, getElementToAdd(element) / consumeMaxTime);
                    }
                }
            }
        }
        boolean increment = false;
        for (Element element : Element.values()) {
            if (getElementToAdd(element) > 0) {
                increment = true;
                addElement(element, getIncrementalElementToAdd(element), false);
                setElementToAdd(element, getElementToAdd(element) - getIncrementalElementToAdd(element));
            }
        }
        if (increment) {
            consumeFuel(minWorkFuel / consumeMaxTime, false);
            consumerTimer++;
        } else {
            consumerTimer = 0;
        }
    }

    public int[] tryConsumeStack(ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.hasCraftingRemainingItem()) {
//                    AgriculturalEnhancements.debugLog("Fuel has a container item to try to push.");
                ItemStack remainder = stack.getCraftingRemainingItem();
                if (!canPushAllOutputs(Collections.singletonList(remainder), this)) {
//                    AgriculturalEnhancements.debugLog("Either the ingredient or the bucket say there's room for two");
                    return null;
                }
            }

            int[] emnts = FertilizerUtils.getFertilizerResultForItem(level, stack);
            if (emnts != null) {
                boolean hasSpace = true;
                for (Element element : Element.values()) {
                    if (getCurrentElement(element) + emnts[element.ordinal()] > getMaxElement()) {
                        hasSpace = false;
                    }
                }
                if (hasSpace) {
                    removeFromSlot(MachineUtils.Area.INGREDIENT_1, 0, 1, false);
                    return emnts;
                }
            }
        }
        return null;
    }

    public void setElementToAdd(Element element, int toSet) {
        this.elementToAdd[element.ordinal()] = toSet;
    }

    public int getElementToAdd(Element element) {
        return this.elementToAdd[element.ordinal()];
    }

    public void setIncrementalElementToAdd(Element element, int increment) {
        this.incrementalElementToAdd[element.ordinal()] = increment;
    }

    public int getIncrementalElementToAdd(Element element) {
        return this.incrementalElementToAdd[element.ordinal()];
    }

    public int getCurrentElement(Element element) {
        return switch (element) {
            case N -> fertilizerProducerData.get(FertilizerProducerData.Data.N);
            case P -> fertilizerProducerData.get(FertilizerProducerData.Data.P);
            case K -> fertilizerProducerData.get(FertilizerProducerData.Data.K);
        };
    }

    public int getMaxElement() {
        return ConfigSettings.FERTILIZER_PRODUCER_MAX_NUTRIENTS.get();
    }

    public boolean addElement(Element element, int toAdd, boolean simulate) {
        if (toAdd + getCurrentElement(element) > getMaxElement()) {
//            AgriculturalEnhancements.debugLog("Can't add element: "+getCurrentElement(element));
            return false;
        }
        if (!simulate) {
//            AgriculturalEnhancements.debugLog("Adding element + : "+toAdd+" "+getCurrentElement(element));
            switch (element) {
                case N -> fertilizerProducerData.set(FertilizerProducerData.Data.N, getCurrentElement(element) + toAdd);
                case K -> fertilizerProducerData.set(FertilizerProducerData.Data.K, getCurrentElement(element) + toAdd);
                case P -> fertilizerProducerData.set(FertilizerProducerData.Data.P, getCurrentElement(element) + toAdd);
            }
        }

        return true;
    }

    public boolean consumeElement(Element element, int toConsume, boolean simulate) {
        return consumeElement(element, toConsume, simulate, false);
    }

    public boolean consumeElement(Element element, int toConsume, boolean simulate, boolean force) {
        if (getCurrentElement(element) < toConsume && !force) {
            return false;
        }
        if (!simulate) {
            switch (element) {
                case N ->
                        fertilizerProducerData.set(FertilizerProducerData.Data.N, Math.max(0, getCurrentElement(element) - toConsume));
                case P ->
                        fertilizerProducerData.set(FertilizerProducerData.Data.P, Math.max(0, getCurrentElement(element) - toConsume));
                case K ->
                        fertilizerProducerData.set(FertilizerProducerData.Data.K, Math.max(0, getCurrentElement(element) - toConsume));
            }
        }
        return true;
    }

    public boolean ventElement() {
        AgriculturalEnhancements.debugLog("venting tile");
        boolean ret = false;
        for (FertilizerProducerTile.Element element : FertilizerProducerTile.Element.values()) {
            if (getCurrentElement(element) > 0) {
                ret = true;
            }
            consumeElement(element, 1000, false, true);
            AgriculturalEnhancements.debugLog(element.name() + getCurrentElement(element));
        }
        return ret;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        outputHandler.deserializeNBT(tag.getCompound("invOut"));
        ingredientHandler.deserializeNBT(tag.getCompound("invIngr"));
        fuelHandler.deserializeNBT(tag.getCompound("invFuel"));

        fertilizerProducerData.set(FertilizerProducerData.Data.FUEL, tag.getInt("fuel"));
        fertilizerProducerData.set(FertilizerProducerData.Data.N, tag.getInt("n"));
        fertilizerProducerData.set(FertilizerProducerData.Data.P, tag.getInt("p"));
        fertilizerProducerData.set(FertilizerProducerData.Data.K, tag.getInt("k"));

        createFertTimer = tag.getInt("fertTimer");
        consumerTimer = tag.getInt("ingredientTimer");

        super.load(tag);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("invOut", outputHandler.serializeNBT());
        tag.put("invIngr", ingredientHandler.serializeNBT());
        tag.put("invFuel", fuelHandler.serializeNBT());

        tag.putInt("fuel", getCurrentFuel());
        tag.putInt("fertTimer", createFertTimer);
        tag.putInt("ingredientTimer", consumerTimer);
        tag.putInt("n", getCurrentElement(Element.N));
        tag.putInt("p", getCurrentElement(Element.P));
        tag.putInt("k", getCurrentElement(Element.K));
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
}

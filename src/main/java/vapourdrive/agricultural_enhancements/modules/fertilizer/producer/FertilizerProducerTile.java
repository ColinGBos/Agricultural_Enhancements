package vapourdrive.agricultural_enhancements.modules.fertilizer.producer;

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
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.config.ConfigSettings;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseFuelUserTile;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.FuelHandler;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.IngredientHandler;
import vapourdrive.agricultural_enhancements.modules.base.itemhandlers.OutputHandler;
import vapourdrive.agricultural_enhancements.modules.fertilizer.FertilizerUtils;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.agricultural_enhancements.utils.MachineUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

import static vapourdrive.agricultural_enhancements.setup.Registration.FERTILIZER_PRODUCER_TILE;
import static vapourdrive.agricultural_enhancements.utils.MachineUtils.canPushAllOutputs;
import static vapourdrive.agricultural_enhancements.utils.MachineUtils.pushOutput;

public class FertilizerProducerTile extends AbstractBaseFuelUserTile {
    
    public enum Element{
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
    public int[] elementToAdd = {0,0,0};
    public int[] incrementalElementToAdd = {0,0,0};
    public int maxElement = 20480;
    public int wait2 = 0;

    public FertilizerProducerTile(BlockPos pos, BlockState state) {
        super(FERTILIZER_PRODUCER_TILE.get(), pos, state, 12800000, 100, new int[]{0, 1, 2, 3});
    }

    public void tickServer(BlockState state) {
        ItemStack fuel = getStackInSlot(MachineUtils.Area.FUEL, 0);
        ItemStack ingredient = getStackInSlot(MachineUtils.Area.INGREDIENT, 0);
        MachineUtils.doFuelProcess(fuel, wait, this);
        doWorkProcesses(ingredient, state);

        wait += 1;
        if (wait >= 160) {
            wait = 0;
        }
        if (wait2>=80){
            wait2 = 0;
        }
    }

    private void doWorkProcesses(ItemStack ingredient, BlockState state) {
        doConsumeProcess(ingredient);
        doCreateProcess();
        changeStateIfNecessary(state, canWork());
    }

    public void doCreateProcess(){
        if(wait%40==0) {
            if (!consumeElement(Element.N, 5, true) || !consumeElement(Element.P, 5, true) || !consumeElement(Element.K, 5, true)) {
                return;
            }
            if (canPushAllOutputs(Collections.singletonList(new ItemStack(Registration.FERTILISER.get())), this)) {
                pushOutput(new ItemStack(Registration.FERTILISER.get()), false, this);
                AgriculturalEnhancements.debugLog("Pushed the output");
                consumeElement(Element.N, 5, false);
                consumeElement(Element.P, 5, false);
                consumeElement(Element.K, 5, false);
            }
        }
//        AgriculturalEnhancements.debugLog("Wait modulo 40: "+ wait%40);
    }

    public void doConsumeProcess(ItemStack stack) {
        if (wait2 == 0 && consumeFuel(minWorkFuel*80, true) && !stack.isEmpty()) {
//            AgriculturalEnhancements.debugLog("N: " + fertilizerProducerData.get(FertilizerProducerData.Data.N));
//            AgriculturalEnhancements.debugLog("P: " + fertilizerProducerData.get(FertilizerProducerData.Data.P));
//            AgriculturalEnhancements.debugLog("K: " + fertilizerProducerData.get(FertilizerProducerData.Data.K));
            int[] toAdds = tryConsumeStack(stack);
            if (toAdds != null) {
                AgriculturalEnhancements.debugLog("resulting lookup: " + toAdds[0]);
                for (Element element : Element.values()) {
                    int emnt = toAdds[element.ordinal()];
                    if (emnt > 0) {
//            AgriculturalEnhancements.debugLog("Doing fuel process");
                        setElementToAdd(element, toAdds[element.ordinal()]);
                        if (!addElement(element, getElementToAdd(element), true)) {
                            setElementToAdd(element, getMaxElement() - getCurrentElement(element));
                        }
                        setIncrementalElementToAdd(element, getElementToAdd(element) / 80);
                    }
                }
            }
        }
        boolean increment = false;
        for(Element element : Element.values()) {
            if (getElementToAdd(element) > 0) {
                increment = true;
                addElement(element, getIncrementalElementToAdd(element), false);
                setElementToAdd(element, getElementToAdd(element) - getIncrementalElementToAdd(element));
            }
        }
        if(increment){
            wait2++;
            consumeFuel(minWorkFuel, false);
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
            if(emnts != null) {
                boolean hasSpace = true;
                for (Element element : Element.values()) {
                    if (getCurrentElement(element) + emnts[element.ordinal()] > getMaxElement()) {
                        hasSpace = false;
                    }
                }
                if (hasSpace) {
                    removeFromSlot(MachineUtils.Area.INGREDIENT, 0, 1, false);
                    return emnts;
                }
            }
        }
        return null;
    }

    public void setElementToAdd(Element element, int toSet){
        this.elementToAdd[element.ordinal()]=toSet;
    }
    public int getElementToAdd(Element element) {
        return this.elementToAdd[element.ordinal()];
    }
    public void setIncrementalElementToAdd(Element element,int increment) {
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
    public int getMaxElement(){
        return this.maxElement;
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
        if (getCurrentElement(element) < toConsume*80) {
            return false;
        }
        if (!simulate) {
            switch (element) {
                case N -> fertilizerProducerData.set(FertilizerProducerData.Data.N, getCurrentElement(element) - toConsume*80);
                case K -> fertilizerProducerData.set(FertilizerProducerData.Data.K, getCurrentElement(element) - toConsume*80);
                case P -> fertilizerProducerData.set(FertilizerProducerData.Data.P, getCurrentElement(element) - toConsume*80);
            }
        }
        return true;
    }

    public int getMinElementToCraftFertilizer() {
        return 400;
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
        fertilizerProducerData.set(FertilizerProducerData.Data.N, tag.getInt("n"));
        fertilizerProducerData.set(FertilizerProducerData.Data.P, tag.getInt("p"));
        fertilizerProducerData.set(FertilizerProducerData.Data.K, tag.getInt("k"));

        increment = tag.getInt("increment");
        toAdd = tag.getInt("toAdd");
        wait = tag.getInt("wait");
        wait2 = tag.getInt("wait2");

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
        tag.putInt("wait2", wait2);
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
            case INGREDIENT_2 -> ItemStack.EMPTY;
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
            case INGREDIENT_2 -> ItemStack.EMPTY;
        };
    }
}

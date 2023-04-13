package vapourdrive.agricultural_enhancements.utils;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.IFuelUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MachineUtils {
    public enum Area {
        FUEL,
        OUTPUT
    }

    public static boolean canSmelt(ItemStack stack, Level world) {
        return world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), world).isPresent();
    }

    public static float getExperience(Level world, ItemStack itemStack) {
        Optional<SmeltingRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        return matchingRecipe.map(AbstractCookingRecipe::getExperience).orElse(0f);
    }

    public static int getCookTime(Level world, ItemStack itemStack) {
        Optional<SmeltingRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        return matchingRecipe.map(AbstractCookingRecipe::getCookingTime).orElse(200) * 100;
    }

    public static int getBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        } else {
            //everything is multiplied by 100 for variable increments instead of 1 per tick
            //i.e. 100% efficiency is 100 consumption per tick, 125% is 80 consumption etc
            return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) * 100;
        }
    }

    public static ItemStack getSmeltingResultForItem(Level world, ItemStack itemStack) {
        Optional<SmeltingRecipe> matchingRecipe = getMatchingRecipeForInput(world, itemStack);
        return matchingRecipe.map(furnaceRecipe -> furnaceRecipe.getResultItem().copy()).orElse(ItemStack.EMPTY);
    }

    public static Optional<SmeltingRecipe> getMatchingRecipeForInput(Level world, ItemStack itemStack) {
        RecipeManager recipeManager = world.getRecipeManager();
        SimpleContainer singleItemInventory = new SimpleContainer(itemStack);
        return recipeManager.getRecipeFor(RecipeType.SMELTING, singleItemInventory, world);
    }

    public static List<ItemStack> cleanItemStacks(Iterable<? extends ItemStack> stacks) {
        List<ItemStack> ret = new ArrayList<>();
        for (ItemStack stack : stacks) {
            if (ret.isEmpty()) {
                ret.add(stack);
            } else {
                for (ItemStack retStack : ret) {
                    if (ItemStack.isSame(retStack, stack) && retStack.getCount() < retStack.getMaxStackSize()) {
                        int change = Math.min(stack.getCount(), retStack.getMaxStackSize() - retStack.getCount());
                        retStack.grow(change);
                        stack.shrink(change);
                    }
                    if (stack.isEmpty()) {
                        break;
                    }
                }
                if (!stack.isEmpty()) {
                    ret.add(stack);
                }
            }
        }
        return ret;
    }

    public static void doFuelProcess(ItemStack fuel, int wait, IFuelUser user) {
        if (wait % 10 == 0) {
//            AgriculturalEnhancements.debugLog("Doing fuel process");
            user.setFuelToAdd(tryConsumeFuelStack(fuel, user));
            if (!user.addFuel(user.getFuelToAdd(), true)) {
                user.setFuelToAdd(user.getMaxFuel() - user.getCurrentFuel());
            }
            user.setIncrementalFuelToAdd(user.getFuelToAdd() / 10);
        } else {
            if (user.getFuelToAdd() > 0) {
                user.addFuel(user.getIncrementalFuelToAdd(), false);
                user.setFuelToAdd(user.getFuelToAdd()-user.getIncrementalFuelToAdd());
            }
        }
    }

    public static int tryConsumeFuelStack(ItemStack fuel, IFuelUser user) {
        if (!fuel.isEmpty()) {
//            AgriculturalEnhancements.debugLog("Has Fuel Stack");
            if (user.getCurrentFuelStack().isEmpty() || !ItemStack.isSame(user.getCurrentFuelStack(), fuel)) {
                user.setCurrentFuelStack(fuel.copy());
                user.setCurrentBurn((int) (getBurnDuration(fuel) * user.getEfficiencyMultiplier()));
            }
            if (user.getCurrentFuel() + user.getCurrentBurn() * user.getEfficiencyMultiplier() <= user.getMaxFuel() || user.getCurrentFuel() < user.getMinFuelToWork()) {
                if (user.getCurrentFuelStack().hasCraftingRemainingItem()) {
//                    AgriculturalEnhancements.debugLog("Fuel has a container item to try to push.");
                    ItemStack fuelRemainder = user.getCurrentFuelStack().getCraftingRemainingItem();
                    if (canPushAllOutputs(Collections.singletonList(fuelRemainder), user)) {
//                    AgriculturalEnhancements.debugLog("Either the ingredient or the bucket say there's room for two");
                        pushOutput(fuelRemainder, false, user);
                    } else {
                        return 0;
                    }
                }
                user.removeFromSlot(Area.FUEL, 0, 1, false);
                if (!ItemStack.isSame(user.getCurrentFuelStack(), fuel)) {
                    user.setCurrentFuelStack(ItemStack.EMPTY);
                    user.setCurrentBurn((int) (getBurnDuration(fuel) * user.getEfficiencyMultiplier()));
                }
//                furnaceData.fuel += toAdd;
//                AgriculturalEnhancements.debugLog("CurrentBurn: "+user.getCurrentBurn());
                return user.getCurrentBurn();
            }
        }
        return 0;
    }

    public static boolean canPushAllOutputs(List<ItemStack> stacks, IFuelUser user) {
        int empties = getEmptyOutputSlotCount(user);
        if (empties >= stacks.size()) {
            return true;
        } else if (empties == 0) {
            for (ItemStack stack : stacks) {
                if (pushOutput(stack, true, user) < 1) {
                    return false;
                }
            }
        } else {
            int eligible = 0;
            for (ItemStack stack : stacks) {
                for (int i : user.getOutputSlots()) {
                    if (!user.getStackInSlot(MachineUtils.Area.OUTPUT, i).isEmpty()) {
                        if (user.insertToSlot(MachineUtils.Area.OUTPUT, i, stack, true) == ItemStack.EMPTY) {
                            eligible++;
                        }
                    }
                }
            }
            return empties + eligible >= stacks.size();
        }

        return true;
    }

    public static int pushOutput(ItemStack stack, boolean simulate, IFuelUser user) {
        int available = 0;
        int empty = 0;

        ItemStack result = stack.copy();

        //iterates through non-empty slots
        for (int i : user.getOutputSlots()) {
            if (!user.getStackInSlot(MachineUtils.Area.OUTPUT, i).isEmpty()) {
                if (user.insertToSlot(MachineUtils.Area.OUTPUT, i, result, simulate) == ItemStack.EMPTY) {
                    if (!simulate) {
                        return -1;
                    }
                    available++;
                }
            }
        }

        //iterate through the slots (empty or not)
        for (int i : user.getOutputSlots()) {
            if (user.getStackInSlot(MachineUtils.Area.OUTPUT, i).isEmpty()) {
                if (user.insertToSlot(MachineUtils.Area.OUTPUT, i, result, simulate) == ItemStack.EMPTY) {
                    if (!simulate) {
                        return -1;
                    }
                    empty++;
                }
            }
        }
        if (empty == 0) {
            return Math.min(available, 1);
        }

        return available + empty;
    }

    public static int getEmptyOutputSlotCount(IFuelUser user) {
        int empty = 0;
        for (int i : user.getOutputSlots()) {
            if (user.getStackInSlot(MachineUtils.Area.OUTPUT, i).isEmpty()) {
                empty++;
            }
        }
        return empty;
    }
}

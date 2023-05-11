package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineContainer;
import vapourdrive.agricultural_enhancements.modules.base.slots.SlotFuel;
import vapourdrive.agricultural_enhancements.modules.base.slots.SlotOutput;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Objects;

public class IrrigationControllerContainer extends AbstractBaseMachineContainer {
    // gui position of the player inventory grid
    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 84;

    public static final int OUTPUT_INVENTORY_XPOS = 62;
    public static final int OUTPUT_INVENTORY_YPOS = 58;

//    public FurnaceMk2Container(int windowId, Level world, BlockPos pos, Inventory inv, Player player) {
//        this(windowId, world, pos, inv, player, new FurnaceData());
//    }

    public IrrigationControllerContainer(int windowId, Level world, BlockPos pos, Inventory inv, Player player, IrrigationControllerData machineData) {
        super(windowId, world, pos, inv, player, Registration.IRRIGATION_CONTROLLER_CONTAINER.get(), machineData);

        //We use this vs the builtin method because we split all the shorts
        addSplitDataSlots(machineData);

        layoutPlayerInventorySlots(PLAYER_INVENTORY_XPOS, PLAYER_INVENTORY_YPOS);

        if (tileEntity != null) {
            tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> {
                addSlot(new SlotFuel(h, 0, 39, 58));
                addSlot(new SlotOutput(h, 1, OUTPUT_INVENTORY_XPOS, OUTPUT_INVENTORY_YPOS));
                addSlot(new SlotOutput(h, 2, OUTPUT_INVENTORY_XPOS + 18, OUTPUT_INVENTORY_YPOS));
                addSlot(new SlotOutput(h, 3, OUTPUT_INVENTORY_XPOS + (18 * 2), OUTPUT_INVENTORY_YPOS));
                addSlot(new SlotOutput(h, 4, OUTPUT_INVENTORY_XPOS + (18 * 3), OUTPUT_INVENTORY_YPOS));
            });
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(tileEntity.getLevel()), tileEntity.getBlockPos()), playerEntity, Registration.IRRIGATION_CONTROLLER_BLOCK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        AgriculturalEnhancements.debugLog("index: " + index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            //Furnace outputs to Inventory
            if (index >= 37 && index <= 40) {
                AgriculturalEnhancements.debugLog("From furnace output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            }

            //Non-output slots to Inventory
            if (index == 36) {
                AgriculturalEnhancements.debugLog("From furnace non-output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            //Player Inventory
            else if (index <= 35) {
                //Inventory to fuel
                if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0.0) {
                    if (!this.moveItemStackTo(stack, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                //Inventory to hotbar
                if (index <= 26) {
                    AgriculturalEnhancements.debugLog("From Player inventory to hotbar");
                    if (!this.moveItemStackTo(stack, 27, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                //Hotbar to inventory
                else {
                    AgriculturalEnhancements.debugLog("From Hotbar to inventory");
                    if (!this.moveItemStackTo(stack, 0, 27, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }
}

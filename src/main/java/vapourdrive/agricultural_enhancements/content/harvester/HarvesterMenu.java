package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineMenu;
import vapourdrive.vapourware.shared.base.slots.SlotFuel;
import vapourdrive.vapourware.shared.base.slots.SlotOutput;
import vapourdrive.vapourware.shared.base.slots.SlotTool;

import java.util.Objects;

public class HarvesterMenu extends AbstractBaseMachineMenu {
    // gui position of the player inventory grid
    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 84;

    public static final int OUTPUT_INVENTORY_XPOS = 44;
    public static final int OUTPUT_INVENTORY_YPOS = 17;


    public HarvesterMenu(int windowId, Level world, BlockPos pos, Inventory inv, Player player, HarvesterData machineData) {
        super(windowId, world, pos, inv, player, Registration.HARVESTER_MENU.get(), machineData);

        //We use this vs the builtin method because we split all the shorts
        addSplitDataSlots(machineData);

        layoutPlayerInventorySlots(PLAYER_INVENTORY_XPOS, PLAYER_INVENTORY_YPOS);

        if (tileEntity != null && tileEntity instanceof HarvesterTile machine) {
            IItemHandler h = machine.getItemHandler(null);
            addSlot(new SlotFuel(h, 0, 8, 59));
            addSlot(new SlotOutput(h, 1, OUTPUT_INVENTORY_XPOS, OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(h, 2, OUTPUT_INVENTORY_XPOS + 18, OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(h, 3, OUTPUT_INVENTORY_XPOS + (18 * 2), OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(h, 4, OUTPUT_INVENTORY_XPOS + (18 * 3), OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(h, 5, OUTPUT_INVENTORY_XPOS + (18 * 4), OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(h, 6, OUTPUT_INVENTORY_XPOS, OUTPUT_INVENTORY_YPOS + 18));
            addSlot(new SlotOutput(h, 7, OUTPUT_INVENTORY_XPOS + 18, OUTPUT_INVENTORY_YPOS + 18));
            addSlot(new SlotOutput(h, 8, OUTPUT_INVENTORY_XPOS + (18 * 2), OUTPUT_INVENTORY_YPOS + 18));
            addSlot(new SlotOutput(h, 9, OUTPUT_INVENTORY_XPOS + (18 * 3), OUTPUT_INVENTORY_YPOS + 18));
            addSlot(new SlotOutput(h, 10, OUTPUT_INVENTORY_XPOS + (18 * 4), OUTPUT_INVENTORY_YPOS + 18));
            addSlot(new SlotOutput(h, 11, OUTPUT_INVENTORY_XPOS, OUTPUT_INVENTORY_YPOS + (18 * 2)));
            addSlot(new SlotOutput(h, 12, OUTPUT_INVENTORY_XPOS + 18, OUTPUT_INVENTORY_YPOS + (18 * 2)));
            addSlot(new SlotOutput(h, 13, OUTPUT_INVENTORY_XPOS + (18 * 2), OUTPUT_INVENTORY_YPOS + (18 * 2)));
            addSlot(new SlotOutput(h, 14, OUTPUT_INVENTORY_XPOS + (18 * 3), OUTPUT_INVENTORY_YPOS + (18 * 2)));
            addSlot(new SlotOutput(h, 15, OUTPUT_INVENTORY_XPOS + (18 * 4), OUTPUT_INVENTORY_YPOS + (18 * 2)));
            addSlot(new SlotTool(h, 16, 143, OUTPUT_INVENTORY_YPOS + (18 * 2)));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(tileEntity.getLevel()), tileEntity.getBlockPos()), playerEntity, Registration.HARVESTER_BLOCK.get());
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
            if (index >= 37 && index <= 52) {
                AgriculturalEnhancements.debugLog("From furnace output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (index >= 53) {
                AgriculturalEnhancements.debugLog("From furnace output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
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
                if (stack.getBurnTime(RecipeType.SMELTING) > 0.0) {
                    if (!this.moveItemStackTo(stack, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getMaxStackSize() == 1) {
                    if (!this.moveItemStackTo(stack, 52, 53, false)) {
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

    public boolean isNonDestructive() {
        return this.machineData.get(1) == 1;
    }
}

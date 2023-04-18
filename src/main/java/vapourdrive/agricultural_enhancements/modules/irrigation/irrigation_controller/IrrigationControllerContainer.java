package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.slots.SlotFuel;
import vapourdrive.agricultural_enhancements.modules.slots.SlotOutput;
import vapourdrive.agricultural_enhancements.setup.Registration;

import java.util.Objects;

public class IrrigationControllerContainer extends AbstractContainerMenu {

    private final IrrigationControllerTile tileEntity;
    private final Player playerEntity;
    private final IItemHandler playerInventory;
    protected final Level world;
    private final IrrigationControllerData machineData;

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;

    // slot index is the unique index for all slots in this container i.e. 0 - 35 for invPlayer then 36 - 45 for furnaceContents
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int HOTBAR_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX;
    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT;
    private static final int FIRST_FUEL_SLOT_INDEX = PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT;

    // gui position of the player inventory grid
    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 84;

    public static final int OUTPUT_INVENTORY_XPOS = 44;
    public static final int OUTPUT_INVENTORY_YPOS = 17;

//    public FurnaceMk2Container(int windowId, Level world, BlockPos pos, Inventory inv, Player player) {
//        this(windowId, world, pos, inv, player, new FurnaceData());
//    }

    public IrrigationControllerContainer(int windowId, Level world, BlockPos pos, Inventory inv, Player player, IrrigationControllerData machineData) {
        super(Registration.HARVESTER_CONTAINER.get(), windowId);
        tileEntity = (IrrigationControllerTile) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inv);
        this.world = world;
        this.machineData = machineData;

        //We use this vs the builtin method because we split all the shorts
        addSplitDataSlots(machineData);

        layoutPlayerInventorySlots(PLAYER_INVENTORY_XPOS, PLAYER_INVENTORY_YPOS);

        if (tileEntity != null) {
            tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> {
                addSlot(new SlotFuel(h, 0, 8, 59));
            });
        }
    }

    //Full disclosure, I don't really know how tf to do bit 'stuff' but it seems to work
    protected void addSplitDataSlots(ContainerData data) {
        for (int i = 0; i < data.getCount(); ++i) {
            int index = i;
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return data.get(index) & 0xffff;
                }

                @Override
                public void set(int value) {
                    int stored = data.get(index) & 0xffff0000;
                    data.set(index, stored + (value & 0xffff));
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (data.get(index) >> 16) & 0xffff;
                }

                @Override
                public void set(int value) {
                    int stored = data.get(index) & 0x0000ffff;
                    data.set(index, stored | value << 16);
                }
            });
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

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private void addSlotBox(IItemHandler handler, int index, int x, int y, int columns, int spacingX, int rows, int spacingY) {
        for (int j = 0; j < rows; j++) {
            index = addSlotRange(handler, index, x, y, columns, spacingX);
            y += spacingY;
        }
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
        // Player inventory
        int index = addSlotRange(playerInventory, 0, leftCol, topRow + 58, 9, 18);

        //hotbar
        addSlotBox(playerInventory, index, leftCol, topRow, 9, 18, 3, 18);

    }

    @OnlyIn(Dist.CLIENT)
    public float getFuelPercentage() {
        int i = this.machineData.get(0);
        if (i == 0) {
            return 0;
        }
        return (float) i / (float) tileEntity.getMaxFuel();
    }

    @OnlyIn(Dist.CLIENT)
    public float getMaxFuel() {
        return tileEntity.getMaxFuel();
    }

    @OnlyIn(Dist.CLIENT)
    public int getFuelStored() {
        return this.machineData.get(0);
    }

}

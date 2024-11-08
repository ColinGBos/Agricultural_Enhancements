package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineMenu;
import vapourdrive.vapourware.shared.base.slots.SlotFuel;
import vapourdrive.vapourware.shared.base.slots.SlotOutput;

import java.util.Objects;

public class FertilizerProducerMenu extends AbstractBaseMachineMenu {
    // gui position of the player inventory grid

    public static final int OUTPUT_INVENTORY_XPOS = 125;
    public static final int OUTPUT_INVENTORY_YPOS = 26;

    protected final FertilizerProducerTile tileEntity;


    public FertilizerProducerMenu(int windowId, Level world, BlockPos pos, Inventory inv, Player player, FertilizerProducerData machineData) {
        super(windowId, world, pos, inv, player, Registration.FERTILIZER_PRODUCER_MENU.get(), machineData);
        tileEntity = (FertilizerProducerTile) world.getBlockEntity(pos);

        //We use this vs the builtin method because we split all the shorts
        addSplitDataSlots(machineData);

        layoutPlayerInventorySlots(PLAYER_INVENTORY_XPOS, PLAYER_INVENTORY_YPOS);

        if (tileEntity != null) {
            IItemHandler handler = tileEntity.getItemHandler(null);
            addSlot(new SlotFuel(handler, 0, 8, 59));
            addSlot(new FertilizerSlotIngredient(handler, 1, 44, 49, this.world));
            addSlot(new SlotOutput(handler, 2, OUTPUT_INVENTORY_XPOS, OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(handler, 3, OUTPUT_INVENTORY_XPOS + 18, OUTPUT_INVENTORY_YPOS));
            addSlot(new SlotOutput(handler, 4, OUTPUT_INVENTORY_XPOS, OUTPUT_INVENTORY_YPOS + 18));
            addSlot(new SlotOutput(handler, 5, OUTPUT_INVENTORY_XPOS + 18, OUTPUT_INVENTORY_YPOS + 18));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(tileEntity.getLevel()), tileEntity.getBlockPos()), playerEntity, Registration.FERTILIZER_PRODUCER_BLOCK.get());
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
            if (index == 36 || index == 37) {
                AgriculturalEnhancements.debugLog("From furnace non-output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            //Furnace outputs to Inventory
            if (index >= 38 && index <= 41) {
                AgriculturalEnhancements.debugLog("From furnace output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            }

            //Player Inventory
            else if (index <= 35) {
                //Inventory to fuel
                if (stack.getBurnTime(RecipeType.SMELTING) > 0.0) {
                    if (!this.moveItemStackTo(stack, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                //Inventory to infgredient
                if (this.world.getRecipeManager().getRecipeFor(Registration.FERTILIZER_TYPE.get(), new SingleRecipeInput(stack), this.world).isPresent()) {
                    if (!this.moveItemStackTo(stack, 37, 38, false)) {
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

    @OnlyIn(Dist.CLIENT)
    public float getElementPercentage(FertilizerProducerData.Data element) {
        int i = this.machineData.get(element.ordinal());
        if (i == 0) {
            return 0;
        }
        return (float) i / (float) tileEntity.getMaxElement();
    }

    @OnlyIn(Dist.CLIENT)
    public int getElementStored(FertilizerProducerData.Data element) {
        return this.machineData.get(element.ordinal());
    }

    public int getMaxElement() {
        return this.tileEntity.getMaxElement();
    }
}
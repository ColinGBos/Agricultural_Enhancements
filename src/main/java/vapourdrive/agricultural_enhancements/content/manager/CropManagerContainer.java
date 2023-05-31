package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineContainer;
import vapourdrive.vapourware.shared.base.slots.SlotFuel;
import vapourdrive.vapourware.shared.base.slots.SlotOutput;

import java.util.Objects;

import static vapourdrive.agricultural_enhancements.AgriculturalEnhancements.seeds;

public class CropManagerContainer extends AbstractBaseMachineContainer {
    // gui position of the player inventory grid
    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 84;

    public static final int SEED_INV_XPOS = 80;
    public static final int SEED_INV_YPOS = 23;

    protected final CropManagerTile tileEntity;


    public CropManagerContainer(int windowId, Level world, BlockPos pos, Inventory inv, Player player, CropManagerData machineData) {
        super(windowId, world, pos, inv, player, Registration.CROP_MANAGER_CONTAINER.get(), machineData);
        tileEntity = (CropManagerTile) world.getBlockEntity(pos);

        //We use this vs the builtin method because we split all the shorts
        addSplitDataSlots(machineData);

        layoutPlayerInventorySlots(PLAYER_INVENTORY_XPOS, PLAYER_INVENTORY_YPOS);

        if (tileEntity != null) {
            tileEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(h -> {
                addSlot(new SlotFuel(h, 0, 8, 59));
                addSlot(new SlotFertilzer(h, 1, 32, 59));
                addSlot(new SlotOutput(h, 2, 56, 23));
                addSlot(new SlotOutput(h, 3, 56, 23 + 18));
                addSlot(new SlotOutput(h, 4, 56, 23 + 18 * 2));
                addSlot(new SlotSeed(h, 5, SEED_INV_XPOS, SEED_INV_YPOS));
                addSlot(new SlotSeed(h, 6, SEED_INV_XPOS + 18, SEED_INV_YPOS));
                addSlot(new SlotSeed(h, 7, SEED_INV_XPOS + (18 * 2), SEED_INV_YPOS));
                addSlot(new SlotSeed(h, 8, SEED_INV_XPOS + (18 * 3), SEED_INV_YPOS));
                addSlot(new SlotSeed(h, 9, SEED_INV_XPOS + (18 * 4), SEED_INV_YPOS));
                addSlot(new SlotSeed(h, 10, SEED_INV_XPOS, SEED_INV_YPOS + 18));
                addSlot(new SlotSeed(h, 11, SEED_INV_XPOS + 18, SEED_INV_YPOS + 18));
                addSlot(new SlotSeed(h, 12, SEED_INV_XPOS + (18 * 2), SEED_INV_YPOS + 18));
                addSlot(new SlotSeed(h, 13, SEED_INV_XPOS + (18 * 3), SEED_INV_YPOS + 18));
                addSlot(new SlotSeed(h, 14, SEED_INV_XPOS + (18 * 4), SEED_INV_YPOS + 18));
                addSlot(new SlotSeed(h, 15, SEED_INV_XPOS, SEED_INV_YPOS + (18 * 2)));
                addSlot(new SlotSeed(h, 16, SEED_INV_XPOS + 18, SEED_INV_YPOS + (18 * 2)));
                addSlot(new SlotSeed(h, 17, SEED_INV_XPOS + (18 * 2), SEED_INV_YPOS + (18 * 2)));
                addSlot(new SlotSeed(h, 18, SEED_INV_XPOS + (18 * 3), SEED_INV_YPOS + (18 * 2)));
                addSlot(new SlotSeed(h, 19, SEED_INV_XPOS + (18 * 4), SEED_INV_YPOS + (18 * 2)));
            });
        }
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return stillValid(ContainerLevelAccess.create(Objects.requireNonNull(tileEntity.getLevel()), tileEntity.getBlockPos()), playerEntity, Registration.CROP_MANAGER_BLOCK.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        AgriculturalEnhancements.debugLog("index: " + index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            //Seeds to Inventory
            if (index >= 41 && index <= 56) {
                AgriculturalEnhancements.debugLog("From furnace output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            //Non-output slots to Inventory
            if (index >= 36 && index <= 40) {
                AgriculturalEnhancements.debugLog("From furnace non-output");
                if (!this.moveItemStackTo(stack, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            }

            //Player Inventory
            else if (index <= 35) {
                //Inventory to fertilizer
                if (stack.is(Registration.FERTILISER.get())) {
                    if (!this.moveItemStackTo(stack, 37, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                //Inventory to seed slots
                else if (seeds.contains(stack.getItem())) {
                    if (!this.moveItemStackTo(stack, 41, 56, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                //Inventory to fuel
                else if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0.0) {
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

    public int getFertilizerStored(CropManagerData.Data data) {
        return this.machineData.get(data.ordinal());
    }

    public int getMaxFertilizer() {
        return this.tileEntity.getMaxFertilizer();
    }

    @OnlyIn(Dist.CLIENT)
    public float getFertilizerPercentage() {
        int i = this.machineData.get(CropManagerData.Data.FERTILIZER.ordinal());
        if (i == 0) {
            return 0;
        }
        return (float) i / (float) tileEntity.getMaxFertilizer();
    }
}

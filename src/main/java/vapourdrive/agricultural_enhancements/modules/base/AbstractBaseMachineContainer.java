package vapourdrive.agricultural_enhancements.modules.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractBaseMachineContainer extends AbstractContainerMenu {

    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 84;
    protected final AbstractBaseFuelUserTile tileEntity;
    protected final Player playerEntity;
    private final IItemHandler playerInventory;
    protected final Level world;
    protected final ContainerData machineData;

    public AbstractBaseMachineContainer(int windowId, Level world, BlockPos pos, Inventory inv, Player player, @Nullable MenuType<?> menu, ContainerData machineData) {
        super(menu, windowId);
        tileEntity = (AbstractBaseFuelUserTile) world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(inv);
        this.world = world;
        this.machineData = machineData;
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
        return true;
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

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
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

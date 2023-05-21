package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.base.AbstractBaseMachineBlock;

import javax.annotation.Nullable;


public class CropManagerBlock extends AbstractBaseMachineBlock {

    public CropManagerBlock() {
        super(BlockBehaviour.Properties.of(Material.STONE), 0.4f);
    }

    @Override
    protected boolean sneakWrenchMachine(Player player, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CropManagerTile machine) {
            machine.resetTillage();
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CropManagerTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        } else {
            return (level1, pos, state1, tile) -> {
                if (tile instanceof CropManagerTile machine) {
                    machine.tickServer(state1);
                }
            };
        }
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CropManagerTile machine) {
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable(AgriculturalEnhancements.MODID + ".crop_manager");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new CropManagerContainer(windowId, level, pos, playerInventory, playerEntity, machine.getMachineData());
                }
            };
            NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        } else {
            throw new IllegalStateException("Our named container provider is missing!");
        }
    }


    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileEntity = world.getBlockEntity(blockPos);
            if (tileEntity instanceof CropManagerTile machine) {
                AbstractBaseMachineBlock.dropContents(world, blockPos, machine.getItemHandler());
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    protected CompoundTag putAdditionalInfo(CompoundTag tag, BlockEntity blockEntity) {
        if (blockEntity instanceof CropManagerTile machine) {
            tag.putInt(AgriculturalEnhancements.MODID + ".fertilizer", machine.getCurrentFertilizer());
        }
        return tag;
    }
}

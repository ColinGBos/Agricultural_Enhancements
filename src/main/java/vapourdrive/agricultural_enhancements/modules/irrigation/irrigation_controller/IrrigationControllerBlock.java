package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.modules.base.AbstractBaseMachineBlock;

import javax.annotation.Nullable;

public class IrrigationControllerBlock extends AbstractBaseMachineBlock {

    public IrrigationControllerBlock() {
        super(Properties.of(Material.STONE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new IrrigationControllerTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        } else {
            return (level1, pos, state1, tile) -> {
                if (tile instanceof IrrigationControllerTile machine) {
                    machine.tickServer(state1);
                }
            };
        }
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IrrigationControllerTile machine) {
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable(AgriculturalEnhancements.MODID + ".irrigation_controller");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new IrrigationControllerContainer(windowId, level, pos, playerInventory, playerEntity, machine.getMachineData());
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
            if (tileEntity instanceof IrrigationControllerTile machine) {
                machine.changeSurroundingBlocks(state, 0);
                dropContents(world, blockPos, machine.getItemHandler());
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }
}

package vapourdrive.agricultural_enhancements.modules.irrigation.irrigation_controller;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;

import javax.annotation.Nullable;

import static net.minecraft.world.Containers.dropItemStack;

public class IrrigationControllerBlock extends FurnaceBlock implements EntityBlock {
    private Class<? extends BlockEntity> blockEntity;

    public IrrigationControllerBlock() {
        super(Properties.of(Material.STONE)
                .sound(SoundType.STONE)
                .strength(4.0f)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
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
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult trace) {
        if (!level.isClientSide) {
            openContainer(level, pos, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IrrigationControllerTile machine) {
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("screen." + AgriculturalEnhancements.MODID + ".harvester");
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
    public void onRemove(BlockState state, @NotNull Level world, @NotNull BlockPos blockPos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = world.getBlockEntity(blockPos);
            if (tileentity instanceof IrrigationControllerTile machine) {
                machine.changeSurroundingBlocks(state, 0);
                dropContents(world, blockPos, machine.getItemHandler());
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    private static void dropContents(Level world, BlockPos blockPos, IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            dropItemStack(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), handler.getStackInSlot(i));
        }

    }


    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

}

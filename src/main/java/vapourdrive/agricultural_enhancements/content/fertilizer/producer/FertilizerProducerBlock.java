package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineBlock;
import vapourdrive.vapourware.shared.utils.MachineUtils;

import javax.annotation.Nullable;


public class FertilizerProducerBlock extends AbstractBaseMachineBlock {

    public FertilizerProducerBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM), 0.2f);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new FertilizerProducerTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        } else {
            return (level1, pos, state1, tile) -> {
                if (tile instanceof FertilizerProducerTile machine) {
                    machine.tickServer(state1);
                }
            };
        }
    }

    @Override
    public boolean sneakWrenchMachine(Player player, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FertilizerProducerTile machine) {
            boolean poison = machine.ventElement();
            if (poison) {
                MachineUtils.playSound(level, pos, level.getRandom(), SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, 0f);
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 160));
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100));
            }
        }
        return true;
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof FertilizerProducerTile machine) {
            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable(AgriculturalEnhancements.MODID + ".fertilizer_producer");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity) {
                    return new FertilizerProducerContainer(windowId, level, pos, playerInventory, playerEntity, machine.getFertilizerProducerData());
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
            if (tileEntity instanceof FertilizerProducerTile machine) {
                AbstractBaseMachineBlock.dropContents(world, blockPos, machine.getItemHandler());
            }
            super.onRemove(state, world, blockPos, newState, isMoving);
        }
    }

    @Override
    protected CompoundTag putAdditionalInfo(CompoundTag tag, BlockEntity blockEntity) {
        if (blockEntity instanceof FertilizerProducerTile machine) {
            tag.putInt(AgriculturalEnhancements.MODID + ".n", machine.getCurrentElement(FertilizerProducerTile.Element.N));
            tag.putInt(AgriculturalEnhancements.MODID + ".p", machine.getCurrentElement(FertilizerProducerTile.Element.P));
            tag.putInt(AgriculturalEnhancements.MODID + ".k", machine.getCurrentElement(FertilizerProducerTile.Element.K));
        }
        return tag;
    }
}

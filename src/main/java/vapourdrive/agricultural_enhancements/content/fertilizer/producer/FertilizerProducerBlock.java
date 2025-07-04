package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.AbstractBaseMachineBlock;
import vapourdrive.vapourware.shared.utils.MachineUtils;

import javax.annotation.Nullable;


public class FertilizerProducerBlock extends AbstractBaseMachineBlock {

    public static final MapCodec<FertilizerProducerBlock> CODEC = simpleCodec(FertilizerProducerBlock::new);


    public FertilizerProducerBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASEDRUM), 0.2f);
    }

    public FertilizerProducerBlock(Properties properties) {
        super(properties, 0.2f);
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
        if (blockEntity instanceof FertilizerProducerTile) {
            player.openMenu((MenuProvider) blockEntity, pos);
        }
    }

    @Override
    protected ItemStack putAdditionalInfo(ItemStack stack, BlockEntity blockEntity) {
        super.putAdditionalInfo(stack, blockEntity);
        if (blockEntity instanceof FertilizerProducerTile machine) {
            stack.set(Registration.NITROGEN_DATA, machine.getCurrentElement(FertilizerProducerTile.Element.N));
            stack.set(Registration.PHOSPHORUS_DATA, machine.getCurrentElement(FertilizerProducerTile.Element.P));
            stack.set(Registration.POTASSIUM_DATA, machine.getCurrentElement(FertilizerProducerTile.Element.K));
        }
        return stack;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}

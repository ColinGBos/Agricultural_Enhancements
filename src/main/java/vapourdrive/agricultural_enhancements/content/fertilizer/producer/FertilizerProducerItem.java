package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.content.base.BaseMachineItem;

import javax.annotation.Nullable;
import java.util.List;

public class FertilizerProducerItem extends BaseMachineItem {
    public FertilizerProducerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("agriculturalenhancements.fertilizer_producer.info_1").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    protected List<Component> appendAdditionalTagInfo(List<Component> list, CompoundTag tag) {
        String n = df.format(tag.getInt(AgriculturalEnhancements.MODID + ".n"));
        list.add(Component.translatable("agriculturalenhancements.n", n));
        String p = df.format(tag.getInt(AgriculturalEnhancements.MODID + ".p"));
        list.add(Component.translatable("agriculturalenhancements.p", p));
        String k = df.format(tag.getInt(AgriculturalEnhancements.MODID + ".k"));
        list.add(Component.translatable("agriculturalenhancements.k", k));
        return list;
    }

    @Override
    protected void updateAdditional(BlockEntity blockentity, CompoundTag tag) {
        if (blockentity instanceof FertilizerProducerTile machine) {
            machine.addElement(FertilizerProducerTile.Element.N, tag.getInt(AgriculturalEnhancements.MODID + ".n"), false);
            machine.addElement(FertilizerProducerTile.Element.P, tag.getInt(AgriculturalEnhancements.MODID + ".p"), false);
            machine.addElement(FertilizerProducerTile.Element.K, tag.getInt(AgriculturalEnhancements.MODID + ".k"), false);
        }
        super.updateAdditional(blockentity, tag);
    }
}

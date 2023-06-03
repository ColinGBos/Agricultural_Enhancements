package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.BaseMachineItem;
import vapourdrive.vapourware.shared.utils.CompUtils;

import java.util.List;

public class FertilizerProducerItem extends BaseMachineItem {
    public FertilizerProducerItem(Block block, Properties properties) {
        super(block, properties, CompUtils.getComp(AgriculturalEnhancements.MODID, "fertilizer_producer.info_1"));
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

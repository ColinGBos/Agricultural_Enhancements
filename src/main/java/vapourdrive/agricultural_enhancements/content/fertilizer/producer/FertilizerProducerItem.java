package vapourdrive.agricultural_enhancements.content.fertilizer.producer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.agricultural_enhancements.setup.Registration;
import vapourdrive.vapourware.shared.base.BaseMachineItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.List;

public class FertilizerProducerItem extends BaseMachineItem {
    public FertilizerProducerItem(Block block, Properties properties) {
        super(block, properties, new DeferredComponent(AgriculturalEnhancements.MODID, "fertilizer_producer.info_1"));
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
    protected void updateAdditional(BlockEntity blockentity, ItemStack stack) {
        if (blockentity instanceof FertilizerProducerTile machine) {
            int nitrogen = stack.getOrDefault(Registration.NITROGEN_DATA, 0);
            int phosphorus = stack.getOrDefault(Registration.PHOSPHORUS_DATA, 0);
            int potassium = stack.getOrDefault(Registration.POTASSIUM_DATA, 0);

            machine.addElement(FertilizerProducerTile.Element.N, nitrogen, false);
            machine.addElement(FertilizerProducerTile.Element.P, phosphorus, false);
            machine.addElement(FertilizerProducerTile.Element.K, potassium, false);
        }
        super.updateAdditional(blockentity, stack);
    }
}

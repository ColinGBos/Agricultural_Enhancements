package vapourdrive.agricultural_enhancements.content.manager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.BaseMachineItem;
import vapourdrive.vapourware.shared.utils.DeferredComponent;

import java.util.List;

public class CropManagerItem extends BaseMachineItem {
    public CropManagerItem(Block block, Properties properties) {
        super(block, properties, new DeferredComponent(AgriculturalEnhancements.MODID, "crop_manager.info_1"));
    }

    @Override
    protected List<Component> appendAdditionalTagInfo(List<Component> list, CompoundTag tag) {
        String fertilizer = df.format(tag.getInt(AgriculturalEnhancements.MODID + ".fertilizer"));
        list.add(Component.translatable("agriculturalenhancements.fertilizer", fertilizer));
        return list;
    }

    @Override
    protected void updateAdditional(BlockEntity blockentity, CompoundTag tag) {
        if (blockentity instanceof CropManagerTile machine) {
            machine.addFertilizer(tag.getInt(AgriculturalEnhancements.MODID + ".fertilizer"), false);
        }
        super.updateAdditional(blockentity, tag);
    }
}

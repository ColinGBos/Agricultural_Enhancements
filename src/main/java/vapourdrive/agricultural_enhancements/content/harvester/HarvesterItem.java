package vapourdrive.agricultural_enhancements.content.harvester;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import vapourdrive.agricultural_enhancements.AgriculturalEnhancements;
import vapourdrive.vapourware.shared.base.BaseMachineItem;
import vapourdrive.vapourware.shared.utils.CompUtils;

import java.util.List;

public class HarvesterItem extends BaseMachineItem {
    public HarvesterItem(Block block, Properties properties) {
        super(block, properties, CompUtils.getComp(AgriculturalEnhancements.MODID, "harvester.info_1"));
    }

    @Override
    protected List<Component> appendAdditionalTagInfo(List<Component> list, CompoundTag tag) {
        list.add(Component.translatable("agriculturalenhancements.harvester.nondestructive_short." + tag.getBoolean(AgriculturalEnhancements.MODID + ".destructive")));
        return list;
    }

    @Override
    protected void updateAdditional(BlockEntity blockentity, CompoundTag tag) {
        if (blockentity instanceof HarvesterTile machine) {
            machine.setMode(tag.getBoolean(AgriculturalEnhancements.MODID + ".destructive"));
        }
        super.updateAdditional(blockentity, tag);
    }
}
